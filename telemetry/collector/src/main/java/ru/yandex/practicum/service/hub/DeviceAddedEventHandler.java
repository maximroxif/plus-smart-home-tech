package ru.yandex.practicum.service.hub;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.service.handler.BaseHubHandler;


@Service
public class DeviceAddedEventHandler extends BaseHubHandler<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(Config config, Producer producer) {
        super(config, producer);
    }


    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEventProto event) {
        try {
            if (event == null) {
                throw new IllegalArgumentException("Event cannot be null");
            }
            throw new IllegalArgumentException("Event must be of type DeviceAddedEvent");

        } catch (Exception e) {
            throw new RuntimeException("Failed to map HubEvent to DeviceAddedEventAvro", e);
        }
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }
}
