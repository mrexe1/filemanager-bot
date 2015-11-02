package me.shib.java.telegram.filemanager.worker;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.nudanam.java.telegram.bot.service.TelegramBotService;
import com.nudanam.java.telegram.bot.types.Message;
import com.nudanam.java.telegram.bot.types.ReplyKeyboardMarkup;
import com.nudanam.java.telegram.bot.types.Update;

import me.shib.java.telegram.filemanager.navigator.KeyBoardAndResponseText;
import me.shib.java.telegram.filemanager.navigator.UserDir;

public class FileManagerThread extends Thread {
	
	private TelegramBotService tbs;
	
	public FileManagerThread() {
		tbs = new TelegramBotService(Config.botApiToken);
	}
	
	private String getFileInfo(File file) {
		StringBuilder fileInfoBuilder = new StringBuilder();
		fileInfoBuilder.append("Name: " + file.getName() + "\n");
		long totalSize = file.length();
		String size = "0";
		if(totalSize < 1024) {
			size = totalSize + "Bytes";
		}
		else if(totalSize < 1048576) {
			size = (int)(totalSize / 1024) + "kB";
		}
		else {
			size = (int)(totalSize / 1048576) + "MB";
		}
		fileInfoBuilder.append("Size: " + size + "\n");
		fileInfoBuilder.append("Last Modified: " + new Date(file.lastModified()));
		return fileInfoBuilder.toString();
	}
	
	protected void processFileRequests() {
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
					File fileToSend = ud.getFile();
					if(fileToSend != null) {
						Message sentMessage;
						if(fileToSend.length() > Config.maxFileSize) {
							sentMessage = tbs.sendMessage(ud.getUserId(), "The file size you have requested is larger than the permissible limit.");
						}
						else {
							sentMessage = tbs.sendDocument(ud.getUserId(), fileToSend);
						}
						if(sentMessage != null) {
							tbs.sendMessage(ud.getUserId(), "File Info:\n" + getFileInfo(fileToSend), null, true, sentMessage.getMessage_id());
						}
					}
					tbs.sendMessage(ud.getUserId(), kbt.getResponse(), null, true, 0, new ReplyKeyboardMarkup(keyboard, true, true));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void run() {
		processFileRequests();
	}
	
}
