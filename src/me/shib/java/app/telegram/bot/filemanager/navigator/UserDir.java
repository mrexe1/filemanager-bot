package me.shib.java.app.telegram.bot.filemanager.navigator;

import java.io.File;
import java.util.ArrayList;

import me.shib.java.app.telegram.bot.filemanager.main.FileManagerBotModel;
import me.shib.java.lib.telegram.bot.easybot.TBotConfig;

public class UserDir {
	
	private long userId;
	private File dir;
	private File file;
	private int fromRange;
	private int toRange;
	private boolean showNextButton;
	private boolean showPrevButton;
	private ShowRange showRange;
	private String consumableSearchSuggestion;
	private int maxEntriesPerView;
	
	private static File homeDir = null;
	private static String[] fileExtensionsToShow = null;
	private static boolean sendDir = false;
	
	public enum ShowRange {
		DEFAULT, NEXT, PREVIOUS
	}
	
	public UserDir(long userId) {
		if(!sendDir) {
			String sendDirString = FileManagerBotModel.fileManagerConfig.getValueForKey("sendDir");
			if((sendDirString != null) && (sendDirString.equalsIgnoreCase("true"))) {
				sendDir = true;
			}
		}
		try {
			maxEntriesPerView = Integer.parseInt(FileManagerBotModel.fileManagerConfig.getValueForKey("maxEntriesPerView"));
		} catch (Exception e) {
			maxEntriesPerView = 20;
		}
		this.userId = userId;
		this.dir = getHomeDir(FileManagerBotModel.fileManagerConfig);
		fromRange = 0;
		toRange = 0;
		showNextButton = false;
		showPrevButton = false;
		showRange = ShowRange.DEFAULT;
		consumableSearchSuggestion = null;
	}
	
	public static File getHomeDir(TBotConfig fileManagerConfig) {
		if(homeDir == null) {
			String homeDirPath = fileManagerConfig.getValueForKey("homeDirPath");
			if((homeDirPath != null) && (!homeDirPath.isEmpty())) {
				homeDir = new File(homeDirPath);
				if(!homeDir.exists()) {
					homeDirPath = System.getProperty("user.dir");
					homeDir = new File(homeDirPath);
				}
			}
		}
		return homeDir;
	}
	
	private static String[] getFileExtensionsToShow() {
		if(fileExtensionsToShow == null) {
			String fileExtensionListToShow = FileManagerBotModel.fileManagerConfig.getValueForKey("fileExtensionsToShow");
			if(fileExtensionListToShow == null) {
				return null;
			}
			fileExtensionsToShow = fileExtensionListToShow.split(",");
		}
		return fileExtensionsToShow;
	}
	
	private static boolean isSupportedFormat(File validationFile) {
		String[] extensions = getFileExtensionsToShow();
		for(String ext : extensions) {
			if(validationFile.getName().toLowerCase().endsWith(ext.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	private String[] getFileNamesForFileList(File[] fileList) {
		String[] fileNames = new String[fileList.length];
		for(int i = 0; i < fileList.length; i++) {
			fileNames[i] = fileList[i].getName();
		}
		return fileNames;
	}
	
	public static File[] getFilesInDirectory(File directory) {
		String[] extensions = getFileExtensionsToShow();
		if((extensions == null) || (extensions.length < 1)) {
			return directory.listFiles();
		}
		File[] allFiles = directory.listFiles();
		ArrayList<File> qualifiedFiles = new ArrayList<File>();
		for(File f : allFiles) {
			if(f.isDirectory() || isSupportedFormat(f)) {
				qualifiedFiles.add(f);
			}
		}
		File[] qualifiedFilesArray = new File[qualifiedFiles.size()];
		return qualifiedFiles.toArray(qualifiedFilesArray);
	}
	
	private File[] getListing() {
		File[] list = getFilesInDirectory(dir);
		if(list.length > maxEntriesPerView) {
			if(showRange == ShowRange.NEXT) {
				fromRange = fromRange + maxEntriesPerView;
				if(fromRange > list.length) {
					fromRange = 1;
				}
			}
			else if(showRange == ShowRange.PREVIOUS) {
				fromRange = fromRange - maxEntriesPerView;
				if(fromRange < 1) {
					fromRange = 1;
				}
			}
			else if(fromRange < 1) {
				fromRange = 1;
			}
			toRange = fromRange + maxEntriesPerView - 1;
			if(toRange > list.length) {
				toRange = list.length;
			}
			if(fromRange > 1) {
				showPrevButton = true;
			}
			if(toRange < list.length) {
				showNextButton = true;
			}
			File[] newList = new File[toRange - fromRange + 1];
			for(int i = (fromRange -1), j = 0; i < toRange; i++, j++) {
				newList[j] = list[i];
			}
			return newList;
		}
		return list;
	}
	
	public KeyBoardAndResponseText getCurrentResponse() {
		StringBuilder responseBuilder = new StringBuilder();
		File[] list = getListing();
		if(list.length > 0) {
			responseBuilder.append("Please select one of the below items:");
		}
		else {
			responseBuilder.append("This directory is empty. Please preform one of the below actions:\n\n/home\n/back");
		}
		File[] dirList = getFilesInDirectory(dir);
		if((fromRange > 0) && (toRange >= fromRange) && (dirList.length > list.length)) {
			responseBuilder.append("\nShowing items: " + fromRange + " to " + toRange + " of " + dirList.length + "\n");
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
		KeyBoardAndResponseText kbart = new KeyBoardAndResponseText(getFileNamesForFileList(list), responseBuilder.toString());
		return kbart;
	}
	
	private boolean isMatching(String str1, String str2) {
		String[] list1 = str1.split("\\s+");
		String[] list2 = str2.split("\\s+");
		for(int i = 0; i < list1.length; i++) {
			for(int j = 0; j < list2.length; j++) {
				if(list1[i].equalsIgnoreCase(list2[j])) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String getMatchedItems(String searchTerm) {
		ArrayList<String> matchedItemList = new ArrayList<String>();
		if(dir.exists() && dir.isDirectory()) {
			File fileList[] = getFilesInDirectory(dir);
			for(int i = 0; i < fileList.length; i++) {
				if(isMatching(searchTerm, fileList[i].getName())) {
					matchedItemList.add("/" + (i + 1) + " - " + fileList[i].getName());
				}
			}
			if(matchedItemList.size() > 0) {
				StringBuilder searchSuggestionMessage = new StringBuilder();
				searchSuggestionMessage.append("See if you could find the item u wish in the following list:\n");
				for(int i = 0; i < matchedItemList.size(); i++) {
					searchSuggestionMessage.append(matchedItemList.get(i) + "\n");
				}
				searchSuggestionMessage.append("Please enter the exact name or the number with \"/\" symbol for the name if u see it.");
				return searchSuggestionMessage.toString();
			}
		}
		return null;
	}
	
	public void navigate(String keyword) {
		file = null;
		showNextButton = false;
		showPrevButton = false;
		showRange = ShowRange.DEFAULT;
		if(keyword.equalsIgnoreCase("/back")) {
			fromRange = 0;
			toRange = 0;
			if(!getHomeDir(FileManagerBotModel.fileManagerConfig).getAbsolutePath().equalsIgnoreCase(dir.getAbsolutePath())) {
				File parentDir = dir.getParentFile();
				if(parentDir != null) {
					dir = parentDir;
				}
			}
		}
		else if(keyword.equalsIgnoreCase("/home") || keyword.equalsIgnoreCase("/start")) {
			fromRange = 0;
			toRange = 0;
			dir = getHomeDir(FileManagerBotModel.fileManagerConfig);
		}
		else if(keyword.equalsIgnoreCase("/next")) {
			showRange = ShowRange.NEXT;
		}
		else if(keyword.equalsIgnoreCase("/previous")) {
			showRange = ShowRange.PREVIOUS;
		}
		else if(keyword.startsWith("/")) {
			int num;
			try {
				num = Integer.parseInt(keyword.replace("/", ""));
			} catch (Exception e) {
				num = 0;
			}
			String[] fileNameListInDir = getFileNamesForFileList(getFilesInDirectory(dir));
			if((num > 0) && (num <= fileNameListInDir.length)) {
				File newFileOrDir = new File(dir.getPath() + File.separator + fileNameListInDir[num - 1]);
				if(newFileOrDir.exists()) {
					if(newFileOrDir.isDirectory() && (!sendDir)) {
						dir = newFileOrDir;
					}
					else {
						file = newFileOrDir;
					}
				}
			}
		}
		else {
			File newFileOrDir = new File(dir.getPath() + File.separator + keyword);
			if(newFileOrDir.exists()) {
				if(newFileOrDir.isDirectory() && (!sendDir)) {
					dir = newFileOrDir;
				}
				else {
					file = newFileOrDir;
				}
			}
			else {
				consumableSearchSuggestion = getMatchedItems(keyword);
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

	public String getConsumableSearchSuggestion() {
		String toReturn = consumableSearchSuggestion;
		consumableSearchSuggestion = null;
		return toReturn;
	}
	
}
