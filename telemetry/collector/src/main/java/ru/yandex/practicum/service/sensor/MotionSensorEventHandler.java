package ru.yandex.practicum.service.sensor;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.service.handler.BaseEventHandler;

@Service
public class MotionSensorEventHandler extends BaseEventHandler<MotionSensorAvro> {
    public MotionSensorEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected MotionSensorAvro mapToAvro(SensorEvent event) {
        try {
            if (!(event instanceof MotionSensorEvent motionEvent)) {
                throw new IllegalArgumentException("Event must be of type MotionSensorEvent");
            }

            if (motionEvent.getLinkQuality() == null || motionEvent.getVoltage() == null) {
                throw new IllegalArgumentException("Event fields (linkQuality, voltage) cannot be null");
            }

            return new MotionSensorAvro(
                    motionEvent.getLinkQuality(),
                    motionEvent.isMotion(),
                    motionEvent.getVoltage()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to map SensorEvent to MotionSensorAvro", e);
        }
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
