package me.shib.java.telegram.filemanager.worker;

import java.io.File;

import me.shib.java.telegram.filemanager.navigator.UserDir;

public class Start {
	
	public static void main(String[] args) throws InterruptedException {
		String homeDirPath = null;
		if(args.length == 0) {
			homeDirPath = System.getProperty("user.dir");
		}
		else if(args.length == 1) {
			homeDirPath = args[0];
			File dir = new File(homeDirPath);
			if(!dir.exists()) {
				homeDirPath = System.getProperty("user.dir");
			}
		}
		if(args.length <= 1) {
			UserDir.homeDir = new File(homeDirPath);
			FileManagerThread[] fmts = new FileManagerThread[Config.fileManagerThreads];
			for(int i = 0; i < Config.fileManagerThreads; i++) {
				fmts[i] = new FileManagerThread();
				System.out.println("Starting the File Manager Thread: " + (i + 1));
				fmts[i].start();
			}
			boolean threadAlive = true;
			while(threadAlive) {
				for(int i = 0; i < Config.fileManagerThreads; i++) {
					if(!fmts[i].isAlive()) {
						threadAlive = false;
					}
				}
				Thread.sleep(4444);
			}
			FileManagerThread fmt = new FileManagerThread();
			fmt.processFileRequests();
		}
		else {
			System.out.println("Invalid Arguments.");
		}
	}
	
}
