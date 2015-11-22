package me.shib.java.app.telegram.bot.filemanager.navigator;

public class KeyBoardAndResponseText {
	
	private String[] fileList;
	private String response;
	
	protected KeyBoardAndResponseText(String[] fileList, String response) {
		this.fileList = fileList;
		this.response = response;
	}

	public String[] getFileList() {
		return fileList;
	}

	public String getResponse() {
		return response;
	}
	
}
