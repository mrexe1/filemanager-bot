package me.shib.java.app.telegram.bot.filemanager.navigator;

import me.shib.java.lib.jbots.JBotConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserBase {

    private static Map<Long, UserBase> userBaseMap;

    private Map<Long, UserDir> userDirMap;
    private long botId;
    private File homeDir;
    private String[] fileExtensionsToShow;
    private boolean sendDir;
    private int maxEntriesPerView;

    private UserBase(long botId) {
        this.botId = botId;
    }

    protected static UserBase getUserBase(long botId) {
        if (userBaseMap == null) {
            return null;
        }
        return userBaseMap.get(botId);
    }

    public static synchronized UserBase getInstance(JBotConfig config, long botId) {
        if (config == null) {
            return null;
        }

        if (userBaseMap == null) {
            userBaseMap = new HashMap<>();
        }
        if (botId == 0) {
            return null;
        }
        UserBase userBase = userBaseMap.get(botId);
        if (userBase == null) {
            userBase = new UserBase(botId);
            String sendDirString = config.getConstant("sendDir");

            userBase.sendDir = (sendDirString != null) && (sendDirString.equalsIgnoreCase("true"));

            userBase.maxEntriesPerView = 20;
            try {
                userBase.maxEntriesPerView = Integer.parseInt(config.getConstant("maxEntriesPerView"));
            } catch (Exception ignored) {
            }

            String homeDirPath = config.getConstant("homeDirPath");
            if ((homeDirPath != null) && (!homeDirPath.isEmpty())) {
                userBase.homeDir = new File(homeDirPath);
                if (!userBase.homeDir.exists()) {
                    userBase.homeDir = new File(System.getProperty("user.dir"));
                }
            } else {
                userBase.homeDir = new File(System.getProperty("user.dir"));
            }
            userBaseMap.put(botId, userBase);

            String fileExtensionListToShow = config.getConstant("fileExtensionsToShow");
            if (fileExtensionListToShow == null) {
                userBase.fileExtensionsToShow = null;
            } else {
                userBase.fileExtensionsToShow = fileExtensionListToShow.split(",");
            }

        }
        return userBase;
    }

    public File getHomeDir() {
        return homeDir;
    }

    public String[] getFileExtensionsToShow() {
        return fileExtensionsToShow;
    }

    public boolean isSendDir() {
        return sendDir;
    }

    public int getMaxEntriesPerView() {
        return maxEntriesPerView;
    }

    private boolean isSupportedFormat(File validationFile) {
        String[] extensions = null;
        UserBase userBase = UserBase.getUserBase(botId);
        if (userBase != null) {
            extensions = userBase.getFileExtensionsToShow();
        }
        if (extensions != null) {
            for (String ext : extensions) {
                if (validationFile.getName().toLowerCase().endsWith(ext.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    public File[] getFilesInDirectory(File directory) {
        if ((fileExtensionsToShow == null) || (fileExtensionsToShow.length < 1)) {
            return directory.listFiles();
        }
        File[] allFiles = directory.listFiles();
        ArrayList<File> qualifiedFiles = new ArrayList<>();
        if (allFiles != null) {
            for (File f : allFiles) {
                if (f.isDirectory() || isSupportedFormat(f)) {
                    qualifiedFiles.add(f);
                }
            }
        }
        File[] qualifiedFilesArray = new File[qualifiedFiles.size()];
        return qualifiedFiles.toArray(qualifiedFilesArray);
    }

    public UserDir getUserDir(long userId) {
        if (userDirMap == null) {
            userDirMap = new HashMap<>();
        }
        UserDir userDir = userDirMap.get(userId);
        if (userDir == null) {
            userDir = new UserDir(userId, botId);
            userDirMap.put(userId, userDir);
        }
        return userDir;
    }

}
