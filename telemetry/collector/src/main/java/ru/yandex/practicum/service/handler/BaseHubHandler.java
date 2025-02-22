package ru.yandex.practicum.service.handler;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.model.hub.HubEvent;

@Component
@RequiredArgsConstructor
public abstract class BaseHubHandler<T extends SpecificRecordBase> implements HubHandler {

    private final Config config;
    private final Producer producer;
    private static final String HUB_TOPIC = "telemetry.hubs.v1";

    protected abstract T mapToAvro(HubEvent event);

    public void handle(HubEvent event) {
        T avroEvent = mapToAvro(event);
        producer.send(HUB_TOPIC, event.getHubId(), avroEvent);
    }
}
