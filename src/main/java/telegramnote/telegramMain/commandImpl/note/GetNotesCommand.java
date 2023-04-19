package telegramnote.telegramMain.commandImpl.note;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import telegramnote.data.CustomResponse;
import telegramnote.data.dto.Note;
import telegramnote.service.RestServiceInterface;
import telegramnote.telegramMain.Command;
import telegramnote.telegramMain.MessageSender;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetNotesCommand implements Command {

    private final RestServiceInterface restService;
    private final MessageSender messageSender;

    private List<Note> notes;

    public GetNotesCommand(RestServiceInterface restService, @Lazy MessageSender messageSender) {
        this.restService = restService;
        this.messageSender = messageSender;
    }

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        CustomResponse<List<Note>> response = restService.getNotes(chatId);
        notes = response.getBody();
        messageSender.sendMessage(chatId,"answer text",initKeyBoard());
    }

    @Override
    public String getCommandIdentifier() {
        return "Посмотреть заметки";
    }

    @Override
    public InlineKeyboardMarkup initKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Note noteTitle : notes) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(noteTitle.getLabel());
            button.setCallbackData("note:" + noteTitle.getLabel());
            row.add(button);
            keyboard.add(row);
        }
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
