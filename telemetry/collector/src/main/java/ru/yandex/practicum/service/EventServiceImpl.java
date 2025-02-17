package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.handler.BaseEventHandler;
import ru.yandex.practicum.service.handler.BaseHubHandler;
import ru.yandex.practicum.service.handler.HubHandler;
import ru.yandex.practicum.service.handler.SensorHandler;

import java.util.HashMap;
import java.util.Map;

@Getter
@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
	private Map<Enum<?>, BaseEventHandler<? extends SpecificRecordBase>> sensorEventHandlers = new HashMap<>();
	private Map<Enum<?>, BaseHubHandler<? extends SpecificRecordBase>> hubEventHandlers = new HashMap<>();


	@Override
	public void addSensorEvent(SensorEvent sensorEvent) {
		SensorHandler handler = sensorEventHandlers.get(sensorEvent.getType());
		if (handler == null) {
			throw new RuntimeException(sensorEvent.getType() + " does not exist");
		}
		handler.handle(sensorEvent);
	}

	@Override
	public void addHubEvent(HubEvent hubEvent) {
		HubHandler handler = hubEventHandlers.get(hubEvent.getType());
		if (handler == null) {
			throw new RuntimeException(hubEvent.getType() + " does not exist");
		}
		handler.handle(hubEvent);
	}
}
