package me.shib.java.telegram.filemanager.worker;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import com.nudanam.java.telegram.bot.service.TelegramBotService;
import com.nudanam.java.telegram.bot.types.Update;

public class MessageListener {
	
private static final String botApiToken = ConfigManager.config().botApiToken();
	
	private static Queue<Update> updatesQueue = new LinkedList<Update>();
	private static TelegramBotService updateReceiver = new TelegramBotService(botApiToken);
	
	private static void fillUpdatesQueue() {
		try {
			Update[] updates = updateReceiver.getUpdates();
			for(Update u : updates) {
				updatesQueue.add(u);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized Update getUpdate() {
		Update update = updatesQueue.poll();
		while(update == null) {
			fillUpdatesQueue();
			update = updatesQueue.poll();
		}
		return update;
	}
	
}
