package kafka.tests;

import kafka.event.OrderCreatedEvent;
import kafka.event.PaymentEvent;
import kafka.kafka.KafkaSettings;
import kafka.kafka.SimpleKafkaProducer;
import kafka.model.Order;
import kafka.model.OrderStatus;
import kafka.service.OrderService;
import kafka.service.PaymentEventConsumer;
import kafka.store.OrderStore;
import kafka.support.KafkaTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("Тесты товаров")
public class KafkaFlowTest {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String ORDERS_TOPIC = "orders";
    private static final String PAYMENTS_TOPIC = "payments";
    private static final String PAYMENTS_DLQ_TOPIC = "payments.dlq";

    private final KafkaSettings settings = new KafkaSettings(BOOTSTRAP_SERVERS);
    private final KafkaTestHelper kafkaHelper = new KafkaTestHelper(BOOTSTRAP_SERVERS);

    @Test
    @DisplayName("Kafka smoke - можно отправить и прочитать сообщение")
    void kafkaSmokeTest() {
        String expected = "smoke-" + UUID.randomUUID();

        try (SimpleKafkaProducer producer = new SimpleKafkaProducer(settings)) {
            String actual = kafkaHelper.readNewRawMessage(
                    ORDERS_TOPIC,
                    () -> producer.sendRaw(ORDERS_TOPIC, "key-" + UUID.randomUUID(), expected)
            );

            assertEquals(expected, actual);
        }
    }

    @Test
    @DisplayName("Producer test - заказ создаётся и событие уходит в Kafka")
    void producerTest() {
        OrderStore orderStore = new OrderStore();
        String orderId = "order-producer-" + UUID.randomUUID();

        try (SimpleKafkaProducer producer = new SimpleKafkaProducer(settings)) {
            OrderService orderService = new OrderService(orderStore, producer);

            OrderCreatedEvent eventFromKafka = kafkaHelper.readNewMessage(
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
    @DisplayName("Consumer test - payment success меняет заказ на PAID")
    void consumerTest() {
        OrderStore orderStore = new OrderStore();
        String orderId = "order-paid-" + UUID.randomUUID();
        String groupId = kafkaHelper.randomGroupId();

        orderStore.save(new Order(orderId, OrderStatus.NEW));
        kafkaHelper.moveGroupToEnd(PAYMENTS_TOPIC, groupId);

        try (SimpleKafkaProducer producer = new SimpleKafkaProducer(settings);
             PaymentEventConsumer paymentConsumer = new PaymentEventConsumer(settings, groupId, orderStore)) {

            PaymentEvent paymentEvent = new PaymentEvent(
                    "event-" + UUID.randomUUID(),
                    orderId,
                    true
            );

            producer.send(PAYMENTS_TOPIC, orderId, paymentEvent);
            paymentConsumer.processNextMessage(Duration.ofSeconds(10));

            Order updatedOrder = orderStore.findById(orderId)
                    .orElseThrow(() -> new AssertionError("Заказ не найден после обработки payment event"));

            assertEquals(OrderStatus.PAID, updatedOrder.status());
        }
    }

    @Test
    @DisplayName("Negative test - битый JSON уходит в DLQ")
    void negativeDlqTest() {
        OrderStore orderStore = new OrderStore();
        String groupId = kafkaHelper.randomGroupId();
        String brokenJson = "{\"eventId\":\"broken-1\",\"orderId\":\"123\",\"success\":tru";

        kafkaHelper.moveGroupToEnd(PAYMENTS_TOPIC, groupId);

        try (SimpleKafkaProducer producer = new SimpleKafkaProducer(settings);
             PaymentEventConsumer paymentConsumer = new PaymentEventConsumer(settings, groupId, orderStore)) {

            String messageFromDlq = kafkaHelper.readNewRawMessage(
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
    @DisplayName("Duplicate test - повторное событие не обрабатывается второй раз")
    void duplicateEventTest() {
        OrderStore orderStore = new OrderStore();
        String orderId = "order-duplicate-" + UUID.randomUUID();
        String eventId = "event-duplicate-" + UUID.randomUUID();
        String groupId = kafkaHelper.randomGroupId();

        orderStore.save(new Order(orderId, OrderStatus.NEW));
        kafkaHelper.moveGroupToEnd(PAYMENTS_TOPIC, groupId);

        try (SimpleKafkaProducer producer = new SimpleKafkaProducer(settings);
             PaymentEventConsumer paymentConsumer = new PaymentEventConsumer(settings, groupId, orderStore)) {

            PaymentEvent duplicateEvent = new PaymentEvent(eventId, orderId, true);

            producer.send(PAYMENTS_TOPIC, orderId, duplicateEvent);
            paymentConsumer.processNextMessage(Duration.ofSeconds(10));

            producer.send(PAYMENTS_TOPIC, orderId, duplicateEvent);
            paymentConsumer.processNextMessage(Duration.ofSeconds(10));

            Order updatedOrder = orderStore.findById(orderId)
                    .orElseThrow(() -> new AssertionError("Заказ не найден после обработки дублей"));

            assertEquals(OrderStatus.PAID, updatedOrder.status());
            assertTrue(orderStore.isEventProcessed(eventId));
            assertEquals(1, orderStore.processedEventsCount());
        }
    }
}
