package it.jincenzo.configs;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Class representing an action
 */
@JsonInclude(Include.NON_EMPTY)
public class ActionConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	private String deviceId;
	private boolean power;
	private Integer brightness;
	private String rgb;
	private Integer saturation;
	private Integer hue;
	private Integer colorTemparature;
	private ColorMode colorMode;
		

	public ColorMode getColorMode() {
		return colorMode;
	}

	public void setColorMode(ColorMode colorMode) {
		this.colorMode = colorMode;
	}

	public Integer getSaturation() {
		return saturation;
	}

	public void setSaturation(Integer saturation) {
		this.saturation = saturation;
	}

	public Integer getHue() {
		return hue;
	}

	public void setHue(Integer hue) {
		this.hue = hue;
	}

	public Integer getColorTemparature() {
		return colorTemparature;
	}

	public void setColorTemparature(Integer colorTemparature) {
		this.colorTemparature = colorTemparature;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void setPower(boolean power) {
		this.power = power;
	}

	public void setBrightness(Integer brightness) {
		this.brightness = brightness;
	}

	public void setRgb(String rgb) {
		this.rgb = rgb;
	}

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



	public boolean isEquivalent(ActionConfig action) {
		if(!action.power && !power) {
			return true;
		}
		if(action.power != power) {
			return false;
		}
		if(action.colorMode!=colorMode) {
			return false;
		}
		
		switch (colorMode) {
		case COLOR:
			return action.getBrightness().equals(brightness ) && action.rgb.equals(rgb);
		case COLOR_TEMPERATURE:
			return action.getColorTemparature().equals(colorTemparature) && action.getBrightness().equals(brightness);
		case HSV:
			return action.getHue().equals(hue) && action.saturation.equals(saturation);
		}
		
		return false;
	}

	
	

}
