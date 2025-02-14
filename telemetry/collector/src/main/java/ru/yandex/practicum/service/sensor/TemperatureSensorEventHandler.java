package ru.yandex.practicum.service.sensor;

import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.kafkaConfig.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.service.handler.BaseEventHandler;

public class TemperatureSensorEventHandler extends BaseEventHandler<TemperatureSensorAvro> {
    public TemperatureSensorEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected TemperatureSensorAvro mapToAvro(SensorEvent event) {
        try {
            if (event == null) {
                throw new IllegalArgumentException("Event cannot be null");
            }
            if (!(event instanceof TemperatureSensorEvent tempEvent)) {
                throw new IllegalArgumentException("Event must be of type TemperatureSensorEvent");
            }

            return new TemperatureSensorAvro(
                    tempEvent.getTemperatureC(),
                    tempEvent.getTemperatureF()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to map SensorEvent to TemperatureSensorAvro", e);
        }
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

}
