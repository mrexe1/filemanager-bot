package me.shib.java.app.telegram.bot.filemanager.main;

import me.shib.java.lib.telegram.bot.easybot.TBotConfig;
import me.shib.java.lib.telegram.bot.run.Launcher;

public class LocalLauncher {
	
	private static final Class<?> thisBotlauncherClass = FileManagerBotLaucher.class;
	
	private static TBotConfig getMatchingConfig(TBotConfig[] configList, Class<?> launcherClass) {
		if(configList != null) {
			for(TBotConfig conf : configList) {
				if(conf.getBotLauncherclassName().equals(launcherClass.getName())) {
					return conf;
				}
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		TBotConfig[] configList = TBotConfig.getFileConfigList();
		TBotConfig thisBotConfig = getMatchingConfig(configList, thisBotlauncherClass);
		if(thisBotConfig != null) {
			TBotConfig[] singleBotConfigAsList = new TBotConfig[1];
			singleBotConfigAsList[0] = thisBotConfig;
			Launcher.launchBots(singleBotConfigAsList);
		}
		else {
			System.out.println("Unable to find the required botLauncherclassName: \"" + thisBotlauncherClass.getName() + "\"");
		}
	}
}
