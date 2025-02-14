package ru.yandex.practicum.service.hub;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.HubEventType;
import ru.yandex.practicum.model.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.service.handler.BaseHubHandler;


@Service
public class ScenarioRemovedEventHandler extends BaseHubHandler<ScenarioRemovedEventAvro> {
    public ScenarioRemovedEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected ScenarioRemovedEventAvro mapToAvro(HubEvent event) {
        if (!(event instanceof ScenarioRemovedEvent scenarioRemovedEvent)) {
            throw new IllegalArgumentException("Invalid event type or null value received.");
        }

        try {
            return new ScenarioRemovedEventAvro(scenarioRemovedEvent.getName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert scenario removal event to Avro", e);
        }
    }


    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
