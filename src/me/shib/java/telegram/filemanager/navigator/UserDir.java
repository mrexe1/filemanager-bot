package me.shib.java.telegram.filemanager.navigator;

import java.io.File;

public class UserDir {
	
	private long userId;
	private File dir;
	private File file;
	
	public static File homeDir;
	
	public UserDir(long userId) {
		this.userId = userId;
		this.dir = homeDir;
	}
	
	public KeyBoardAndResponseText getCurrentResponse() {
		StringBuilder responseBuilder = new StringBuilder();
		String[] list = dir.list();
		responseBuilder.append("Please select one of the below items:");
		for(int i = 0; i < list.length; i++) {
			responseBuilder.append("\n" + list[i]);
		}
		KeyBoardAndResponseText kbart = new KeyBoardAndResponseText(dir.list(), responseBuilder.toString());
		return kbart;
	}

	public void navigate(String keyword) {
		file = null;
		if(keyword.equalsIgnoreCase("/back")) {
			File parentDir = dir.getParentFile();
			if(parentDir != null) {
				if(!homeDir.getParentFile().getAbsolutePath().equalsIgnoreCase(parentDir.getAbsolutePath())) {
					dir = parentDir;
				}
			}
		}
		else if(keyword.equalsIgnoreCase("/home") || keyword.equalsIgnoreCase("/start")) {
			dir = homeDir;
		}
		else {
			File newFileOrDir = new File(dir.getPath() + File.separator + keyword);
			if(newFileOrDir.exists()) {
				if(newFileOrDir.isDirectory()) {
					dir = newFileOrDir;
				}
				else {
					file = newFileOrDir;
				}
			}
		}
	}
	
	public File getFile() {
		File returnableFile = file;
		file = null;
		return returnableFile;
	}

	public long getUserId() {
		return userId;
	}
	
}
