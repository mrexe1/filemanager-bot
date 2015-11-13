package me.shib.java.telegram.filemanager.main;

import java.io.File;
import java.util.Date;

import me.shib.java.telegram.bot.service.TelegramBotService;
import me.shib.java.telegram.bot.service.TelegramBotService.ChatAction;
import me.shib.java.telegram.bot.types.ChatId;
import me.shib.java.telegram.bot.types.Message;
import me.shib.java.telegram.bot.types.ReplyKeyboardMarkup;
import me.shib.java.telegram.bot.types.TelegramFile;
import me.shib.java.telegram.bot.worker.TBotModel;
import me.shib.java.telegram.filemanager.navigator.KeyBoardAndResponseText;
import me.shib.java.telegram.filemanager.navigator.UserBase;
import me.shib.java.telegram.filemanager.navigator.UserDir;

public class FileManagerBotModel implements TBotModel {
	
	private static final long maxFileSize = 50000000;
	private ChatActionHandler cah;
	
	private static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	private String getFileInfo(File file) {
		StringBuilder fileInfoBuilder = new StringBuilder();
		fileInfoBuilder.append("Name: " + file.getName() + "\n");
		fileInfoBuilder.append("Size: " + humanReadableByteCount(file.length(), false) + "\n");
		fileInfoBuilder.append("Last Modified: " + new Date(file.lastModified()));
		return fileInfoBuilder.toString();
	}
	
	private void startChatAction(TelegramBotService tBotService, ChatId chatId, ChatAction chatAction) {
		cah = new ChatActionHandler(tBotService, chatId, chatAction);
		cah.start();
	}
	
	private void endChatAction() {
		cah.endAction();
	}

	@Override
	public Message onReceivingMessage(TelegramBotService tBotService, Message message) {
		Message returnMessage = null;
		try {
			if(message.getText() == null) {
				tBotService.sendMessage(new ChatId(message.getChat().getId()), "Please input a text");
			}
			else {
				UserDir ud = UserBase.getUserDir(message.getChat().getId());
				ud.navigate(message.getText());
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
				String consumableSuggestionMessage = ud.getConsumableSearchSuggestion();
				if(fileToSend != null) {
					int sentMessageId = 0;
					returnMessage = tBotService.sendMessage(new ChatId(ud.getUserId()), "File Info:\n" + getFileInfo(fileToSend), null, true);
					if(returnMessage != null) {
						sentMessageId = returnMessage.getMessage_id();
					}
					if(fileToSend.length() > maxFileSize) {
						returnMessage = tBotService.sendMessage(new ChatId(ud.getUserId()), "The file you requested is larger in size than the permissible limit.", null, true, sentMessageId);
					}
					else {
						returnMessage = tBotService.sendMessage(new ChatId(ud.getUserId()), "Please wait while the file is being sent..." + getFileInfo(fileToSend), null, true, sentMessageId);
						startChatAction(tBotService, new ChatId(ud.getUserId()), ChatAction.upload_document);
						returnMessage = tBotService.sendDocument(new ChatId(ud.getUserId()), new TelegramFile(fileToSend), sentMessageId);
						endChatAction();
					}
				}
				if(consumableSuggestionMessage != null) {
					returnMessage = tBotService.sendMessage(new ChatId(ud.getUserId()), consumableSuggestionMessage, null, true);
				}
				else {
					returnMessage = tBotService.sendMessage(new ChatId(ud.getUserId()), kbt.getResponse(), null, true, 0, new ReplyKeyboardMarkup(keyboard, true, true));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return returnMessage;
	}

	@Override
	public Message onMessageFromAdmin(TelegramBotService tBotService, Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message customSupportHandler(TelegramBotService tBotService, Message message) {
		// TODO Auto-generated method stub
		return null;
	}

}
