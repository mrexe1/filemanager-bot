package me.shib.java.app.telegram.bot.filemanager.main;

import me.shib.java.lib.telegram.bot.service.TelegramBot;
import me.shib.java.lib.telegram.bot.service.TelegramBot.ChatAction;
import me.shib.java.lib.telegram.bot.types.ChatId;

public class ChatActionHandler extends Thread {

    private TelegramBot tbs;
    private ChatId chatId;
    private ChatAction chatAction;
    private boolean chatActionAlive;

    public ChatActionHandler(TelegramBot tbs, ChatId chatId, ChatAction chatAction) {
        this.tbs = tbs;
        this.chatId = chatId;
        this.chatAction = chatAction;
        chatActionAlive = true;
    }

    private void startAction() {
        while (chatActionAlive) {
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
