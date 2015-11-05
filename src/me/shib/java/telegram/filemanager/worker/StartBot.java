package me.shib.java.telegram.filemanager.worker;

public class StartBot {
	
	public static void main(String[] args) throws InterruptedException {
		if(args.length <= 1) {
			FileManagerThread[] fmts = new FileManagerThread[ConfigManager.config().fileManagerThreads()];
			for(int i = 0; i < ConfigManager.config().fileManagerThreads(); i++) {
				fmts[i] = new FileManagerThread();
				System.out.println("Starting the File Manager Thread: " + (i + 1));
				fmts[i].start();
			}
			boolean threadAlive = true;
			while(threadAlive) {
				for(int i = 0; i < ConfigManager.config().fileManagerThreads(); i++) {
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
