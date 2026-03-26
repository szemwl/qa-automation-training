package commerce.kafka.tests;

import commerce.event.OrderCreatedEvent;
import commerce.event.PaymentEvent;
import commerce.kafka.KafkaSettings;
import commerce.kafka.SimpleKafkaProducer;
import commerce.kafka.support.KafkaTestFacade;
import commerce.model.Order;
import commerce.model.OrderStatus;
import commerce.service.OrderService;
import commerce.service.PaymentEventConsumer;
import commerce.service.PaymentEventHandler;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Feature("Kafka")
@Tag("kafka")
@DisplayName("Kafka-тесты обработки заказов и оплат")
public class KafkaFlowTest extends BaseTest {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String ORDERS_TOPIC = "orders";
    private static final String PAYMENTS_TOPIC = "payments";
    private static final String PAYMENTS_DLQ_TOPIC = "payments.dlq";

    private final KafkaSettings settings = new KafkaSettings(BOOTSTRAP_SERVERS);
    private final KafkaTestFacade kafkaFacade = new KafkaTestFacade(settings);

    @Test
    @Story("Smoke")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Можно отправить и прочитать сообщение из Kafka")
    @Description("Проверка отправки и получения сообщения")
    void kafkaSmokeTest() {
        String expected = "smoke-" + UUID.randomUUID();

        try (SimpleKafkaProducer producer = new SimpleKafkaProducer(settings)) {
            String actual = kafkaFacade.readNewRawMessage(
                    ORDERS_TOPIC,
                    () -> producer.sendRaw(ORDERS_TOPIC, "key-" + UUID.randomUUID(), expected)
            );

            assertEquals(expected, actual);
        }
    }

    @Test
    @Story("Order creation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Заказ создаётся и событие уходит в Kafka")
    @Description("Проверка создания заказа и отправки события")
    void producerTest() {
        String orderId = "order-producer-" + UUID.randomUUID();

        try (SimpleKafkaProducer producer = new SimpleKafkaProducer(settings)) {
            OrderService orderService = new OrderService(orderStore, producer);

            OrderCreatedEvent eventFromKafka = kafkaFacade.readNewMessage(
                    ORDERS_TOPIC,
                    OrderCreatedEvent.class,
                    () -> orderService.createOrder(orderId)
            );

            Order storedOrder = orderStore.findById(orderId)
                    .orElseThrow(() -> new AssertionError("Заказ не сохранился в OrderStore"));

            assertEquals(OrderStatus.NEW, storedOrder.status());
            assertEquals(new OrderCreatedEvent(orderId, OrderStatus.NEW), eventFromKafka);
        }
    }

    @Test
    @Story("Payment processing")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Успешный PaymentEvent переводит заказ в статус PAID")
    @Description("Проверка смены статуса заказа на PAID при успешной обработке события оплаты")
    void consumerTest() {
        PaymentEventHandler handler = new PaymentEventHandler(orderStore, processedEventStore);

        String orderId = "order-paid-" + UUID.randomUUID();
        orderStore.save(new Order(orderId, OrderStatus.NEW));

        String groupId = kafkaFacade.createNewGroupAtEnd(PAYMENTS_TOPIC);

        try (SimpleKafkaProducer producer = new SimpleKafkaProducer(settings);
             PaymentEventConsumer paymentConsumer = new PaymentEventConsumer(settings, groupId, handler)) {

            PaymentEvent paymentEvent = new PaymentEvent(
                    "event-" + UUID.randomUUID(),
                    orderId,
                    true
            );

            producer.send(PAYMENTS_TOPIC, orderId, paymentEvent);
            paymentConsumer.processNextMessage(Duration.ofSeconds(10));

            Order updatedOrder = orderStore.findById(orderId)
                    .orElseThrow(() -> new AssertionError("Заказ не найден после обработки PaymentEventConsumer"));

            assertEquals(OrderStatus.PAID, updatedOrder.status());
        }
    }

    @Test
    @Story("DLQ")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Битый JSON отправляется в DLQ")
    @Description("Проверка отправки невалидного JSON-сообщения в payments.dlq")
    void negativeDlqTest() {
        PaymentEventHandler handler = new PaymentEventHandler(orderStore, processedEventStore);

        String brokenJson = "{\"eventId\":\"broken-1\",\"orderId\":\"123";

        String groupId = kafkaFacade.createNewGroupAtEnd(PAYMENTS_TOPIC);

        try (SimpleKafkaProducer producer = new SimpleKafkaProducer(settings);
             PaymentEventConsumer paymentConsumer = new PaymentEventConsumer(settings, groupId, handler)) {

            String messageFromDlq = kafkaFacade.readNewRawMessage(
                    PAYMENTS_DLQ_TOPIC,
                    () -> {
                        producer.sendRaw(PAYMENTS_TOPIC, "broken-key", brokenJson);
                        paymentConsumer.processNextMessage(Duration.ofSeconds(10));
                    }
            );

            assertEquals(brokenJson, messageFromDlq);
        }
    }

    @Test
    @Story("Idempotency")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Повторное событие не обрабатывается второй раз")
    @Description("Проверка, что событие с тем же eventId повторно не влияет на состояние заказа")
    void duplicateEventTest() {
        PaymentEventHandler handler = new PaymentEventHandler(orderStore, processedEventStore);

        String orderId = "order-duplicate-" + UUID.randomUUID();
        String eventId = "event-duplicate-" + UUID.randomUUID();
        orderStore.save(new Order(orderId, OrderStatus.NEW));

        int beforeCount = processedEventStore.processedEventsCount();
        String groupId = kafkaFacade.createNewGroupAtEnd(PAYMENTS_TOPIC);

        try (SimpleKafkaProducer producer = new SimpleKafkaProducer(settings);
             PaymentEventConsumer paymentConsumer = new PaymentEventConsumer(settings, groupId, handler)) {

            PaymentEvent duplicateEvent = new PaymentEvent(eventId, orderId, true);

            producer.send(PAYMENTS_TOPIC, orderId, duplicateEvent);
            paymentConsumer.processNextMessage(Duration.ofSeconds(10));

            producer.send(PAYMENTS_TOPIC, orderId, duplicateEvent);
            paymentConsumer.processNextMessage(Duration.ofSeconds(10));

            Order updatedOrder = orderStore.findById(orderId)
                    .orElseThrow(() -> new AssertionError("Заказ не найден после обработки дублей"));

            int afterCount = processedEventStore.processedEventsCount();

            assertEquals(OrderStatus.PAID, updatedOrder.status());
            assertTrue(processedEventStore.isEventProcessed(eventId));
            assertEquals(++beforeCount, afterCount);
        }
    }
}
