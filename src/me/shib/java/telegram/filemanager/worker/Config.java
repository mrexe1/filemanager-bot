package me.shib.java.telegram.filemanager.worker;

public class Config {
	
	private String botApiToken;
	private String homeDirPath;
	private int fileManagerThreads;
	private int fileListMaxLengthPerView;
	
	protected Config() {
		homeDirPath = System.getProperty("user.dir");
		fileManagerThreads = 7;
		fileListMaxLengthPerView = 20;
	}

	public String botApiToken() {
		return botApiToken;
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
