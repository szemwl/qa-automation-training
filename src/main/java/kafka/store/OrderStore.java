package kafka.store;

import kafka.model.Order;
import kafka.model.OrderStatus;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OrderStore {
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public void save(Order order) {
        orders.put(order.id(), order);
    }

    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    public void updateStatus(String orderId, OrderStatus newStatus) {
        Order existingOrder = orders.get(orderId);

        if (existingOrder == null) {
            throw new IllegalArgumentException("Заказ не найден: " + orderId);
        }

        orders.put(orderId, new Order(existingOrder.id(), newStatus));
    }

    public boolean markEventProcessed(String eventId) {
        return processedEventIds.add(eventId);
    }

    public boolean isEventProcessed(String eventId) {
        return processedEventIds.contains(eventId);
    }

    public int processedEventsCount() {
        return processedEventIds.size();
    }
}