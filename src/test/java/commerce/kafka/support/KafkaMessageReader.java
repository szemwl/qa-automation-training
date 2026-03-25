package commerce.kafka.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import commerce.kafka.KafkaSettings;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;

public class KafkaMessageReader {

    private final KafkaSettings settings;
    private final KafkaOffsetHelper offsetHelper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KafkaMessageReader(KafkaSettings settings, KafkaOffsetHelper offsetHelper) {
        this.settings = settings;
        this.offsetHelper = offsetHelper;
    }

    public String readNewRawMessage(String topic, Runnable action) {
        String groupId = offsetHelper.randomGroupId();

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(settings.consumerProperties(groupId))) {
            offsetHelper.assignToEnd(consumer, topic);
            action.run();
            long deadline = System.currentTimeMillis() + 10_000;

            while (System.currentTimeMillis() < deadline) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

                for (ConsumerRecord<String, String> record : records) {
                    return record.value();
                }
            }

            throw new RuntimeException("Сообщение не найдено в Kafka за время ожидания: PT10S");
        }
    }

    public <T> T readNewMessage(String topic, Class<T> clazz, Runnable action) {
        try {
            String rawMessage = readNewRawMessage(topic, action);
            return objectMapper.readValue(rawMessage, clazz);
        } catch (Exception ex) {
            throw new RuntimeException("Не удалось десериализовать сообщение из Kafka", ex);
        }
    }
}
