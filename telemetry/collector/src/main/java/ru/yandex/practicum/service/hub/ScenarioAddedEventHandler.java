package ru.yandex.practicum.service.hub;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.model.hub.DeviceAction;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.HubEventType;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.ScenarioCondition;
import ru.yandex.practicum.service.handler.BaseHubHandler;

@Service
public class ScenarioAddedEventHandler extends BaseHubHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        try {
            if (event == null) {
                throw new IllegalArgumentException("Event cannot be null");
            }
            if (!(event instanceof ScenarioAddedEvent scenarioEvent)) {
                throw new IllegalArgumentException("Event must be of type ScenarioAddedEvent");
            }

            if (scenarioEvent.getName() == null || scenarioEvent.getConditions() == null || scenarioEvent.getActions() == null) {
                throw new IllegalArgumentException("Fields (name, conditions, actions) cannot be null");
            }

            return new ScenarioAddedEventAvro(
                    scenarioEvent.getName(),
                    scenarioEvent.getConditions().stream().map(this::mapScenarioConditionToAvro).toList(),
                    scenarioEvent.getActions().stream().map(this::mapDeviceActionToAvro).toList()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to map HubEvent to ScenarioAddedEventAvro", e);
        }
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    private ScenarioConditionAvro mapScenarioConditionToAvro(ScenarioCondition condition) {
        return new ScenarioConditionAvro(
                condition.getSensorId(),
                ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.valueOf(condition.getType().name()),
                ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro.valueOf(condition.getOperation().name()),
                condition.getValue()
        );
    }

    private DeviceActionAvro mapDeviceActionToAvro(DeviceAction action) {
        return new DeviceActionAvro(
                action.getSensorId(),
                ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro.valueOf(action.getType().name()),
                action.getValue()
        );
    }
}
