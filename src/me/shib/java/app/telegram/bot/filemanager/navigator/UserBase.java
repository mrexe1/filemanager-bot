package me.shib.java.app.telegram.bot.filemanager.navigator;

import me.shib.java.lib.telegram.bot.easybot.BotConfig;

import java.util.HashMap;
import java.util.Map;

public class UserBase {

    private static Map<String, UserDir> userDirMap;

    public static UserDir getUserDir(long userId, BotConfig fileManagerConfig) {
        if (userDirMap == null) {
            userDirMap = new HashMap<>();
        }
        UserDir userDir = userDirMap.get(userId + "");
        if (userDir == null) {
            userDir = new UserDir(userId);
            userDirMap.put(userId + "", userDir);
        }
        return userDir;
    }

}
