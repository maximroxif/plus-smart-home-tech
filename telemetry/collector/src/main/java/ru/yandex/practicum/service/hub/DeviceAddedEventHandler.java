package ru.yandex.practicum.service.hub;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.HubEventType;
import ru.yandex.practicum.service.handler.BaseHubHandler;

@Service
public class DeviceAddedEventHandler extends BaseHubHandler<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(Config config, Producer producer) {
        super(config, producer);
    }


    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEvent event) {
        try {
            if (event == null) {
                throw new IllegalArgumentException("Event cannot be null");
            }
            if (!(event instanceof DeviceAddedEvent deviceAddedEvent)) {
                throw new IllegalArgumentException("Event must be of type DeviceAddedEvent");
            }

            if (deviceAddedEvent.getId() == null || deviceAddedEvent.getDeviceType() == null) {
                throw new IllegalArgumentException("Fields (id, deviceType) cannot be null");
            }

            return new DeviceAddedEventAvro(
                    deviceAddedEvent.getId(),
                    DeviceTypeAvro.valueOf(deviceAddedEvent.getDeviceType().name())
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to map HubEvent to DeviceAddedEventAvro", e);
        }
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }
}
