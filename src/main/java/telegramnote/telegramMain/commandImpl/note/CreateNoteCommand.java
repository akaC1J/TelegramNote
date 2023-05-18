package telegramnote.telegramMain.commandImpl.note;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramnote.data.CustomResponse;
import telegramnote.data.dto.Note;
import telegramnote.data.dto.User_;
import telegramnote.service.RestServiceInterface;
import telegramnote.telegramMain.CommandHandler;
import telegramnote.telegramMain.CommandWithText;
import telegramnote.telegramMain.MessageSender;
import telegramnote.telegramMain.commandImpl.model.DataChatId;

@Component

public class CreateNoteCommand implements CommandWithText {
    private final RestServiceInterface restService;
    private final MessageSender messageSender;

    private final DataChatId<Step> currentStep = new DataChatId<>(Step.LABEL);
    private final DataChatId<Note> currentNote = new DataChatId<>();


    public CreateNoteCommand(RestServiceInterface restService, @Lazy MessageSender messageSender) {
        this.restService = restService;
        this.messageSender = messageSender;
    }

    @Override
    public void execute(Update update) {
        String answer = "Введите название заметки";
        long chatId = update.getMessage().getChatId();
        messageSender.sendMessage(chatId,answer);
        currentStep.setData(chatId,Step.LABEL);
        currentNote.setData(chatId,null);
    }

    @Override
    public String getCommandIdentifier() {
        return "Создать заметку";
    }

    @Override
    public void executeWithText(Update update, CommandHandler context) {
        Long chatId = update.getMessage().getChatId();
        switch (currentStep.getData(chatId)) {
            case LABEL -> {
                String answer = "Введите текст заметки:";
                String label = update.getMessage().getText();
                Note localNote = new Note();
                localNote.setLabel(label);
                localNote.setUser(new User_().setChatId(chatId));
                currentNote.setData(chatId,localNote);
                messageSender.sendMessage(chatId,answer);
                currentStep.setData(chatId,Step.TEXT);
                context.setCurrentState(chatId, CommandHandler.StateBot.WAITING_TEXT);
            }
            case TEXT -> {
                String answer;
                String text = update.getMessage().getText();
                Note localNote = currentNote.getData(chatId);
                localNote.setText(text);
                localNote.setUser(new User_().setChatId(chatId));
                CustomResponse<Note> response = restService.postNote(localNote);
                if (!response.isSuccess()) {
                    answer = "Не удалось сохранить заметку \"" + localNote.getLabel() + "\"." +
                            "\nПричина: " + response.getErrorMessage();
                } else {
                    answer = "Заметка \"" + response.getBody().getLabel() + "\" успешно сохранено";
                }
                messageSender.sendMessage(chatId, answer, initKeyBoard(update));
                currentStep.setData(chatId, Step.LABEL);
                context.setCurrentState(chatId, CommandHandler.StateBot.WAITING_COMMAND);
                currentNote.setData(chatId, null);
            }

        }
    }

    private enum Step {
        LABEL, TEXT,
    }

}
