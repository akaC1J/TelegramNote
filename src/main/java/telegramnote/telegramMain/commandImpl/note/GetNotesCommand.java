package telegramnote.telegramMain.commandImpl.note;

import org.springframework.beans.factory.annotation.Value;
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
import telegramnote.telegramMain.commandImpl.model.DataChatId;

import java.util.ArrayList;
import java.util.List;

@Component

public class GetNotesCommand implements Command {

    private final RestServiceInterface restService;
    private final MessageSender messageSender;

    private final DataChatId<List<Note>> notes = new DataChatId<>(new ArrayList<>());

    @Value("${bot.pagesize}")
    private int pageSize;
    private int currentOffSet = 0;

    public GetNotesCommand(RestServiceInterface restService, @Lazy MessageSender messageSender) {
        this.restService = restService;
        this.messageSender = messageSender;
    }

    @Override
    public void execute(Update update) {
        Long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            currentOffSet = 0;
        } else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            currentOffSet = getOffsetFromData(update.getCallbackQuery().getData());
            messageSender.answerCallBack(update.getCallbackQuery(),null);
        }
        CustomResponse<List<Note>> response = restService.getNotes(chatId, pageSize, currentOffSet);
        if (response.isSuccess()) {
            List<Note> localNotes = response.getBody();
            notes.setData(chatId, localNotes);
            if (localNotes.size() == 0) {
                messageSender.sendMessage(chatId, "Не добавлено ни одной заметки");
                return;
            }
            int start = currentOffSet * pageSize + 1;
            int end = start + pageSize - 1;
            messageSender.sendMessage(chatId, "Заметки с " + start + " по " + end, initKeyBoard(update));
        } else {
            messageSender.sendMessage(chatId,response.getErrorMessage());
        }

    }

    @Override
    public String getCommandIdentifier() {
        return "Посмотреть заметки";
    }

    @Override
    public InlineKeyboardMarkup initKeyBoard(Object data) {
        Long chatId = ((Update) data).hasMessage() ? ((Update) data).getMessage().getChatId() :
                ((Update) data).getCallbackQuery().getMessage().getChatId();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<Note> localNotes = notes.getData(chatId);
        for (Note note : localNotes) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(note.getLabel());
            button.setCallbackData("get_note: " + note.getId());
            row.add(button);
            keyboard.add(row);
        }

        CustomResponse<Integer> response = restService.countNotesForChatId(chatId);
        if (response.isSuccess()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            int maxOffset = (response.getBody() - 1) / pageSize;
            if (currentOffSet > 0) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("<--");
                button.setCallbackData("get_notes: " + "offset=" + (currentOffSet - 1));
                row.add(button);

            }
            if (maxOffset >= currentOffSet + 1) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("-->");
                button.setCallbackData("get_notes: " + "offset=" + (currentOffSet + 1));
                row.add(button);
            }
            keyboard.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    private int getOffsetFromData(String callBackData) {
        return Integer.parseInt(callBackData.substring(callBackData.indexOf("offset=") + "offset=".length()));
    }

}
