package commerce.kafka.tests;

import commerce.event.OrderCreatedEvent;
import commerce.kafka.SimpleKafkaProducer;
import commerce.model.Order;
import commerce.model.OrderStatus;
import commerce.service.OrderService;
import commerce.store.OrderStore;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Feature("Управление заказами")
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Юнит тесты на бизнес-логику OrderService")
class OrderServiceTest {

    @Mock
    private OrderStore orderStore;

    @Mock
    private SimpleKafkaProducer kafkaProducer;

    @InjectMocks
    private OrderService orderService;

    @Test
    @Story("Создание заказа")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет, что новый заказ создаётся и сохраняет его в БД")
    @DisplayName("Создаёт новый заказ и сохраняет его в хранилище")
    void shouldCreateOrderAndSaveItToStore() {
        Order result = orderService.createOrder("123");

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderStore).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();

        assertEquals("123", savedOrder.id());
        assertEquals(OrderStatus.NEW, savedOrder.status());

        assertEquals("123", result.id());
        assertEquals(OrderStatus.NEW, result.status());
    }

    @Test
    @Story("Создание заказа")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет, что в Kafka отправляется событие о создании заказа")
    @DisplayName("Отправляет событие о создании заказа в Kafka")
    void shouldSendOrderCreatedEventToKafka() {
        orderService.createOrder("123");

        verify(kafkaProducer).send(
                eq("orders"),
                eq("123"),
                any(OrderCreatedEvent.class)
        );
    }

    @Test
    @Story("Создание заказа")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет, что заказ сначала сохраняется в БД, а затем отправляется в Kafka")
    @DisplayName("Сначала сохраняет заказ, затем отправляет событие в Kafka")
    void shouldSaveOrderBeforeSendingEvent() {
        orderService.createOrder("123");

        InOrder inOrder = inOrder(orderStore, kafkaProducer);
        inOrder.verify(orderStore).save(any(Order.class));
        inOrder.verify(kafkaProducer).send(
                eq("orders"),
                eq("123"),
                any(OrderCreatedEvent.class)
        );
    }

    @Test
    @Story("Создание заказа")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Проверяет, что если сохранение заказа упало с ошибкой, событие в Kafka не отправляется")
    @DisplayName("Не отправляет событие в Kafka, если сохранение заказа упало")
    void shouldNotSendEventIfStoreSaveFailed() {
        doThrow(new RuntimeException("DB error"))
                .when(orderStore)
                .save(any(Order.class));

        assertThrows(RuntimeException.class, () -> orderService.createOrder("123"));

        verify(kafkaProducer, never()).send(
                eq("orders"),
                eq("123"),
                any(OrderCreatedEvent.class)
        );
    }
}
