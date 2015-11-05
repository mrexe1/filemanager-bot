package me.shib.java.telegram.filemanager.worker;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.nudanam.java.telegram.bot.service.TelegramBotService;
import com.nudanam.java.telegram.bot.service.TelegramBotService.ChatAction;
import com.nudanam.java.telegram.bot.types.Message;
import com.nudanam.java.telegram.bot.types.ReplyKeyboardMarkup;
import com.nudanam.java.telegram.bot.types.Update;

import me.shib.java.telegram.filemanager.navigator.KeyBoardAndResponseText;
import me.shib.java.telegram.filemanager.navigator.UserDir;

public class FileManagerThread extends Thread {
	
	private static final long maxFileSize = 50000000;
	
	private TelegramBotService tbs;
	
	public FileManagerThread() {
		tbs = new TelegramBotService(ConfigManager.config().botApiToken());
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
					String[] navigationButtons = null;
					if(ud.isShowNextButton() && ud.isShowPrevButton()) {
						navigationButtons = new String[4];
						navigationButtons[2] = "/previous";
						navigationButtons[3] = "/next";
					}
					else if(ud.isShowNextButton() || ud.isShowPrevButton()) {
						navigationButtons = new String[3];
						if(ud.isShowPrevButton()) {
							navigationButtons[2] = "/previous";
						}
						else {
							navigationButtons[2] = "/next";
						}
					}
					else {
						navigationButtons = new String[2];
					}
					navigationButtons[0] = "/home";
					navigationButtons[1] = "/back";
					String[][] keyboard = new String[fileNameList.length + 1][1];
					keyboard[0] = navigationButtons;
					for(int i = 0; i < fileNameList.length; i++) {
						keyboard[i + 1][0] = fileNameList[i];
					}
					File fileToSend = ud.getFile();
					if(fileToSend != null) {
						int sentMessageId = 0;
						Message sentMessage = tbs.sendMessage(ud.getUserId(), "File Info:\n" + getFileInfo(fileToSend), null, true);
						if(sentMessage != null) {
							sentMessageId = sentMessage.getMessage_id();
						}
						if(fileToSend.length() > maxFileSize) {
							tbs.sendMessage(ud.getUserId(), "The file you requested is larger in size than the permissible limit.", null, true, sentMessageId);
						}
						else {
							tbs.sendMessage(ud.getUserId(), "Please wait while the file is being sent..." + getFileInfo(fileToSend), null, true, sentMessageId);
							tbs.sendChatAction(ud.getUserId(), ChatAction.upload_document);
							tbs.sendDocument(ud.getUserId(), fileToSend, sentMessageId);
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
