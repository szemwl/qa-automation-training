package kafka.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class SimpleKafkaProducer implements AutoCloseable {
    private final KafkaProducer<String, String> producer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SimpleKafkaProducer(KafkaSettings settings) {
        this.producer = new KafkaProducer<>(settings.producerProperties());
    }

    public void send(String topic, String key, Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            sendRaw(topic, key, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Не удалось сериализовать сообщение в JSON", e);
        }
    }

    public void sendRaw(String topic, String key, String message) {
        producer.send(new ProducerRecord<>(topic, key, message));
        producer.flush();
    }

    @Override
    public void close() {
        producer.close();
    }
}