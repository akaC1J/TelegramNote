package telegramnote.telegramMain;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramnote.telegramMain.commandImpl.UnknownCommand;
import telegramnote.telegramMain.commandImpl.model.DataChatId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommandHandler {
    private final Map<String, Command> commandMap = new HashMap<>();
    private final UnknownCommand unknownCommand;
    private DataChatId<Command> lastCommandWithText = new DataChatId<>();
    private DataChatId<StateBot> currentState = new DataChatId<>(StateBot.WAITING_COMMAND);
    private final MessageSender messageSender;

    public CommandHandler(@Lazy MessageSender messageSender, List<Command> commands,
                          UnknownCommand unknownCommand) {
        this.messageSender = messageSender;
        this.unknownCommand = unknownCommand;
        for (Command command : commands) {
            if (command instanceof UnknownCommand) {
                continue;
            }
            commandMap.put(command.getCommandIdentifier(), command);
        }
    }

    public void handleCommand(Update update) {
        long chatId = update.hasMessage() ? update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();
        if (currentState.getData(chatId) == StateBot.WAITING_COMMAND) {
            String commandId;
            if (update.hasMessage()) {
                commandId = update.getMessage().getText();
            } else {
                commandId = getCommandIdFromCallBackData(update.getCallbackQuery().getData());
            }
            Command command = commandMap.get(commandId);
            if (command != null) {
                command.execute(update);
                if (command instanceof CommandWithText) {
                    lastCommandWithText.setData(chatId,command);
                    currentState.setData(chatId,StateBot.WAITING_TEXT);
                }
                return;
            }
        } else if (currentState.getData(chatId) == StateBot.WAITING_TEXT) {
            if (update.hasCallbackQuery()) {
                messageSender.answerCallBack(update.getCallbackQuery(),"Недоступно");
                return;
            }
            if (lastCommandWithText.getData(chatId) != null && lastCommandWithText.getData(chatId) instanceof CommandWithText commandWithText) {
                commandWithText.executeWithText(update, this);
                return;
            }
        }
        unknownCommand.execute(update);

    }

    private String getCommandIdFromCallBackData(String callBackData) {
        return callBackData.substring(0, callBackData.indexOf(':'));
    }

    public void setCurrentState(long chatId, StateBot currentState) {
        this.currentState.setData(chatId,currentState);
    }

    public enum StateBot {
        WAITING_TEXT,
        WAITING_COMMAND,
    }

}
