package it.jincenzo.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;

import it.jincenzo.configs.ActionConfig;
import it.jincenzo.configs.ActionSet;
import it.jincenzo.configs.Configs;
import it.jincenzo.configs.DeviceConfig;
import it.jincenzo.configs.TriggerConfig;
import it.jincenzo.core.remotedevice.RemoteDevice;
import it.jincenzo.core.remotedevice.RemoveDeviceCommunicationException;

public class ActionExecutor {
	private Configs configs;
	private List<RemoteDevice> devices;
	private Map<String, Integer> lastActionMap;
	private RemoteDeviceFactory deviceFactory;

	public ActionExecutor(Configs configs) throws Exception {
		super();
		deviceFactory = new RemoteDeviceFactory();
		validateAndSetConfigs(configs);
	}

	public void validateAndSetConfigs(Configs configs) throws Exception {
		ConfigurationValidationUtils.validateConfigs(configs);
		this.configs = configs;
		setupDevices();

	}

	private void setupDevices() throws Exception {
		if (CollectionUtils.isNotEmpty(devices)) {
			devices.stream().forEach(RemoteDevice::disconnect);
		}
		devices = new ArrayList<>();
		lastActionMap = new HashMap<>();
		for (DeviceConfig config : configs.getDevices()) {
			devices.add(deviceFactory.create(config));
		}

	}

	public void handleTrigger(String triggerId) {
		Optional<TriggerConfig> triggerOpt = configs.getTriggers().stream()
				.filter(t -> t.getTriggerId().equals(triggerId)).findAny();
		if (triggerOpt.isPresent()) {
			triggerCalled(triggerOpt.get());
		}
	}

	private void triggerCalled(TriggerConfig triggerConfig) {
		int lastAction = 0;
		if (lastActionMap.containsKey(triggerConfig.getTriggerId())) {
			lastAction = lastActionMap.get(triggerConfig.getTriggerId());
		}

		ActionSet set = triggerConfig.getActionSets().get(lastAction);
		if (!isSetAlreadyApplied(set)) {
			applySet(set);
			lastActionMap.put(triggerConfig.getTriggerId(), lastAction);
		} else {
			lastAction = (lastAction + 1) % triggerConfig.getActionSets().size();
			applySet(triggerConfig.getActionSets().get(lastAction));
			lastActionMap.put(triggerConfig.getTriggerId(), lastAction);
		}
	}

	private void applySet(ActionSet set) {

		for (ActionConfig action : set.getActions()) {
			try {
				findDeviceById(action.getDeviceId()).applyAction(action);
			} catch (RemoveDeviceCommunicationException e) {
				e.printStackTrace();
			}
		}

	}

	private boolean isSetAlreadyApplied(ActionSet set) {

		for (ActionConfig action : set.getActions()) {
			RemoteDevice device = findDeviceById(action.getDeviceId());
			try {
				if (!device.isCurrentActionAlreadyApplied(action)) {
					return false;
				}
			} catch (RemoveDeviceCommunicationException e) {				
				e.printStackTrace();
				return false;
			}
		}

		return true;

	}

	private RemoteDevice findDeviceById(String deviceId) {
		return devices.stream().filter(d -> d.getId().equals(deviceId)).findAny().orElse(null);
	}

}
