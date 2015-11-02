package me.shib.java.telegram.filemanager.worker;

import java.io.File;
import java.io.IOException;

import com.nudanam.java.telegram.bot.service.TelegramBotService;
import com.nudanam.java.telegram.bot.types.ReplyKeyboardMarkup;
import com.nudanam.java.telegram.bot.types.Update;

import me.shib.java.telegram.filemanager.navigator.KeyBoardAndResponseText;
import me.shib.java.telegram.filemanager.navigator.UserDir;

public class Start {
	
	public static void main(String[] args) {
		String homeDirPath = null;
		if(args.length == 0) {
			homeDirPath = System.getProperty("user.dir");
		}
		else if(args.length == 1) {
			homeDirPath = args[0];
			File dir = new File(homeDirPath);
			if(!dir.exists()) {
				homeDirPath = System.getProperty("user.dir");
			}
		}
		if(args.length <= 1) {
			UserDir.homeDir = new File(homeDirPath);
			TelegramBotService tbs = new TelegramBotService(Config.botApiToken);
			while(true) {
				try {
					Update upd = MessageListener.getUpdate();
					if(upd.getMessage().getText() == null) {
						tbs.sendMessage(upd.getMessage().getChat().getId(), "Please input a text");
					}
					else {
						UserDir ud = UserBase.getUserDir(upd.getMessage().getChat().getUser().getId());
						ud.navigate(upd.getMessage().getText());
						KeyBoardAndResponseText kbt = ud.getCurrentResponse();
						String[] fileNameList = kbt.getFileList();
						String[][] keyboard = new String[fileNameList.length][1];
						for(int i = 0; i < fileNameList.length; i++) {
							keyboard[i][0] = fileNameList[i];
						}
						tbs.sendMessage(ud.getUserId(), kbt.getResponse(), null, true, 0, new ReplyKeyboardMarkup(keyboard));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			System.out.println("Invalid Arguments.");
		}
	}
	
}
