package telegramnote.telegramMain.commandImpl.note;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import telegramnote.data.CustomResponse;
import telegramnote.data.dto.Note;
import telegramnote.service.RestServiceInterface;
import telegramnote.telegramMain.Command;
import telegramnote.telegramMain.MessageSender;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetNoteCommand implements Command {
    private final RestServiceInterface restService;
    private final MessageSender messageSender;

    public GetNoteCommand(RestServiceInterface restService, @Lazy MessageSender messageSender) {
        this.restService = restService;
        this.messageSender = messageSender;
    }

    @Override
    public void execute(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long noteId = Long.valueOf(getDataFromCallBack(update.getCallbackQuery().getData()));
        CustomResponse<Note> response = restService.getNote(chatId,noteId);
        if (response.isSuccess()) {
            String text = "Заметка " + response.getBody().getLabel() +
                    "\n\n" + response.getBody().getText();
            messageSender.answerCallBack(update.getCallbackQuery(), null);
            messageSender.sendMessage(chatId,text,initKeyBoard(response.getBody()));
        } else {
            messageSender.answerCallBack(update.getCallbackQuery(),response.getErrorMessage());
        }
    }



    @Override
    public String getCommandIdentifier() {
        return "get_note";
    }

    private String getDataFromCallBack(String callBackData) {
        return callBackData.substring(callBackData.indexOf(": ") + 2);
    }


    @Override
    public ReplyKeyboard initKeyBoard(Object data) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(new ArrayList<>());
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Удалить заметку");
        button.setCallbackData("delete_note: id=" + ((Note) data).getId());
        keyboard.get(0).add(button);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
