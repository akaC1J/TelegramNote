package telegramnote.telegramMain.commandImpl;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramnote.data.CustomResponse;
import telegramnote.data.dto.User_;
import telegramnote.service.RestServiceInterface;
import telegramnote.telegramMain.Command;
import telegramnote.telegramMain.MessageSender;

@Component
public class StartCommand implements Command {
    private final RestServiceInterface restService;
    private final MessageSender messageSender;

    public StartCommand(RestServiceInterface restService, @Lazy MessageSender messageSender) {
        this.restService = restService;
        this.messageSender = messageSender;
    }

    @Override
    public void execute(Update update) {
        String answer = null;
        String name = update.getMessage().getChat().getFirstName();
        long chatId = update.getMessage().getChatId();
        CustomResponse<User_> responseFindUser = restService.getUser(chatId);
        if (responseFindUser.isSuccess()) {
            answer = "Привет " + responseFindUser.getBody().getName() + " давно не виделись!";
        } else if (responseFindUser.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            CustomResponse<User_> responseRegister = restService.postUser(new User_(chatId, name));
            if (responseRegister.isSuccess()) {
                answer = "Привет " + responseRegister.getBody().getName() + " приятно познакомиться!";
            } else {
                answer = "Ошибка! Причина: " + responseRegister.getErrorMessage();
            }
        }
        messageSender.sendMessage(chatId, answer, initKeyBoard(update));

    }

    @Override
    public String getCommandIdentifier() {
        return "/start";
    }


}
