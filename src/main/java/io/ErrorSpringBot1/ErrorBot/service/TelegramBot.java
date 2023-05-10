package io.ErrorSpringBot1.ErrorBot.service;


import com.vdurmont.emoji.EmojiParser;
import io.ErrorSpringBot1.ErrorBot.config.BotConfig;
import io.ErrorSpringBot1.ErrorBot.model.Ads;
import io.ErrorSpringBot1.ErrorBot.model.AdsRepository;

import io.ErrorSpringBot1.ErrorBot.model.User;
import io.ErrorSpringBot1.ErrorBot.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//Главный класс где всё завязанно.
@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdsRepository adsRepository;
    final BotConfig config;

    static final String HELP_TEXT = "Бот создан как проект который должен быть реализован полностью Но...\n\n" +
            "Но многие функции будут работать позже.";


    static final String NOT_FOUND ="Не найдено";
    static final String FOUND = "Найдено";
    static final String YES_BUTTON = "YES_BUTTON";
    static final String TEST = "Отдельное спасибо за тестирование бота и уделённое время ";
    static final String TH = "Спасибо за терпение: Климец Татьяне ";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String REMOVE_USER ="REMOVE_USER";
    static final String ERROR_TEXT = "Error occurred: ";



    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Стар бота"));
        listofCommands.add(new BotCommand("/help", "Информация о боте "));
        listofCommands.add(new BotCommand("/register", "Регистрация"));
        listofCommands.add(new BotCommand("/status", "Статус регистрации"));
        listofCommands.add(new BotCommand("/remove","Удаление регистрационных данных"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.contains("/send") && config.getOwnerId() == chatId) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (User user : users) {
                    prepareAndSendMessage(user.getChatId(), textToSend);
                }

            } else {

                switch (messageText) {
                    case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;

                    case "/help":
                        prepareAndSendMessage(chatId, HELP_TEXT);
                        break;

                    case "/register":
                        register(chatId);
                        break;

                    case "/th":
                        prepareAndSendMessage(chatId, TH);
                        break;

                    case "/test":
                        prepareAndSendMessage(chatId, TEST);
                        break;

                    case "/remove":
                        Unregister (chatId);
                        break;
                    case "/status":
                        statusUser(chatId);
                        break;

                    default:

                        prepareAndSendMessage(chatId, "Нет такой команды");


                }
            }


        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals(YES_BUTTON)) {
                String text = "Успешно";
                executeEditMessageText(text, chatId, messageId);
                registerUser(update.getCallbackQuery().getMessage());


            }
            if (callbackData.equals(NO_BUTTON)) {
                String text = "Отмена";
                executeEditMessageText(text, chatId, messageId);


            } if (callbackData.equals(REMOVE_USER)) {
                String text = "Удалено";
                executeEditMessageText(text, chatId, messageId);
                removeUser(update.getCallbackQuery().getMessage());


            }


        }
    }


    private void register(long chatId) {


        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Вы хотите зарегистрироваться?");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();



        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Да");
        yesButton.setCallbackData(YES_BUTTON);


        var noButton = new InlineKeyboardButton();
        noButton.setText("Нет");
        noButton.setCallbackData(NO_BUTTON);

        var removeButton = new InlineKeyboardButton();
        removeButton.setText("Удалить уже имеющиеся данные");
        removeButton.setCallbackData(REMOVE_USER);



        rowInLine.add(yesButton);
        rowInLine.add(noButton);
        rowInLine.add(removeButton);





        rowsInLine.add(rowInLine);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
        executeMessage(message);

    }

    private void registerUser (Message msg) {
        //Примитивная регистрация и добавление данных в бд.

        if (userRepository.findById(msg.getChatId()).isEmpty()) {

            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setRegStatus(true);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);

            log.info("user saved: " + user);

        }

    }
    private void Unregister(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Вы хотите Удалить данные?");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();



        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Да");
        yesButton.setCallbackData(REMOVE_USER);


        var noButton = new InlineKeyboardButton();
        noButton.setText("Нет");
        noButton.setCallbackData(NO_BUTTON);


        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
        executeMessage(message);

    }

    private void removeUser (Message msg) {
        //Удаление данных с бд!

        var chatId = msg.getChatId();
        var user = userRepository.findById(chatId);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            log.info("user deleted: " + user.get());
        } else {
            log.info("user with chatId " + chatId + " not found");
        }
    }
private void statusUser (long chatId){
        if (userRepository.existsById(chatId)){
            prepareAndSendMessage(chatId, FOUND);
        }
        else {
            prepareAndSendMessage(chatId,NOT_FOUND);
        }


}












    private void startCommandReceived(long chatId, String name) {

        String answer = "Привет " + name;
        log.info("Replied to user " + name);


        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {


        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        keyboardRows.add(row);
        row = new KeyboardRow();

        row.add("/start");
        row.add("/help");
        row.add("/register");
        row.add("/remove");
        row.add("/status");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }


    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);


        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    @Scheduled(cron = "${cron.scheduler}")
    private void sendAds() {

        var ads = adsRepository.findAll();
        var users = userRepository.findAll();

        for (Ads ad : ads) {
            for (User user : users) {
                prepareAndSendMessage(user.getChatId(), ad.getAd());
            }
        }

    }
}
