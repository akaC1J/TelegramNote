package telegramnote;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramnote.data.CustomResponse;
import telegramnote.data.dto.User_;
import telegramnote.service.RestService;
import telegramnote.service.TelegramBot;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TelegramBotTest {

    @Autowired
    private TelegramBot telegramBot;

    @MockBean
    private RestService restService;

    @Test
    public void testStartCommandReceived() {
        Update update = new Update();
        Message message = new Message();
        message.setText("/start");
        Chat chat = new Chat();
        chat.setId(1L);
        chat.setFirstName("John");
        message.setChat(chat);
        update.setMessage(message);

        User_ expectedUser = new User_(chat.getId(), chat.getFirstName());

        CustomResponse<String> response = new CustomResponse<>();
        response.setBody("Register user");
        when(restService.register(expectedUser)).thenReturn(response);
        telegramBot.onUpdateReceived(update);
        verify(restService, times(1)).register(expectedUser);
    }
}

