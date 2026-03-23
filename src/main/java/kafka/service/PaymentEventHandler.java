package kafka.service;

import kafka.event.PaymentEvent;
import kafka.model.OrderStatus;
import kafka.store.OrderStore;

public class PaymentEventHandler {

    private final OrderStore orderStore;

    public PaymentEventHandler(OrderStore orderStore) {
        this.orderStore = orderStore;
    }

    public void handle(PaymentEvent event) {
        if (!orderStore.markEventProcessed(event.eventId())) {
            return;
        }

        if (event.success()) {
            orderStore.updateStatus(event.orderId(), OrderStatus.PAID);
        }
    }
}
