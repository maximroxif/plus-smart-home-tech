package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {
	@NotNull
	private String id;

	@Override
	public HubEventType getType() {
		return HubEventType.DEVICE_REMOVED;
	}
}
