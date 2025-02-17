package ru.yandex.practicum.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorHandler<T extends SpecificRecordBase> implements SensorHandler {

    private final Config config;
    private final Producer producer;
    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";

    protected abstract T mapToAvro(SensorEventProto event);

    @Override
    public void handle(SensorEventProto event) {
        T protoEvent = mapToAvro(event);
        producer.send(SENSOR_TOPIC, event.getId(), protoEvent);
    }
}
