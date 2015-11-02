package me.shib.java.telegram.filemanager.navigator;

import java.io.File;
import java.util.Date;

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
		if(file == null) {
			String[] list = dir.list();
			responseBuilder.append("Please select one of the below items:");
			for(int i = 0; i < list.length; i++) {
				responseBuilder.append("\n" + list[i]);
			}
		}
		else {
			responseBuilder.append("File Info:\n");
			responseBuilder.append("Name: " + file.getName() + "\n");
			responseBuilder.append("Last Modified: " + new Date(file.lastModified()) + "\n");
			responseBuilder.append("Full Path: \"" + file.getAbsolutePath() + "\"");
		}
		KeyBoardAndResponseText kbart = new KeyBoardAndResponseText(dir.list(), responseBuilder.toString());
		return kbart;
	}
	
	public void navigate(String keyword) {
		file = null;
		if(keyword.equalsIgnoreCase("/back")) {
			File parentDir = dir.getParentFile();
			if(parentDir != null) {
				dir = parentDir;
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

	public long getUserId() {
		return userId;
	}
	
}
