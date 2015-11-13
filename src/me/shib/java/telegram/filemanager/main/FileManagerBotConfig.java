package me.shib.java.telegram.filemanager.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import me.shib.java.rest.client.lib.JsonLib;

public class FileManagerBotConfig {
	
	private String homeDirPath;
	private int fileManagerThreads;
	private int fileListMaxLengthPerView;
	
	private static final String configFilePath = "FileManagerBotConfig.json";
	private static FileManagerBotConfig fileManagerBotConfig;
	
	public static synchronized FileManagerBotConfig getInstanceFromFile() {
		if(fileManagerBotConfig == null) {
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
					fileManagerBotConfig = JsonLib.fromJson(jsonBuilder.toString(), FileManagerBotConfig.class);
					fileManagerBotConfig.initDefaults();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fileManagerBotConfig;
	}
	
	protected FileManagerBotConfig() {
		initDefaults();
	}
	
	private void initDefaults() {
		if(this.homeDirPath == null) {
			homeDirPath = System.getProperty("user.dir");
		}
		if(this.fileManagerThreads < 1) {
			this.fileManagerThreads = 1;
		}
		if(this.fileListMaxLengthPerView < 1) {
			this.fileListMaxLengthPerView = 20;
		}
	}

	public String homeDirPath() {
		return homeDirPath;
	}

	public int fileManagerThreads() {
		return fileManagerThreads;
	}

	public int fileListMaxLengthPerView() {
		return fileListMaxLengthPerView;
	}

}
