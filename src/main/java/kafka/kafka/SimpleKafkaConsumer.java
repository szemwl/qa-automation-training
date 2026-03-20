package kafka.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;

public class SimpleKafkaConsumer implements AutoCloseable {
    private final KafkaConsumer<String, String> consumer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SimpleKafkaConsumer(KafkaSettings settings, String groupId, String topic) {
        this.consumer = new KafkaConsumer<>(settings.consumerProperties(groupId));
        this.consumer.subscribe(Collections.singletonList(topic));
    }

    public void skipExistingMessages(Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();

        while (consumer.assignment().isEmpty() && System.currentTimeMillis() < deadline) {
            consumer.poll(Duration.ofMillis(200));
        }

        if (consumer.assignment().isEmpty()) {
            throw new RuntimeException("Не удалось получить assignment для consumer");
        }

        consumer.seekToEnd(consumer.assignment());
    }

    public String pollRawMessage(Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < deadline) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

            if (!records.isEmpty()) {
                return records.iterator().next().value();
            }
        }

        throw new RuntimeException("Сообщение не найдено в Kafka за время ожидания: " + timeout);
    }

    public <T> T pollMessage(Class<T> clazz, Duration timeout) {
        try {
            String rawMessage = pollRawMessage(timeout);
            return objectMapper.readValue(rawMessage, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось десериализовать сообщение из Kafka", e);
        }
    }

    @Override
    public void close() {
        consumer.close();
    }
}
