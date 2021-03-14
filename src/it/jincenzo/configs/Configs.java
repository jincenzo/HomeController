package it.jincenzo.configs;

import java.io.Serializable;
import java.util.List;
/**
 * Class the represent the general configurations
 */
public class Configs implements Serializable{
	private List<DeviceConfig> devices;
	private List<TriggerConfig> triggers;
	/**
	 * Constructor
	 */
	public Configs(List<DeviceConfig> devices, List<TriggerConfig> triggers) {
		super();
		this.devices = devices;
		this.triggers = triggers;
	}
	
	
	
	public Configs() {
		super();
	}



	public List<DeviceConfig> getDevices() {
		return devices;
	}
	public List<TriggerConfig> getTriggers() {
		return triggers;
	}

	
	
	
}
