package ru.yandex.practicum.service.handler;


import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.HubEventType;

public interface HubHandler {
    HubEventType getMessageType();

    void handle(HubEvent event);
}
