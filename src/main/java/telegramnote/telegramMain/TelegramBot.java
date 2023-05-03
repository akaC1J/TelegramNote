package telegramnote.telegramMain;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramnote.config.BotConfig;
import telegramnote.telegramMain.commandImpl.ServerErrorCommand;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final CommandHandler commandHandler;
    private final ServerErrorCommand errorCommand;


    public TelegramBot(@Lazy CommandHandler commandHandler, BotConfig config, @Lazy ServerErrorCommand errorCommand) {

        super(config.getToken());
        this.config = config;
        this.commandHandler = commandHandler;
        this.errorCommand = errorCommand;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() ||
                update.hasCallbackQuery() && update.getCallbackQuery().getData() != null) {
            try {
                commandHandler.handleCommand(update);
            } catch (ResourceAccessException e) {
                errorCommand.execute(update);
                long chatId = update.hasMessage() ? update.getMessage().getChatId() :
                        update.getCallbackQuery().getMessage().getChatId();
                commandHandler.setCurrentState(chatId, CommandHandler.StateBot.WAITING_COMMAND);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
}
