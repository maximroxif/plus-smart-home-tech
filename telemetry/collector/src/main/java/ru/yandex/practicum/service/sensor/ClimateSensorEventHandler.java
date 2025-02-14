package ru.yandex.practicum.service.sensor;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.kafkaConfig.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.service.handler.BaseEventHandler;

@Service
public class ClimateSensorEventHandler extends BaseEventHandler<ClimateSensorAvro> {

    public ClimateSensorEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected ClimateSensorAvro mapToAvro(SensorEvent event) {
        try {
            if (event == null) {
                throw new IllegalArgumentException("Event cannot be null");
            }
            if (!(event instanceof ClimateSensorEvent climateEvent)) {
                throw new IllegalArgumentException("Event must be of type ClimateSensorEvent");
            }

            if (climateEvent.getTemperatureC() == null || climateEvent.getHumidity() == null || climateEvent.getCo2Level() == null) {
                throw new IllegalArgumentException("Fields (temperatureC, humidity, co2Level) cannot be null");
            }

            return new ClimateSensorAvro(
                    climateEvent.getTemperatureC(),
                    climateEvent.getHumidity(),
                    climateEvent.getCo2Level()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to map SensorEvent to ClimateSensorAvro", e);
        }
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}

