package telegramnote.service;

import telegramnote.data.CustomResponse;
import telegramnote.data.dto.Note;
import telegramnote.data.dto.User_;

import java.util.List;

public interface RestServiceInterface {
    CustomResponse<String> register(User_ newUser);

    CustomResponse<Note> postNote(Note note);

    CustomResponse<List<Note>> getNotes(Long user);
}
