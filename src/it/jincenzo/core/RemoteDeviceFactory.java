package it.jincenzo.core;

import it.jincenzo.configs.DeviceConfig;
import it.jincenzo.core.remotedevice.DeviceType;
import it.jincenzo.core.remotedevice.RemoteDevice;
import it.jincenzo.core.remotedevice.YeelightRemoteDevice;

public class RemoteDeviceFactory {
	
	public RemoteDevice create(DeviceConfig config) throws Exception {
		if(config.getType().equals(DeviceType.YEELIGHT)) {
			return new YeelightRemoteDevice(config);
		}
		
		throw new IllegalArgumentException("Device type "+config.getType()+" not supported");
	}
}
