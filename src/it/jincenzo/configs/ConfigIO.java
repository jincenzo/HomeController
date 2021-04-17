package it.jincenzo.configs;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class ConfigIO {

	public static Configs read(String path) throws JsonParseException, JsonMappingException, IOException {
		XmlMapper xmlMapper = new XmlMapper();
		return xmlMapper.readValue(new File(path), Configs.class);
	}

	public static void write(Configs configs, String path) throws JsonGenerationException, JsonMappingException, IOException  {
		ObjectMapper mapper = new XmlMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(new File(path), configs);	
	}
	
	public static String actionConfigToXML(ActionConfig config) throws JsonProcessingException {
		ObjectMapper mapper = new XmlMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	
		return mapper.writeValueAsString(config);
	}
}
