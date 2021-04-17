package it.jincenzo.core.remotedevice;

import it.jincenzo.configs.ActionConfig;
import it.jincenzo.configs.DeviceConfig;

public abstract class RemoteDevice {
	protected DeviceConfig deviceConfig;	

	public RemoteDevice(DeviceConfig deviceConfig) {
		super();
		this.deviceConfig = deviceConfig;
	}

	public abstract boolean isCurrentActionAlreadyApplied(ActionConfig action) throws RemoveDeviceCommunicationException ;	
	public abstract void applyAction(ActionConfig action) throws RemoveDeviceCommunicationException ;
	public abstract void disconnect();

	public String getId() {
		return deviceConfig.getId();
	}

	public abstract String statusReport();

	public abstract ActionConfig getCurrentStatusAsConfig();
}
