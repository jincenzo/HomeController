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

	public ActionExecutorResult handleTrigger(String triggerId) {
		Optional<TriggerConfig> triggerOpt = configs.getTriggers().stream()
				.filter(t -> t.getTriggerId().equals(triggerId)).findAny();
		if (triggerOpt.isPresent()) {
			try {
				triggerCalled(triggerOpt.get());
				return ActionExecutorResult.OK;
			} catch (Exception e) {
				return ActionExecutorResult.createErrorFeedBack(e);
			}
		} else {
			return ActionExecutorResult.NOT_FOUND;
		}
	}

	private void triggerCalled(TriggerConfig triggerConfig) throws RemoveDeviceCommunicationException {
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

	private void applySet(ActionSet set) throws RemoveDeviceCommunicationException {
		for (ActionConfig action : set.getActions()) {
			findDeviceById(action.getDeviceId()).applyAction(action);
		}

	}

	private boolean isSetAlreadyApplied(ActionSet set) throws RemoveDeviceCommunicationException {
		for (ActionConfig action : set.getActions()) {
			RemoteDevice device = findDeviceById(action.getDeviceId());
			if (!device.isCurrentActionAlreadyApplied(action)) {
				return false;
			}
		}
		return true;
	}

	private RemoteDevice findDeviceById(String deviceId) {
		return devices.stream().filter(d -> d.getId().equals(deviceId)).findAny().orElse(null);
	}

}
