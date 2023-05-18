package telegramnote.telegramMain.commandImpl.note;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramnote.data.CustomResponse;
import telegramnote.service.RestServiceInterface;
import telegramnote.telegramMain.Command;
import telegramnote.telegramMain.MessageSender;

@Component

public class DeleteNoteCommand implements Command {

    private final MessageSender messageSender;
    private final RestServiceInterface restService;

    public DeleteNoteCommand(@Lazy MessageSender messageSender, RestServiceInterface restService) {
        this.messageSender = messageSender;
        this.restService = restService;
    }

    @Override
    public void execute(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long deleteId = getIdFromData(update.getCallbackQuery().getData());
        CustomResponse<Void> response = restService.deleteNote(deleteId,chatId);
        messageSender.answerCallBack(update.getCallbackQuery(), null);
        if (response.isSuccess()) {
            String text = "Заметка успешно удалена";
            messageSender.sendMessage(chatId,text);
        } else {
             messageSender.sendMessage(chatId, response.getErrorMessage());
        }
    }

    @Override
    public String getCommandIdentifier() {
        return "delete_note";
    }

    private Long getIdFromData(String callBackData) {
        return Long.valueOf(callBackData.substring(callBackData.indexOf("id=") + "id=".length()));
    }
}
