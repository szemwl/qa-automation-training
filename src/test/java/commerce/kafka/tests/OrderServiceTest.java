package commerce.kafka.tests;

import commerce.event.OrderCreatedEvent;
import commerce.kafka.SimpleKafkaProducer;
import commerce.model.Order;
import commerce.model.OrderStatus;
import commerce.service.OrderService;
import commerce.store.OrderStore;
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

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderStore orderStore;

    @Mock
    private SimpleKafkaProducer kafkaProducer;

    @InjectMocks
    private OrderService orderService;

    @Test
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
    void shouldSendOrderCreatedEventToKafka() {
        orderService.createOrder("123");

        verify(kafkaProducer).send(
                eq("orders"),
                eq("123"),
                any(OrderCreatedEvent.class)
        );
    }

    @Test
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
