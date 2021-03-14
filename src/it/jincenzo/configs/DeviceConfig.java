package it.jincenzo.configs;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import it.jincenzo.core.remotedevice.DeviceType;

/**
 * Class representing a device configuration
 */
@JacksonXmlRootElement(localName = "device")
public class DeviceConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String ip;
	private DeviceType type;

	/**
	 * Constructor
	 */
	public DeviceConfig(String id, DeviceType type) {
		this(id, null, type);
	}

	public DeviceConfig() {
		super();
	}

	/**
	 * Constructor
	 */
	public DeviceConfig(String id, String ip, DeviceType type) {
		super();
		this.id = id;
		this.ip = ip;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public DeviceType getType() {
		return type;
	}

}
