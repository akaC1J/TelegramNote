package telegramnote.telegramMain;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandWithText extends Command {
    void executeWithText(Update update, CommandHandler context);
}
