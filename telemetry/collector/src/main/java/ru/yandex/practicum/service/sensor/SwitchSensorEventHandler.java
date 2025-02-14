package ru.yandex.practicum.service.sensor;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.kafkaConfig.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.service.handler.BaseEventHandler;

@Service
public class SwitchSensorEventHandler extends BaseEventHandler<SwitchSensorAvro> {
    public SwitchSensorEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected SwitchSensorAvro mapToAvro(SensorEvent event) {
        try {
            if (!(event instanceof SwitchSensorEvent switchEvent)) {
                throw new IllegalArgumentException("Event must be of type SwitchSensorEvent");
            }

            return new SwitchSensorAvro(switchEvent.isState());
        } catch (Exception e) {
            throw new RuntimeException("Failed to map SensorEvent to SwitchSensorAvro", e);
        }
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

}
