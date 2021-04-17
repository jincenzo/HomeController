package it.jincenzo.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.jincenzo.configs.ActionConfig;
import it.jincenzo.configs.ActionSet;
import it.jincenzo.configs.ConfigIO;
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

	public ActionExecutorResult handleTrigger(String triggerId, boolean longPress) {
		Optional<TriggerConfig> triggerOpt = configs.getTriggers().stream()
				.filter(t -> t.getTriggerId().equals(triggerId)).findAny();
		if (triggerOpt.isPresent()) {
			try {
				triggerCalled(triggerOpt.get(),longPress);
				return ActionExecutorResult.OK;
			} catch (Exception e) {
				return ActionExecutorResult.createErrorFeedBack(e);
			}
		} else {
			return ActionExecutorResult.NOT_FOUND;
		}
	}

	private void triggerCalled(TriggerConfig triggerConfig, boolean longPress) throws RemoveDeviceCommunicationException {

		if(longPress) {
			ActionSet longPressActionSet = triggerConfig.getActionSets().stream().filter(ActionSet::isLongPress).findAny().orElse(null);
			if(longPressActionSet!=null) {
				applySet(longPressActionSet);
			}
		}else {
			List<ActionSet> actionSets = triggerConfig.getActionSets().stream().filter(a -> !a.isLongPress()).collect(Collectors.toList());

			int lastAction = 0;
			if (lastActionMap.containsKey(triggerConfig.getTriggerId())) {
				lastAction = lastActionMap.get(triggerConfig.getTriggerId());
			}

			ActionSet set = actionSets.get(lastAction);
			if (!isSetAlreadyApplied(set)) {
				applySet(set);
				lastActionMap.put(triggerConfig.getTriggerId(), lastAction);
			} else {
				lastAction = (lastAction + 1) % actionSets.size();
				applySet(actionSets.get(lastAction));
				lastActionMap.put(triggerConfig.getTriggerId(), lastAction);
			}
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

	public String getStatusReport() {	
		StringBuilder sb = new StringBuilder();
		for(RemoteDevice device:devices) {
			sb.append(device.getId()).append("\n").append(device.statusReport()).append("\n");
		}
		return sb.toString();
	}

	public String getStatusConfigReport() throws JsonProcessingException {
		StringBuilder sb = new StringBuilder();
		for(RemoteDevice device:devices) {
			ActionConfig config = device.getCurrentStatusAsConfig();
			String configXml = ConfigIO.actionConfigToXML(config);
			sb.append(device.getId()).append("\n").append(configXml).append("\n");
		}
		return sb.toString();
	}

}
