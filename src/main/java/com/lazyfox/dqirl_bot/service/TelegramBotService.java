package com.lazyfox.dqirl_bot.service;

import com.lazyfox.dqirl_bot.configuration.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot {
    private final BotConfig config;
//    private User user;

    public TelegramBotService(BotConfig config) {
        this.config = config;
        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/start", "hello bot"));
        botCommandList.add(new BotCommand("/mydata", "get telegram data"));
        botCommandList.add(new BotCommand("/info", "get info"));
        try {
            this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Произошла ошибка: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        if (update.hasMessage() && update.getMessage().hasText()) {
            switch (messageText) {
                case "/start":
                    start(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/mydata":
                    showProfile(chatId, update.getMessage().getChat());
                    break;
                case "/info":
                    infoCommand(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    sendMessage(chatId, "Введите существующую команду");
            }
        }

    }

    public void start(long chatId, String userName) {
        String result = "Привет " + userName;
        log.info("Чат с " + userName + " вызвал команду /start ");
        sendMessage(chatId, result);
    }

    public void infoCommand(long chatId, String userName) {
        String result = "Данный бот предназначен для тетстирования аутентификация пользователя и проверки защиты его данных";
        log.info("Чат с " + userName + " вызвал команду /info");
        sendMessage(chatId, result);
    }

    public void showProfile(long chatId, Chat chat) {
        String result = "First Name: " + chat.getFirstName() + "\nLast Name: " + chat.getLastName() + "\nUser Name: " + chat.getUserName() + "\nBio: " + chat.getBio() + "\nэто данные пользователя которые можно узнать и хранить автоматически";
        log.info("Чат с " + chat.getFirstName() + " вызвал команду /mydata");
        sendMessage(chatId, result);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Произошла ошибка: " + e.getMessage());
        }
    }

}
