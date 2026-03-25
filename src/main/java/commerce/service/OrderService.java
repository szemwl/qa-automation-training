package commerce.service;

import commerce.event.OrderCreatedEvent;
import commerce.kafka.SimpleKafkaProducer;
import commerce.model.Order;
import commerce.model.OrderStatus;
import commerce.store.OrderStore;

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
