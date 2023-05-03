package telegramnote.telegramMain.commandImpl.note;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramnote.telegramMain.Command;

@Component
public class ChangePageCommand implements Command {

    private final GetNotesCommand getNotesCommand;

    public ChangePageCommand(GetNotesCommand getNotesCommand) {
        this.getNotesCommand = getNotesCommand;
    }

    @Override
    public void execute(Update update) {
        getNotesCommand.execute(update);
    }

    @Override
    public String getCommandIdentifier() {
        return "get_notes";
    }
}
