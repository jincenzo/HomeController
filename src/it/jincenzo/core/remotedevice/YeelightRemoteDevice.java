package it.jincenzo.core.remotedevice;

import java.awt.Color;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.mollin.yapi.YeelightDevice;
import com.mollin.yapi.enumeration.YeelightEffect;
import com.mollin.yapi.enumeration.YeelightProperty;
import com.mollin.yapi.exception.YeelightResultErrorException;
import com.mollin.yapi.exception.YeelightSocketException;
import com.mollin.yapi.utils.YeelightUtils;

import it.jincenzo.configs.ActionConfig;
import it.jincenzo.configs.ColorMode;
import it.jincenzo.configs.DeviceConfig;

public class YeelightRemoteDevice extends RemoteDevice {
	private YeelightDevice device;

	public YeelightRemoteDevice(DeviceConfig deviceConfig) throws Exception {
		super(deviceConfig);
		init();

	}

	private void init() throws YeelightSocketException {
		device = new YeelightDevice(deviceConfig.getIp());		
	}

	@Override
	public boolean isCurrentActionAlreadyApplied(ActionConfig action) throws RemoveDeviceCommunicationException {
		try {
			return isCurrentActionAlreadyAppliedImpl(action);
		} catch (YeelightResultErrorException | YeelightSocketException e) {
			try {
				init();
				return isCurrentActionAlreadyAppliedImpl(action);
			}catch (Exception e1) {
				throw new RemoveDeviceCommunicationException(e1);
			}

		}

	}


	private boolean isCurrentActionAlreadyAppliedImpl(ActionConfig action) throws YeelightResultErrorException, YeelightSocketException {
		return getCurrentStatusAsConfig().isEquivalent(action);
	}

	@Override
	public void applyAction(ActionConfig action) throws RemoveDeviceCommunicationException {
		try {
			applyActionImpl(action);
		} catch (YeelightResultErrorException | YeelightSocketException e) {
			try {
				init();
				applyActionImpl(action);
			}catch (Exception e1) {				
				throw new RemoveDeviceCommunicationException(e1);
			}
		}

	}

	private void applyActionImpl(ActionConfig action) throws YeelightResultErrorException, YeelightSocketException {
		device.setPower(action.isPower());

		if(!action.isPower()) {
			return;
		}

		switch (action.getColorMode()) {
		case COLOR:
			Color color = Color.decode(action.getRgb());
			device.setRGB(color.getRed(), color.getGreen(), color.getBlue());
			device.setBrightness(action.getBrightness());
			break;
		case COLOR_TEMPERATURE:			
			device.setColorTemperature(action.getColorTemparature());
			device.setBrightness(action.getBrightness());
			break;
		case HSV:
			device.setHSV(action.getHue(), action.getSaturation());
			break;

		default:
			break;
		}



	}

	public static int colorHexToYeelightColor(String hex) {	
		Color color = Color.decode(hex);
		return (color.getRed() * 65536) + (color.getGreen() * 256) + color.getBlue();
	}



	private boolean getOnOffBoolean(String onOff) {
		return onOff.trim().toUpperCase().equals("ON");
	}
	@Override
	public String statusReport() {
		StringBuilder sb = new StringBuilder();
		try {
			Map<YeelightProperty, String> properties = device.getProperties();
			for(Entry<YeelightProperty, String> propertyEntry:properties.entrySet()) {
				if(StringUtils.isNotEmpty(propertyEntry.getValue()))
					sb.append("\n\t").append(propertyEntry.getKey().toString()).append(" : ").append(getValue(propertyEntry));
			}
			return sb.toString();
		} catch (YeelightResultErrorException | YeelightSocketException e) {
			return e.getMessage();
		}
	}
	private String getValue(Entry<YeelightProperty, String> propertyEntry) {
		if(propertyEntry.getKey().equals(YeelightProperty.RGB)) {
			return propertyEntry.getValue();
		}
		return propertyEntry.getValue();
	}

	@Override
	public void disconnect() {

	}

	@Override
	public ActionConfig getCurrentStatusAsConfig() {
		try {
			Map<YeelightProperty, String> properties = device.getProperties();
			ActionConfig config = new ActionConfig();
			int colorMode = toInt(properties.get(YeelightProperty.COLOR_MODE));
			config.setColorMode(getColorModeByCode(colorMode));			
			config.setDeviceId(deviceConfig.getId());
			config.setPower(getOnOffBoolean(properties.get(YeelightProperty.POWER)));
			
			if(config.getColorMode().equals(ColorMode.COLOR)) {
				Color rgb = new Color(toInt(properties.get(YeelightProperty.RGB)));
				config.setRgb(String.format("#%02x%02x%02x", rgb.getRed(), rgb.getGreen(), rgb.getBlue()));
				config.setBrightness(toInt(properties.get(YeelightProperty.BRIGHTNESS)));				
			}
			if(config.getColorMode().equals(ColorMode.COLOR_TEMPERATURE)) {
				config.setColorTemparature(toInt(properties.get(YeelightProperty.COLOR_TEMPERATURE)));	
				config.setBrightness(toInt(properties.get(YeelightProperty.BRIGHTNESS)));				
			}
			if(config.getColorMode().equals(ColorMode.HSV)) {				
				config.setSaturation(toInt(properties.get(YeelightProperty.SAT)));
				config.setHue(toInt(properties.get(YeelightProperty.HUE)));
			}			

			return config;
		} catch (YeelightResultErrorException | YeelightSocketException e) {
			throw new RemoveDeviceCommunicationException(e);
		}
	}

	private ColorMode getColorModeByCode(int colorMode) {
		switch (colorMode) {
		case 1:
			return ColorMode.COLOR;
		case 2:
			return ColorMode.COLOR_TEMPERATURE;
		case 3:
			return ColorMode.HSV;

		}
		return null;
	}

	private int toInt(String string) {
		try {
			return Integer.parseInt(string);
		}catch (Exception e) {
			return 0;
		}
	}

}
