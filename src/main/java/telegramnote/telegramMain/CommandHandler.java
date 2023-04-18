package telegramnote.telegramMain;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommandHandler {
    private final Map<String, Command> commandMap = new HashMap<>();
    private Command lastCommandWithText = null;
    private StateBot currentState = StateBot.WAITING_COMMAND;
    private final MessageSender messageSender;

    public CommandHandler(@Lazy MessageSender messageSender, List<Command> commands) {
        this.messageSender = messageSender;
        for (Command command : commands) {
            commandMap.put(command.getCommandIdentifier(), command);
        }
    }

    public void handleCommand(Update update) {
        if (currentState == StateBot.WAITING_COMMAND) {
            String messageText = update.getMessage().getText();
            Command command = commandMap.get(messageText);
            if (command != null) {
                command.execute(update);
                if (command instanceof CommandWithText) {
                    lastCommandWithText = command;
                    currentState = StateBot.WAITING_TEXT;
                }
                return;
            }
        } else if (currentState == StateBot.WAITING_TEXT) {
            if (lastCommandWithText != null && lastCommandWithText instanceof CommandWithText commandWithText) {
                commandWithText.executeWithText(update,this);
                return;
            }
        }
        String answer = "Неизвестная команда";
        long chatId = update.getMessage().getChatId();
        messageSender.sendMessage(chatId,answer);

    }

    public void setCurrentState(StateBot currentState) {
        this.currentState = currentState;
    }

    public enum StateBot {
        WAITING_TEXT,
        WAITING_COMMAND,
    }

}
