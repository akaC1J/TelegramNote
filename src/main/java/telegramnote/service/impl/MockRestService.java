package telegramnote.service.impl;

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
    public CustomResponse<User_> postUser(User_ newUser) {
        if (isSuccess()) {
            CustomResponse<User_> customResponse = new CustomResponse<>();
            customResponse.setBody(newUser);
            return customResponse;
        }
        CustomResponse<User_> customResponse = new CustomResponse<>();
        customResponse.setErrorMessage("Ошибка регистрации пользователя");
        return customResponse;
    }

    @Override
    public CustomResponse<User_> getUser(Long chatId) {
        CustomResponse<User_> response = new CustomResponse<>();
        response.setBody(new User_(chatId,"Гомер"));
        return response;
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
    public CustomResponse<List<Note>> getNotes(Long chatId, int sizePage, int offset) {
        List<Note> notes = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Note note = new Note();
            note.setText(i + " some text " + i);
            note.setUser(new User_().setChatId(chatId));
            note.setLabel(i + ": label");
            note.setId((long) (i * 2));
            notes.add(note);
        }
        CustomResponse<List<Note>> customResponse = new CustomResponse<>();
        if (isSuccess()) {
            int indexStart = offset * sizePage;
            int end = Math.min((indexStart + sizePage), notes.size());
            customResponse.setBody(new ArrayList<>());
            for (int i = indexStart; i < end; i++) {
                customResponse.getBody().add(notes.get(i));
            }
        } else {
            customResponse.setErrorMessage("Ошибка получение заметок");

        }
        return customResponse;
    }

    @Override
    public CustomResponse<Note> getNote(Long chatId, Long idNote) {
        CustomResponse<Note> customResponse = new CustomResponse<>();
        if (isSuccess()) {
            Note note = new Note();
            note.setText("QWERTYUIOP{asdfghjkl:zxcvbnm<");
            note.setUser(new User_().setChatId(chatId));
            note.setLabel("Название getNote");
            note.setId(idNote);
            customResponse.setBody(note);
        } else {
            customResponse.setErrorMessage("Ошибка получение заметки");

        }
        return customResponse;
    }

    @Override
    public CustomResponse<Integer> countNotesForChatId(Long chatId) {
        CustomResponse<Integer> customResponse = new CustomResponse<>();
        CustomResponse<List<Note>> notesResponse = getNotes(chatId, Integer.MAX_VALUE, 0);
        if (notesResponse.isSuccess()) {
            customResponse.setBody(notesResponse.getBody().size());
            return customResponse;
        }
        customResponse.setErrorMessage("Ошибка получение количества заметок");
        return customResponse;
    }

    @Override
    public CustomResponse<Void> deleteNote(Long noteId, Long chatId) {
        CustomResponse<Void> customResponse = new CustomResponse<>();
        if (isSuccess()) {
            customResponse.setSuccess(true);
            return customResponse;
        }
        customResponse.setErrorMessage("Ошибка удаление заметки");
        return customResponse;
    }

    @Override
    public CustomResponse<Note> patchNote(Note note) {
        CustomResponse<Note> customResponse = getNote(null, note.getId());
        if (!customResponse.getSuccess()) {
            customResponse.setErrorMessage("Ошибка изменения названия заметки");
        }
        return customResponse;
    }

    public boolean isSuccess() {
        /*int num = (int) (Math.random() * 10);
        return num <= 7;*/
        return true;
    }
}

