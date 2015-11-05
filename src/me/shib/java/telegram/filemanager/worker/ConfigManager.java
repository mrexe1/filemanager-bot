package me.shib.java.telegram.filemanager.worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.nudanam.java.rest.client.lib.JsonLib;

public class ConfigManager {
	
	private static final String configFilePath = "FileManagerBotConfig.json";
	private static Config config = null;
	
	public static Config config() {
		if(config == null) {
			File configFile = new File(configFilePath);
			if(configFile.exists()) {
				try {
					StringBuilder jsonBuilder = new StringBuilder();
					BufferedReader br = new BufferedReader(new FileReader(configFile));
					String line = br.readLine();
					while(line != null) {
						jsonBuilder.append(line);
						line = br.readLine();
						if(line != null) {
							jsonBuilder.append("\n");
						}
					}
					br.close();
					config = JsonLib.fromJson(jsonBuilder.toString(), Config.class);
					if(config.botApiToken() == null) {
						config = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return config;
	}
	
}
