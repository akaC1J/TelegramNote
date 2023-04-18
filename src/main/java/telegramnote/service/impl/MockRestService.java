package telegramnote.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import telegramnote.data.CustomResponse;
import telegramnote.data.dto.Note;
import telegramnote.data.dto.User_;
import telegramnote.service.RestServiceInterface;

@Service
@Profile("dev")
public class MockRestService implements RestServiceInterface {

    @SuppressWarnings("FieldMayBeFinal")
    private boolean success = true;

    @Override
    @SneakyThrows
    public CustomResponse<String> register(User_ newUser) {
        if (success) {
            CustomResponse<String> customResponse = new CustomResponse<>();
            ObjectMapper objectMapper = new ObjectMapper();
            customResponse.setBody(objectMapper.writeValueAsString(newUser));
            return customResponse;
        }
        CustomResponse<String> customResponse = new CustomResponse<>();
        customResponse.setErrorMessage("Ошибка регистрации пользователя");
        return customResponse;
    }

    @Override
    public CustomResponse<Note> postNote(Note note) {
        if (success) {
            CustomResponse<Note> customResponse = new CustomResponse<>();
            customResponse.setBody(note);
            return customResponse;
        }
        CustomResponse<Note> customResponse = new CustomResponse<>();
        customResponse.setErrorMessage("Ошибка поста заметки");
        return customResponse;
   }
}

