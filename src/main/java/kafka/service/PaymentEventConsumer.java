package kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.event.PaymentEvent;
import kafka.kafka.KafkaSettings;
import kafka.kafka.SimpleKafkaConsumer;
import kafka.kafka.SimpleKafkaProducer;

import java.time.Duration;
import java.util.UUID;

public class PaymentEventConsumer implements AutoCloseable {

    private static final String PAYMENTS_TOPIC = "payments";
    private static final String PAYMENTS_DLQ_TOPIC = "payments.dlq";

    private final SimpleKafkaConsumer consumer;
    private final SimpleKafkaProducer producer;
    private final PaymentEventHandler paymentEventHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentEventConsumer(KafkaSettings settings, String groupId, PaymentEventHandler paymentEventHandler) {
        this.consumer = new SimpleKafkaConsumer(settings, groupId, PAYMENTS_TOPIC);
        this.producer = new SimpleKafkaProducer(settings);
        this.paymentEventHandler = paymentEventHandler;
    }

    public void processNextMessage(Duration timeout) {
        String rawMessage = consumer.pollRawMessage(timeout);
        try {
            PaymentEvent event = objectMapper.readValue(rawMessage, PaymentEvent.class);
            paymentEventHandler.handle(event);
        } catch (Exception e) {
            producer.sendRaw(PAYMENTS_DLQ_TOPIC, "dlq-" + UUID.randomUUID(), rawMessage);
        }

    }

    @Override
    public void close() {
        consumer.close();
        producer.close();
    }
}
