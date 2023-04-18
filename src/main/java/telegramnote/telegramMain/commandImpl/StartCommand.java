package telegramnote.telegramMain.commandImpl;

import org.springframework.context.annotation.Lazy;
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
        String name = update.getMessage().getChat().getFirstName();
        long chatId = update.getMessage().getChatId();
        User_ newUser = new User_(chatId,name);
        CustomResponse<String> register = restService.register(newUser);
        String answer;
        if (register.isSuccess()) {
            answer = "Привет " + name + " приятно познакомиться!";
        } else {
            answer = "Ошибка! Причина: " + register.getErrorMessage();
        }
        messageSender.sendMessage(chatId, answer, initKeyBoard());

    }

    @Override
    public String getCommandIdentifier() {
        return "/start";
    }


}
