package kafka.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class KafkaTestHelper {

    private final String bootstrapServers;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KafkaTestHelper(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String randomGroupId() {
        return "test-group-" + UUID.randomUUID();
    }

    public void moveGroupToEnd(String topic, String groupId) {
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties(groupId))) {
            List<TopicPartition> partitions = topicPartitions(consumer, topic);

            consumer.assign(partitions);

            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);
            for (TopicPartition partition : partitions) {
                consumer.seek(partition, endOffsets.get(partition));
            }

            consumer.commitSync();
        }
    }

    public String readNewRawMessage(String topic, Runnable action) {
        String groupId = randomGroupId();

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties(groupId))) {
            List<TopicPartition> partitions = topicPartitions(consumer, topic);

            consumer.assign(partitions);

            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);
            for (TopicPartition partition : partitions) {
                consumer.seek(partition, endOffsets.get(partition));
            }

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
        } catch (Exception e) {
            throw new RuntimeException("Не удалось десериализовать сообщение из Kafka", e);
        }
    }

    private Properties consumerProperties(String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    private List<TopicPartition> topicPartitions(KafkaConsumer<String, String> consumer, String topic) {
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic, Duration.ofSeconds(5));

        if (partitionInfos == null || partitionInfos.isEmpty()) {
            throw new RuntimeException("Топик не найден или у него нет partition: " + topic);
        }

        return partitionInfos.stream()
                .map(info -> new TopicPartition(topic, info.partition()))
                .toList();
    }
}