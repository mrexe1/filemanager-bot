package me.shib.java.app.telegram.bot.filemanager.navigator;

import java.io.File;
import java.util.ArrayList;

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
    private long botId;

    protected UserDir(long userId, long botId) {
        this.userId = userId;
        this.botId = botId;
        UserBase userBase = UserBase.getUserBase(botId);
        if (userBase != null) {
            this.dir = userBase.getHomeDir();
        }
        fromRange = 0;
        toRange = 0;
        showNextButton = false;
        showPrevButton = false;
        showRange = ShowRange.DEFAULT;
        consumableSearchSuggestion = null;
    }

    private String[] getFileNamesForFileList(File[] fileList) {
        String[] fileNames = new String[fileList.length];
        for (int i = 0; i < fileList.length; i++) {
            fileNames[i] = fileList[i].getName();
        }
        return fileNames;
    }

    private File[] getListing() {
        int maxEntriesPerView;
        File[] list;
        UserBase userBase = UserBase.getUserBase(botId);
        if (userBase != null) {
            maxEntriesPerView = userBase.getMaxEntriesPerView();
            list = userBase.getFilesInDirectory(dir);
        } else {
            maxEntriesPerView = 20;
            list = new File[0];
        }
        if (list.length > maxEntriesPerView) {
            if (showRange == ShowRange.NEXT) {
                fromRange = fromRange + maxEntriesPerView;
                if (fromRange > list.length) {
                    fromRange = 1;
                }
            } else if (showRange == ShowRange.PREVIOUS) {
                fromRange = fromRange - maxEntriesPerView;
                if (fromRange < 1) {
                    fromRange = 1;
                }
            } else if (fromRange < 1) {
                fromRange = 1;
            }
            toRange = fromRange + maxEntriesPerView - 1;
            if (toRange > list.length) {
                toRange = list.length;
            }
            if (fromRange > 1) {
                showPrevButton = true;
            }
            if (toRange < list.length) {
                showNextButton = true;
            }
            File[] newList = new File[toRange - fromRange + 1];
            for (int i = (fromRange - 1), j = 0; i < toRange; i++, j++) {
                newList[j] = list[i];
            }
            return newList;
        }
        return list;
    }

    public KeyBoardAndResponseText getCurrentResponse() {
        StringBuilder responseBuilder = new StringBuilder();
        File[] list = getListing();
        if (list.length > 0) {
            responseBuilder.append("Please select one of the below items:");
        } else {
            responseBuilder.append("This directory is empty. Please preform one of the below actions:\n\n/home\n/back");
        }
        File[] dirList;
        UserBase userBase = UserBase.getUserBase(botId);
        if (userBase != null) {
            dirList = userBase.getFilesInDirectory(dir);
        } else {
            dirList = new File[0];
        }
        if ((fromRange > 0) && (toRange >= fromRange) && (dirList.length > list.length)) {
            responseBuilder.append("\nShowing items: ").append(fromRange).append(" to ").append(toRange).append(" of ").append(dirList.length).append("\n");
        }
        for (File item : list) {
            responseBuilder.append("\n").append(item.getName());
        }
        if (showPrevButton) {
            responseBuilder.append("\n\nEnter \"/previous\" for previous set of items.");
        }
        if (showNextButton) {
            if (!showPrevButton) {
                responseBuilder.append("\n");
            }
            responseBuilder.append("\nEnter \"/next\" for more items.");
        }
        return new KeyBoardAndResponseText(getFileNamesForFileList(list), responseBuilder.toString());
    }

    private boolean isMatching(String str1, String str2) {
        String[] list1 = str1.split("\\s+");
        String[] list2 = str2.split("\\s+");
        for (String l1Item : list1) {
            for (String l2Item : list2) {
                if (l1Item.equalsIgnoreCase(l2Item)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getMatchedItems(String searchTerm) {
        ArrayList<String> matchedItemList = new ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            File[] fileList;
            UserBase userBase = UserBase.getUserBase(botId);
            if (userBase != null) {
                fileList = userBase.getFilesInDirectory(dir);
            } else {
                fileList = new File[0];
            }
            for (int i = 0; i < fileList.length; i++) {
                if (isMatching(searchTerm, fileList[i].getName())) {
                    matchedItemList.add("/" + (i + 1) + " - " + fileList[i].getName());
                }
            }
            if (matchedItemList.size() > 0) {
                StringBuilder searchSuggestionMessage = new StringBuilder();
                searchSuggestionMessage.append("See if you could find the item u wish in the following list:\n");
                for (String item : matchedItemList) {
                    searchSuggestionMessage.append(item).append("\n");
                }
                searchSuggestionMessage.append("Please enter the exact name or the number with \"/\" symbol for the name if u see it.");
                return searchSuggestionMessage.toString();
            }
        }
        return null;
    }

    public void navigate(String keyword) {
        File homeDir;
        boolean sendDir = false;
        UserBase userBase = UserBase.getUserBase(botId);
        if (userBase != null) {
            homeDir = userBase.getHomeDir();
            sendDir = userBase.isSendDir();
        } else {
            homeDir = new File(System.getProperty("user.dir"));
        }
        file = null;
        showNextButton = false;
        showPrevButton = false;
        showRange = ShowRange.DEFAULT;
        if (keyword.equalsIgnoreCase("/back")) {
            fromRange = 0;
            toRange = 0;
            if (!homeDir.getAbsolutePath().equalsIgnoreCase(dir.getAbsolutePath())) {
                File parentDir = dir.getParentFile();
                if (parentDir != null) {
                    dir = parentDir;
                }
            }
        } else if (keyword.equalsIgnoreCase("/home") || keyword.equalsIgnoreCase("/start")) {
            fromRange = 0;
            toRange = 0;
            dir = homeDir;
        } else if (keyword.equalsIgnoreCase("/next")) {
            showRange = ShowRange.NEXT;
        } else if (keyword.equalsIgnoreCase("/previous")) {
            showRange = ShowRange.PREVIOUS;
        } else if (keyword.startsWith("/")) {
            int num;
            try {
                num = Integer.parseInt(keyword.replace("/", ""));
            } catch (Exception e) {
                num = 0;
            }
            File[] fileList;
            if (userBase != null) {
                fileList = userBase.getFilesInDirectory(dir);
            } else {
                fileList = new File[0];
            }
            String[] fileNameListInDir = getFileNamesForFileList(fileList);
            if ((num > 0) && (num <= fileNameListInDir.length)) {
                File newFileOrDir = new File(dir.getPath() + File.separator + fileNameListInDir[num - 1]);
                if (newFileOrDir.exists()) {
                    if (newFileOrDir.isDirectory() && (!sendDir)) {
                        dir = newFileOrDir;
                    } else {
                        file = newFileOrDir;
                    }
                }
            }
        } else {
            File newFileOrDir = new File(dir.getPath() + File.separator + keyword);
            if (newFileOrDir.exists()) {
                if (newFileOrDir.isDirectory() && (!sendDir)) {
                    dir = newFileOrDir;
                } else {
                    file = newFileOrDir;
                }
            } else {
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

    public enum ShowRange {
        DEFAULT, NEXT, PREVIOUS
    }

}
