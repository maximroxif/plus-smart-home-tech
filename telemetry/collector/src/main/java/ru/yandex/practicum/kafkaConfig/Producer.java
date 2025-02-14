package ru.yandex.practicum.kafkaConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
@Slf4j
public class Producer {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    public <T extends SpecificRecordBase> void send(String topic, String key, T event) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, key, event);
        log.info("Sending an event to topic: {} with key: {} Event: {}", topic, key, event);

        try {
            Future<RecordMetadata> future = kafkaProducer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Error when posting a message to a topic: {}", topic, exception);
                } else {
                    log.info("Message successfully posted in topic: {}, partition: {}, offset: {}",
                            metadata.topic(), metadata.partition(), metadata.offset());
                }
            });
        } catch (Exception e) {
            log.error("Error when posting a message to a topic: {}", topic, e);
            throw new RuntimeException("Error when sending a message in Kafka", e);
        }
    }

    public void close() {
        kafkaProducer.close();
    }
}