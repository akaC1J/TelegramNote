package telegramnote.telegramMain.commandImpl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramnote.telegramMain.Command;
import telegramnote.telegramMain.MessageSender;

@Component
public class UnknownCommand implements Command {
    private final MessageSender messageSender;

    public UnknownCommand(@Lazy MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void execute(Update update) {
        String answer = "Неизвестная команда";
        long chatId = update.getMessage().getChatId();
        messageSender.sendMessage(chatId, answer,initKeyBoard(update));
    }

    @Override
    public String getCommandIdentifier() {
        return null;
    }
}
