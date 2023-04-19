package telegramnote.telegramMain;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public interface Command {
    void execute(Update update);
    String getCommandIdentifier();

    default ReplyKeyboard initKeyBoard(Object data) {
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

        KeyboardRow rowHelp = new KeyboardRow();
        rowHelp.add(new KeyboardButton("Помощь"));
        keyboard.add(rowHelp);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
