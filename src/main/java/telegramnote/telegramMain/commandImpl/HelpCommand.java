package telegramnote.telegramMain.commandImpl;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramnote.telegramMain.Command;
import telegramnote.telegramMain.MessageSender;

@Component

public class HelpCommand implements Command {
    private final MessageSender messageSender;

    public HelpCommand(@Lazy MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        String answer = getHelpText();
        messageSender.sendMessageWithMarkdown(chatId, answer);
    }

    @Override
    public String getCommandIdentifier() {
        return "Помощь";
    }

    private String getHelpText() {
        return """
                Чтобы перезапустить бота нажмите /start

                *Чтобы создать заметку* используйте текст или кнопку “Создать заметку”

                *Чтобы посмотреть список заметок*  используйте текст или кнопку “Посмотреть заметку”.
                Количество выводимых заметок ограничено - используйте стрелки для перехода на следующие страницы.

                *Чтобы посмотреть заметку*, вы должны нажать на нее(они являются кликабельными).

                При открытии заметки вы можете нажать на кнопку удалить и заметка будет удалена.""";
    }
}
