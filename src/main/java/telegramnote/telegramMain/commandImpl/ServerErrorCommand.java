package telegramnote.telegramMain.commandImpl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramnote.telegramMain.MessageSender;


@Component
public class ServerErrorCommand implements SystemCommand{
    private final MessageSender messageSender;

    public ServerErrorCommand(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void execute(Update update) {
        String answer = "Внутрення ошибка сервера. Повторите попытку позднее";
        long chatId = update.getMessage().getChatId();
        messageSender.sendMessage(chatId, answer,initKeyBoard(update));
    }

    @Override
    public String getCommandIdentifier() {
        return null;
    }
}
