package kafka.event;

public record PaymentEvent(
        String eventId,
        String orderId,
        boolean success
) {
}
