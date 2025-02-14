package ru.yandex.practicum.service.hub;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.kafkaConfig.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.HubEventType;
import ru.yandex.practicum.service.handler.BaseHubHandler;

@Service
public class DeviceRemovedEventHandler extends BaseHubHandler<DeviceRemovedEventAvro> {
    public DeviceRemovedEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected DeviceRemovedEventAvro mapToAvro(HubEvent event) {
        try {
            if (event == null) {
                throw new IllegalArgumentException("Event cannot be null");
            }
            if (!(event instanceof DeviceRemovedEvent deviceRemovedEvent)) {
                throw new IllegalArgumentException("Event must be of type DeviceRemovedEvent");
            }

            if (deviceRemovedEvent.getId() == null) {
                throw new IllegalArgumentException("Field (id) cannot be null");
            }

            return new DeviceRemovedEventAvro(deviceRemovedEvent.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to map HubEvent to DeviceRemovedEventAvro", e);
        }
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_REMOVED;
    }

}
