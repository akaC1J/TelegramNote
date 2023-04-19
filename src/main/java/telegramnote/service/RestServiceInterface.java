package telegramnote.service;

import telegramnote.data.CustomResponse;
import telegramnote.data.dto.Note;
import telegramnote.data.dto.User_;

import java.util.List;

public interface RestServiceInterface {
    CustomResponse<User_> postUser(User_ newUser);

    CustomResponse<User_> getUser(Long chatId);

    CustomResponse<Note> postNote(Note note);

    CustomResponse<List<Note>> getNotes(Long chatId, int sizePage, int offset);

    CustomResponse<Note> getNote(Long chatId, Long idNote);

    CustomResponse<Integer> countNotesForChatId(Long chatId);

    CustomResponse<Void> deleteNote(Long noteId, Long chatId);
}
