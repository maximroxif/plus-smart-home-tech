package ru.yandex.practicum.service.sensor;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.model.sensor.LightSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.service.handler.BaseEventHandler;

@Service
public class LightSensorEventHandler extends BaseEventHandler<LightSensorAvro> {

    public LightSensorEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected LightSensorAvro mapToAvro(SensorEvent event) {
        try {
            if (event == null) {
                throw new IllegalArgumentException("Event cannot be null");
            }
            if (!(event instanceof LightSensorEvent lightEvent)) {
                throw new IllegalArgumentException("Event must be of type LightSensorEvent");
            }

            if (lightEvent.getLinkQuality() == null || lightEvent.getLuminosity() == null) {
                throw new IllegalArgumentException("Fields (linkQuality, luminosity) cannot be null");
            }

            return new LightSensorAvro(
                    lightEvent.getLinkQuality(),
                    lightEvent.getLuminosity()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to map SensorEvent to LightSensorAvro", e);
        }
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}