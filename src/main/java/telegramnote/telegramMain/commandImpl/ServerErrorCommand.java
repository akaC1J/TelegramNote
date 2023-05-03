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
        long chatId = -1;
        if (update.getMessage() != null) {
            chatId = update.getMessage().getChatId();

        } else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        String answer = "Внутрення ошибка сервера. Повторите попытку позднее";
        messageSender.sendMessage(chatId, answer,initKeyBoard(update));
    }

    @Override
    public String getCommandIdentifier() {
        return null;
    }
}
