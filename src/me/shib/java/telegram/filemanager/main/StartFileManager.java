package me.shib.java.telegram.filemanager.main;

import me.shib.java.telegram.bot.worker.TBotWorker;

public class StartFileManager {
	
	public static void main(String[] args) throws InterruptedException {
		FileManagerBotConfig fileManagerConfig = FileManagerBotConfig.getInstanceFromFile();
		TBotWorker[] botWorkers = new TBotWorker[fileManagerConfig.fileManagerThreads()];
		for(int i = 0; i < fileManagerConfig.fileManagerThreads(); i++) {
			botWorkers[i] = new TBotWorker(new FileManagerBotModel());
			botWorkers[i].start();
		}
		boolean threadAlive = true;
		while(threadAlive) {
			for(int i = 0; i < fileManagerConfig.fileManagerThreads(); i++) {
				if(!botWorkers[i].isAlive()) {
					threadAlive = false;
				}
			}
			Thread.sleep(4444);
		}
	}
	
}
