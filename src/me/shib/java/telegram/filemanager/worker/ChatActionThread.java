package me.shib.java.telegram.filemanager.worker;

import com.nudanam.java.telegram.bot.service.TelegramBotService;
import com.nudanam.java.telegram.bot.service.TelegramBotService.ChatAction;

public class ChatActionThread extends Thread {
	
	private TelegramBotService tbs;
	private long chatId;
	private ChatAction chatAction;
	private boolean chatActionAlive;
	
	public ChatActionThread(TelegramBotService tbs, long chatId, ChatAction chatAction) {
		this.tbs = tbs;
		this.chatId = chatId;
		this.chatAction = chatAction;
		chatActionAlive = true;
	}
	
	private void startAction() {
		while(chatActionAlive) {
			try {
				tbs.sendChatAction(chatId, chatAction);
				Thread.sleep(4444);
			} catch (Exception e) {
				chatActionAlive = false;
			}
		}
	}
	
	public void run() {
		startAction();
	}
	
	public void endAction() {
		chatActionAlive = false;
	}
	
}
