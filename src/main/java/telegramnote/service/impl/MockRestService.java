package telegramnote.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import telegramnote.data.CustomResponse;
import telegramnote.data.dto.Note;
import telegramnote.data.dto.User_;
import telegramnote.service.RestServiceInterface;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile("dev")
public class MockRestService implements RestServiceInterface {

    @Override
    @SneakyThrows
    public CustomResponse<String> register(User_ newUser) {
        if (isSuccess()) {
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
        if (isSuccess()) {
            CustomResponse<Note> customResponse = new CustomResponse<>();
            customResponse.setBody(note);
            return customResponse;
        }
        note.setId(1L);
        CustomResponse<Note> customResponse = new CustomResponse<>();
        customResponse.setErrorMessage("Ошибка поста заметки");
        return customResponse;
   }

    @Override
    public CustomResponse<List<Note>> getNotes(Long chatId) {
        CustomResponse<List<Note>> customResponse = new CustomResponse<>();
        if (isSuccess()) {
            List<Note> notes = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Note note = new Note();
                note.setText(i + " some text " + i);
                note.setChatId(chatId);
                note.setLabel(i + ": label");
                note.setId((long) (i * 2));
                notes.add(note);
            }
            customResponse.setBody(notes);
        } else {
            customResponse.setErrorMessage("Ошибка получение заметок");

        }
        return customResponse;
    }

    public boolean isSuccess() {
        int num = (int) (Math.random() * 10);
        return num <= 7;
    }
}

