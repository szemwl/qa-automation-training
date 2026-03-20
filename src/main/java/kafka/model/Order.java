package kafka.model;

public record Order(
        String id,
        OrderStatus status
) {
}
