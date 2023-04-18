package telegramnote.telegramMain;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramnote.config.BotConfig;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final CommandHandler commandHandler;

    public TelegramBot(@Lazy CommandHandler commandHandler, BotConfig config) {

        super(config.getToken());
        this.config = config;
        this.commandHandler = commandHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            commandHandler.handleCommand(update);
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
}
