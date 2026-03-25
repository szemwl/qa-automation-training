package commerce.kafka.support;

import commerce.kafka.KafkaSettings;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KafkaOffsetHelper {

    private final KafkaSettings settings;

    public KafkaOffsetHelper(KafkaSettings settings) {
        this.settings = settings;
    }

    public String randomGroupId() {
        return "test-group-" + UUID.randomUUID();
    }

    public void moveGroupToEnd(String topic, String groupId) {
        try (KafkaConsumer<String, String> consumer = createConsumer(groupId)) {
            assignToEnd(consumer, topic);
            consumer.commitSync();
        }
    }

    KafkaConsumer<String, String> createConsumer(String groupId) {
        return new KafkaConsumer<>(settings.consumerProperties(groupId));
    }

    void assignToEnd(KafkaConsumer<String, String> consumer, String topic) {
        List<TopicPartition> partitions = topicPartitions(consumer, topic);
        consumer.assign(partitions);

        Map<TopicPartition, Long> endOffset = consumer.endOffsets(partitions);
        for (TopicPartition partition : partitions) {
            consumer.seek(partition, endOffset.get(partition));
        }
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
