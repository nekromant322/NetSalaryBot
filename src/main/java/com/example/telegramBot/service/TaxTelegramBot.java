package com.example.telegramBot.service;

import com.example.telegramBot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TaxTelegramBot extends TelegramLongPollingBot {
    @Autowired
    private TaxService taxService;
    @Autowired
    private BotConfig botConfig;
    private static final Logger logger = LoggerFactory.getLogger(TaxTelegramBot.class);

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotKey();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            if (update.getMessage().isGroupMessage() || update.getMessage().isSuperGroupMessage()) {
                if (messageText.contains("@" + botConfig.getBotName())) {
                    handleMention(update, messageText);
                }
            } else {
                handleDirectMessage(update, messageText);
            }
        }
    }

    private void handleMention(Update update, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(update.getMessage().getChatId()));
        message.enableMarkdown(true);

        String[] parts = messageText.split("\\s+");
        boolean numberFound = false;
        for (String part : parts) {
            String salaryStr = parseSalary(part);
            if (salaryStr != null) {
                int salary = convertSalaryToInt(salaryStr);
                processSalary(update, salary, salaryStr);
                numberFound = true;
                break;
            }
        }
        if (!numberFound) {
            message.setText("Пожалуйста, укажите сумму зарплаты в числовом формате (число должно быть положительным) после упоминания бота.");
            sendResponse(update, message);
        }
    }

    private void handleDirectMessage(Update update, String messageText) {
        String salaryStr = parseSalary(messageText);
        if (salaryStr != null) {
            int salary = convertSalaryToInt(salaryStr);
            processSalary(update, salary, salaryStr);
        } else {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            message.enableMarkdown(true);
            message.setText("Пожалуйста, отправьте зарплату (зарплата должна быть положительной) в числовом формате");
            sendResponse(update, message);
        }
    }

    private void processSalary(Update update, int salary, String originalFormat) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(update.getMessage().getChatId()));
        message.enableMarkdown(true);

        int tax = taxService.countTax(salary);
        int finalSalary = salary - tax;
        String responseText;
        if (originalFormat.endsWith("к")) {
            responseText = "*Зарплата net:* " + (finalSalary / 1000) + "к";
        } else if (originalFormat.endsWith("k")) {
            responseText = "*Зарплата net:* " + (finalSalary / 1000) + "k";
        } else {
            responseText = "*Зарплата net:* " + finalSalary;
        }

        message.setText(responseText);
        sendResponse(update, message);
    }

    private void sendResponse(Update update, SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error sending message", e);
        }
    }

    private String parseSalary(String input) {
        if (input.matches("\\d+(\\.\\d+)?[кk]?")) {
            return input;
        } else {
            return null;
        }
    }
    private int convertSalaryToInt(String salaryStr) {
        if (salaryStr.toLowerCase().endsWith("к") || salaryStr.toLowerCase().endsWith("k")) {
            salaryStr = salaryStr.substring(0, salaryStr.length() - 1);
            return Integer.parseInt(salaryStr) * 1000;
        } else {
            return Integer.parseInt(salaryStr);
        }
    }
}
