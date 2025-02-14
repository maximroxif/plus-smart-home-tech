package ru.yandex.practicum.service.handler;


import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;

public interface SensorHandler {
    SensorEventType getMessageType();

    void handle(SensorEvent event);
}
