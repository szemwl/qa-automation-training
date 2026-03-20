package kafka.event;

import kafka.model.OrderStatus;

public record OrderCreatedEvent(
        String orderId,
        OrderStatus status
) {
}
