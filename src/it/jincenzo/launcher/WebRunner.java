package it.jincenzo.launcher;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import it.jincenzo.configs.ConfigIO;
import it.jincenzo.core.ActionExecutor;
import it.jincenzo.core.ActionExecutorFeedback;
import it.jincenzo.core.ActionExecutorResult;
import it.jincenzo.core.HomeControllerConstants;

public class WebRunner {

	private static final String RESPONSE_MISSING_PARAMS = "MISSING_PARAMS";
	private static final String PARAM_RELOAD_CONFIGS = "reloadConfigs";
	private static final String RESPONSE_KO = "KO";
	private static final String RESPONSE_OK = "OK";
	private static final String PARAM_TRIGGER = "TRIGGER";
	private static ActionExecutor EXECUTOR;

	public static void main(String[] args) throws Exception {

		EXECUTOR = new ActionExecutor(ConfigIO.read(HomeControllerConstants.DEFAULT_CONF_FILE_PATH));

		int PORT = 8000;
		HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
		server.createContext("/HomeController", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		System.out.println("Server is Up on port "+PORT+" context = /HomeController");
	}

	static class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			System.out.println("Query String "+t.getRequestURI().getQuery());
			Map<String, String> params = queryToMap(t.getRequestURI().getQuery());

			String response = handleParams(params);        	

			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}


	}

	public static Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<>();
		if(StringUtils.isNotEmpty(query)) {
			for (String param : query.split("&")) {
				String[] entry = param.split("=");
				if (entry.length > 1) {
					result.put(entry[0], entry[1]);
				}else{
					result.put(entry[0], "");
				}
			}
		}
		return result;
	}

	static String handleParams(Map<String, String> params) {
		if(params.containsKey(PARAM_TRIGGER)) {
			ActionExecutorResult result = EXECUTOR.handleTrigger(params.get(PARAM_TRIGGER));
			if(result.getFeedback().equals(ActionExecutorFeedback.TRIGGER_EXECUTED)) {
				return RESPONSE_OK;				
			}else
			if(result.getFeedback().equals(ActionExecutorFeedback.TRIGGER_NOT_FOUND)) {
				return response(RESPONSE_KO,ActionExecutorFeedback.TRIGGER_NOT_FOUND.name());				
			}			else {
				return response(RESPONSE_KO,result.getException()!=null ? result.getException().getMessage() : "");
			}			
		}
		if(params.containsKey(PARAM_RELOAD_CONFIGS)) {
			try {
				EXECUTOR.validateAndSetConfigs(ConfigIO.read(HomeControllerConstants.DEFAULT_CONF_FILE_PATH));
				return response(RESPONSE_OK);
			} catch (Exception e) {
				e.printStackTrace();
				return response(RESPONSE_KO,e.getMessage());
			} 
		}
		return RESPONSE_MISSING_PARAMS;
	}

	private static String response(String ... strings ) {
		return Arrays.asList(strings).stream().collect(Collectors.joining("\n"));
	}

}