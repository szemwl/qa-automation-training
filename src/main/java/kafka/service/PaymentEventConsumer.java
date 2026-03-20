package kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.store.OrderStore;
import kafka.event.PaymentEvent;
import kafka.kafka.KafkaSettings;
import kafka.kafka.SimpleKafkaConsumer;
import kafka.kafka.SimpleKafkaProducer;
import kafka.model.OrderStatus;

import java.time.Duration;
import java.util.UUID;

public class PaymentEventConsumer implements AutoCloseable {
    private static final String PAYMENTS_TOPIC = "payments";
    private static final String PAYMENTS_DLQ_TOPIC = "payments.dlq";

    private final OrderStore orderStore;
    private final SimpleKafkaConsumer consumer;
    private final SimpleKafkaProducer producer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentEventConsumer(KafkaSettings settings, String groupId, OrderStore orderStore) {
        this.orderStore = orderStore;
        this.consumer = new SimpleKafkaConsumer(settings, groupId, PAYMENTS_TOPIC);
        this.producer = new SimpleKafkaProducer(settings);
    }

    public void processNextMessage(Duration timeout) {
        String rawMessage = consumer.pollRawMessage(timeout);

        PaymentEvent event;
        try {
            event = objectMapper.readValue(rawMessage, PaymentEvent.class);
        } catch (Exception e) {
            producer.sendRaw(PAYMENTS_DLQ_TOPIC, "dlq-" + UUID.randomUUID(), rawMessage);
            return;
        }

        boolean firstProcessing = orderStore.markEventProcessed(event.eventId());
        if (!firstProcessing) {
            return;
        }

        if (event.success()) {
            orderStore.updateStatus(event.orderId(), OrderStatus.PAID);
        }
    }

    @Override
    public void close() {
        consumer.close();
        producer.close();
    }
}
