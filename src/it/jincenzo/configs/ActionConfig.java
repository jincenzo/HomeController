package it.jincenzo.configs;

import java.io.Serializable;

/**
 * Class representing an action
 */
public class ActionConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	private String deviceId;
	private boolean power;
	private Integer brightness;
	private String rgb;

	/**
	 * Constructor
	 */
	public ActionConfig(String deviceId, boolean power, Integer brightness, String rgb) {
		super();
		this.deviceId = deviceId;
		this.power = power;
		this.brightness = brightness;
		this.rgb = rgb;
	}

	/**
	 * 
	 */
	public ActionConfig() {
		super();
	}

	public String getDeviceId() {
		return deviceId;
	}

	public boolean isPower() {
		return power;
	}

	public Integer getBrightness() {
		return brightness;
	}

	public String getRgb() {
		return rgb;
	}

}
