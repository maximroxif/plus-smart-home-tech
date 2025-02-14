package ru.yandex.practicum.service.handler;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.model.sensor.SensorEvent;

@RequiredArgsConstructor
@Component
public abstract class BaseEventHandler<T extends SpecificRecordBase> implements SensorHandler {

    private final Config config;
    private final Producer producer;
    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";

    protected abstract T mapToAvro(SensorEvent event);

    public void handle(SensorEvent event) {

        T avroEvent = mapToAvro(event);
        producer.send(SENSOR_TOPIC, event.getId(), avroEvent);
    }
}
