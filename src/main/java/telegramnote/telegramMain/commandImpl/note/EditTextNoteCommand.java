package telegramnote.telegramMain.commandImpl.note;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import telegramnote.data.CustomResponse;
import telegramnote.data.dto.Note;
import telegramnote.data.dto.User_;
import telegramnote.service.RestServiceInterface;
import telegramnote.telegramMain.CommandHandler;
import telegramnote.telegramMain.CommandWithText;
import telegramnote.telegramMain.MessageSender;
import telegramnote.telegramMain.commandImpl.model.DataChatId;

@SuppressWarnings("DuplicatedCode")
@Component

public class EditTextNoteCommand implements CommandWithText {

    private final MessageSender messageSender;
    private final RestServiceInterface restService;
    private final DataChatId<Note> noteDataChatId = new DataChatId<>();
    public EditTextNoteCommand(@Lazy MessageSender messageSender, RestServiceInterface restService) {
        this.messageSender = messageSender;
        this.restService = restService;
    }

    @Override
    public void execute(Update update) {
        String answer = "Введите текст заметки";
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long noteId = getIdFromData(update.getCallbackQuery().getData());
        Note note = new Note();
        note.setId(noteId);
        note.setUser(new User_().setChatId(chatId));
        noteDataChatId.setData(chatId, note);
        messageSender.sendMessage(chatId,answer);
        messageSender.answerCallBack(update.getCallbackQuery(), null);

    }

    @Override
    public String getCommandIdentifier() {
        return "edit_text_note";
    }

    @Override
    public ReplyKeyboard initKeyBoard(Object data) {
        return CommandWithText.super.initKeyBoard(data);
    }

    private Long getIdFromData(String callBackData) {
        return Long.valueOf(callBackData.substring(callBackData.indexOf("id=") + "id=".length()));
    }

    @Override
    public void executeWithText(Update update, CommandHandler context) {
        Long chatId = update.getMessage().getChatId();
        String textNote = update.getMessage().getText();
        Note localNote = noteDataChatId.getData(chatId);
        localNote.setText(textNote);
        CustomResponse<Note> response = restService.patchNote(localNote);
        String answer;
        if (!response.isSuccess()) {
            answer = "Не удалось изменить заметку \"" + localNote.getLabel() + "\"." +
                    "\nПричина: " + response.getErrorMessage();
        } else {
            answer = "Текст успешно изменен";
        }
        messageSender.sendMessage(chatId, answer, initKeyBoard(update));
        context.setCurrentState(chatId, CommandHandler.StateBot.WAITING_COMMAND);
    }
}
