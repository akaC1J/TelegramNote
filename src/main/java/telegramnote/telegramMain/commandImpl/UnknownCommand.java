package telegramnote.telegramMain.commandImpl;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramnote.telegramMain.MessageSender;

@Component

public class UnknownCommand implements SystemCommand {
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
