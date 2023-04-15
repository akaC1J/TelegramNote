package telegramnote.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegramnote.config.BotConfig;
import telegramnote.data.CustomResponse;
import telegramnote.data.dto.User_;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final RestService restService;

    public TelegramBot(BotConfig config, RestService restService) {
        super(config.getToken());
        this.config = config;
        this.restService = restService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText){
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    String answer = "Неизвестная команда";
                    sendMessage(chatId, answer);
            }
        }
    }

    private ReplyKeyboardMarkup initKeyBoard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        KeyboardButton button = new KeyboardButton();
        button.setText("Создать заметку");
        row.add(button);

        button = new KeyboardButton();
        button.setText("Посмотреть заметки");
        row.add(button);
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Привет " + name + " приятно познакомиться!";
        User_ newUser = new User_(chatId,name);
        CustomResponse<String> register = restService.register(newUser);
        if (register.isSuccess()) {
            sendMessage(chatId, answer, initKeyBoard());
        }

    }

    private void sendMessage(Long chatId, String textToSend, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        sendMessage.setReplyMarkup(keyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(Long chatId, String textToSend){
        sendMessage(chatId, textToSend, null);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }


}
