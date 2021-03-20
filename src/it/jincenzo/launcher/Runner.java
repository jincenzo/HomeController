package it.jincenzo.launcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import it.jincenzo.configs.ActionConfig;
import it.jincenzo.configs.ActionSet;
import it.jincenzo.configs.ConfigIO;
import it.jincenzo.configs.Configs;
import it.jincenzo.configs.DeviceConfig;
import it.jincenzo.configs.TriggerConfig;
import it.jincenzo.core.ActionExecutor;
import it.jincenzo.core.HomeControllerConstants;
import it.jincenzo.core.remotedevice.DeviceType;

public class Runner {
	

	public static void main(String[] args) throws Exception {
		//testWriteConfig();
		testReadConfig();
	}

	private static void testReadConfig() throws Exception {
		Configs configs = ConfigIO.read(HomeControllerConstants.DEFAULT_CONF_FILE_PATH);
		ActionExecutor executor = new ActionExecutor(configs);
		for(int i=0;i<10;i++) {
			executor.handleTrigger("TRIGGER_1");
			s(1000);
		}

	}

	private static void s(long delay) throws InterruptedException {
		Thread.sleep(delay);

	}

	private static void testWriteConfig() throws JsonGenerationException, JsonMappingException, IOException {
		Configs test = createDefaultConfigs();
		ConfigIO.write(test, HomeControllerConstants.DEFAULT_CONF_FILE_PATH);
	}

	private static Configs createDefaultConfigs() {
		List<DeviceConfig> devices = new ArrayList<>();
		devices.add(new DeviceConfig("SOTTOMARINO","192.168.178.32", DeviceType.YEELIGHT));

		List<ActionSet> actionSets = new ArrayList<>();

		List<ActionConfig> actions = new ArrayList<>();
		actions.add(new ActionConfig("SOTTOMARINO", true, 100, "#71eb34"));

		actionSets.add(new ActionSet(actions));
		List<TriggerConfig> triggers = new ArrayList<>();
		triggers.add(new TriggerConfig("TRIGGER_1", actionSets));
		Configs configs = new Configs(devices, triggers);
		return configs;
	}
}
