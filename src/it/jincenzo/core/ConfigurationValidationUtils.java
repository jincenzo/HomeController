package it.jincenzo.core;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import it.jincenzo.configs.ActionConfig;
import it.jincenzo.configs.Configs;
import it.jincenzo.configs.DeviceConfig;

public class ConfigurationValidationUtils {

	public static void validateConfigs(Configs configs) {
		
		List<String> declaredDevices = configs.getDevices().stream().map(DeviceConfig::getId).distinct().collect(Collectors.toList());
		
		if(declaredDevices.size()!=configs.getDevices().size()) {
			throw new IllegalArgumentException("Some device is duplicated in the declaration");		
		}
		
		List<String> deviceInTriggers = configs.getTriggers().stream().flatMap(t -> t.getActionSets().stream()).flatMap(s -> s.getActions().stream()).map(ActionConfig::getDeviceId).distinct().collect(Collectors.toList());
		
		
		Collection<String> devicesUsedButNotDeclared = CollectionUtils.subtract(deviceInTriggers, declaredDevices);
		if(!devicesUsedButNotDeclared.isEmpty()) {
			throw new IllegalArgumentException("Devices used but not declared found "+devicesUsedButNotDeclared);
		}
		
	}

}
