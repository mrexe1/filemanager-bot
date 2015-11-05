package me.shib.java.telegram.filemanager.navigator;

import java.io.File;

import me.shib.java.telegram.filemanager.worker.ConfigManager;

public class UserDir {
	
	private long userId;
	private File dir;
	private File file;
	private int fromRange;
	private int toRange;
	private boolean showNextButton;
	private boolean showPrevButton;
	private ShowRange showRange;
	
	private static File homeDir = new File(ConfigManager.config().homeDirPath());
	
	public static enum ShowRange {
		DEFAULT, NEXT, PREVIOUS
	}
	
	public UserDir(long userId) {
		this.userId = userId;
		this.dir = homeDir;
		fromRange = 0;
		toRange = 0;
		showNextButton = false;
		showPrevButton = false;
		showRange = ShowRange.DEFAULT;
	}

	private String[] getListing() {
		String[] list = dir.list();
		if(list.length > ConfigManager.config().fileListMaxLengthPerView()) {
			if(showRange == ShowRange.NEXT) {
				fromRange = fromRange + ConfigManager.config().fileListMaxLengthPerView();
				if(fromRange > list.length) {
					fromRange = 1;
				}
			}
			else if(showRange == ShowRange.PREVIOUS) {
				fromRange = fromRange - ConfigManager.config().fileListMaxLengthPerView();
				if(fromRange < 1) {
					fromRange = 1;
				}
			}
			else if(fromRange < 1) {
				fromRange = 1;
			}
			toRange = fromRange + ConfigManager.config().fileListMaxLengthPerView() - 1;
			if(toRange > list.length) {
				toRange = list.length;
			}
			if(fromRange > 1) {
				showPrevButton = true;
			}
			if(toRange < list.length) {
				showNextButton = true;
			}
			String[] newList = new String[toRange - fromRange + 1];
			for(int i = (fromRange -1), j = 0; i < toRange; i++, j++) {
				newList[j] = list[i];
			}
			return newList;
		}
		return list;
	}
	
	public KeyBoardAndResponseText getCurrentResponse() {
		StringBuilder responseBuilder = new StringBuilder();
		String[] list = getListing();
		if(list.length > 0) {
			responseBuilder.append("Please select one of the below items:");
		}
		else {
			responseBuilder.append("This directory is empty. Please preform one of the below actions:\n\n/home\n/back");
		}
		if((fromRange > 0) && (toRange > fromRange)) {
			responseBuilder.append("\nShowing items: " + fromRange + " to " + toRange + " of " + dir.list().length + "\n");
		}
		for(int i = 0; i < list.length; i++) {
			responseBuilder.append("\n" + list[i]);
		}
		if(showPrevButton) {
			responseBuilder.append("\n\nEnter \"/previous\" for previous set of items.");
		}
		if(showNextButton) {
			if(!showPrevButton) {
				responseBuilder.append("\n");
			}
			responseBuilder.append("\nEnter \"/next\" for more items.");
		}
		KeyBoardAndResponseText kbart = new KeyBoardAndResponseText(list, responseBuilder.toString());
		return kbart;
	}

	public void navigate(String keyword) {
		file = null;
		showNextButton = false;
		showPrevButton = false;
		showRange = ShowRange.DEFAULT;
		if(keyword.equalsIgnoreCase("/back")) {
			fromRange = 0;
			toRange = 0;
			if(!homeDir.getAbsolutePath().equalsIgnoreCase(dir.getAbsolutePath())) {
				File parentDir = dir.getParentFile();
				if(parentDir != null) {
					dir = parentDir;
				}
			}
		}
		else if(keyword.equalsIgnoreCase("/home") || keyword.equalsIgnoreCase("/start")) {
			fromRange = 0;
			toRange = 0;
			dir = homeDir;
		}
		else if(keyword.equalsIgnoreCase("/next")) {
			showRange = ShowRange.NEXT;
		}
		else if(keyword.equalsIgnoreCase("/previous")) {
			showRange = ShowRange.PREVIOUS;
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

	public boolean isShowNextButton() {
		return showNextButton;
	}

	public boolean isShowPrevButton() {
		return showPrevButton;
	}
	
}
