package kafka.service;

import kafka.store.OrderStore;
import kafka.event.OrderCreatedEvent;
import kafka.kafka.SimpleKafkaProducer;
import kafka.model.Order;
import kafka.model.OrderStatus;

public class OrderService {
    private static final String ORDERS_TOPIC = "orders";

    private final OrderStore orderStore;
    private final SimpleKafkaProducer producer;

    public OrderService(OrderStore orderStore, SimpleKafkaProducer producer) {
        this.orderStore = orderStore;
        this.producer = producer;
    }

    public Order createOrder(String orderId) {
        Order order = new Order(orderId, OrderStatus.NEW);
        orderStore.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, OrderStatus.NEW);
        producer.send(ORDERS_TOPIC, orderId, event);

        return order;
    }
}
