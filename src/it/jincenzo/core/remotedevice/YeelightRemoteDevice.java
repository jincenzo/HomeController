package it.jincenzo.core.remotedevice;

import java.awt.Color;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ctc.wstx.util.StringUtil;
import com.mollin.yapi.YeelightDevice;
import com.mollin.yapi.enumeration.YeelightProperty;
import com.mollin.yapi.exception.YeelightResultErrorException;
import com.mollin.yapi.exception.YeelightSocketException;

import it.jincenzo.configs.ActionConfig;
import it.jincenzo.configs.DeviceConfig;

public class YeelightRemoteDevice extends RemoteDevice {
	private YeelightDevice device;

	public YeelightRemoteDevice(DeviceConfig deviceConfig) throws Exception {
		super(deviceConfig);
		device = new YeelightDevice(deviceConfig.getIp());
	}

	@Override
	public boolean isCurrentActionAlreadyApplied(ActionConfig action) throws RemoveDeviceCommunicationException {
		try {
			Map<YeelightProperty, String> properties = device.getProperties();
			String power = properties.getOrDefault(YeelightProperty.POWER, null);
			if (power == null || getOnOffBoolean(power) != action.isPower()) {
				return false;
			}

			if (StringUtils.isNotEmpty(action.getRgb())) {
				String rgb = properties.getOrDefault(YeelightProperty.RGB, null);
				int actionColor = getColorCode(action.getRgb());
				if (rgb == null || Integer.parseInt(rgb) != actionColor) {
					return false;
				}
			}

			if (action.getBrightness() != null) {
				String brightness = properties.getOrDefault(YeelightProperty.BRIGHTNESS, null);
				if (brightness == null || Integer.parseInt(brightness) != action.getBrightness()) {
					return false;
				}
			}

			return true;
		} catch (YeelightResultErrorException | YeelightSocketException e) {
			throw new RemoveDeviceCommunicationException(e);
		}

	}

	@Override
	public void applyAction(ActionConfig action) throws RemoveDeviceCommunicationException {
		try {

			device.setPower(action.isPower());
			if(action.getBrightness() != null) {
				device.setBrightness(action.getBrightness());
			}
			if(StringUtils.isNotEmpty(action.getRgb())) {
				Color color = Color.decode(action.getRgb());
				device.setRGB(color.getRed(), color.getGreen(), color.getBlue());
			}

		} catch (YeelightResultErrorException | YeelightSocketException e) {
			throw new RemoveDeviceCommunicationException(e);
		}

	}

	private int getColorCode(String hex) {
		Color color = Color.decode(hex);
		return (color.getRed() * 65536) + (color.getGreen() * 256) + color.getBlue();
	}

	private boolean getOnOffBoolean(String onOff) {
		return onOff.trim().toUpperCase().equals("ON");
	}

	@Override
	public void disconnect() {

	}

}
