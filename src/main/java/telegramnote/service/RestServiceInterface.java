package telegramnote.service;

import telegramnote.data.CustomResponse;
import telegramnote.data.dto.Note;
import telegramnote.data.dto.User_;

public interface RestServiceInterface {
    CustomResponse<String> register(User_ newUser);

    CustomResponse<Note> postNote(Note note);
}
