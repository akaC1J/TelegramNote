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

@Component//dsfsadads213213adsd12
public class CreateNoteCommand implements CommandWithText {
    private final RestServiceInterface restService;
    private final MessageSender messageSender;

    private Step currentStep = Step.LABEL;
    private Note currentNote;


    public CreateNoteCommand(RestServiceInterface restService, @Lazy MessageSender messageSender) {
        this.restService = restService;
        this.messageSender = messageSender;
    }

    @Override
    public void execute(Update update) {
        String answer = "Введите название заметки";
        long chatId = update.getMessage().getChatId();
        messageSender.sendMessage(chatId,answer);
        currentStep = Step.LABEL;
        currentNote = null;
    }

    @Override
    public String getCommandIdentifier() {
        return "Создать заметку";
    }

    @Override
    public void executeWithText(Update update, CommandHandler context) {
        switch (currentStep) {
            case LABEL -> {
                String answer = "Введите текст заметки:";
                String label = update.getMessage().getText();
                Long chatId = update.getMessage().getChatId();
                currentNote = new Note();
                currentNote.setLabel(label);
                currentNote.setChatId(chatId);
                messageSender.sendMessage(chatId,answer);
                currentStep = Step.TEXT;
                context.setCurrentState(CommandHandler.StateBot.WAITING_TEXT);
            }
            case TEXT -> {
                String answer;
                String text = update.getMessage().getText();
                currentNote.setText(text);
                User_ user = new User_();
                user.setChatId(currentNote.getChatId());
                currentNote.setUser(user);
                CustomResponse<Note> response = restService.postNote(currentNote);
                if (!response.isSuccess()) {
                    answer = "Не удалось сохранить заметку \"" + currentNote.getLabel() + "\"." +
                            "\nПричина: " + response.getErrorMessage();
                } else {
                    answer = "Заметка \"" + response.getBody().getLabel() + "\" успешно сохранено";
                }
                messageSender.sendMessage(currentNote.getChatId(), answer, initKeyBoard(update));
                currentStep = Step.LABEL;
                context.setCurrentState(CommandHandler.StateBot.WAITING_COMMAND);
                currentNote = null;
            }

        }
    }

    private enum Step {
        LABEL, TEXT,
    }

}
