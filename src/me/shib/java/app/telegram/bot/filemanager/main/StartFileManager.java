package me.shib.java.app.telegram.bot.filemanager.main;

import java.io.File;

import me.shib.java.lib.telegram.bot.easybot.TBotConfig;
import me.shib.java.lib.telegram.bot.easybot.TBotWorker;

public class StartFileManager {
	
	private static final int threadCount = 7;
	
	public static void main(String[] args) throws InterruptedException {
		TBotWorker[] botWorkers = new TBotWorker[threadCount];
		TBotConfig fileManagerConfig = TBotConfig.getFileConfig(new File("FileManagerBotConfig.json"));
		for(int i = 0; i < threadCount; i++) {
			botWorkers[i] = new TBotWorker(new FileManagerBotModel(fileManagerConfig), fileManagerConfig);
			botWorkers[i].start();
		}
		boolean threadAlive = true;
		while(threadAlive) {
			for(int i = 0; i < threadCount; i++) {
				if(!botWorkers[i].isAlive()) {
					threadAlive = false;
				}
			}
			Thread.sleep(4444);
		}
	}
	
}
