package telegramnote.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import telegramnote.data.CustomResponse;
import telegramnote.data.dto.Note;
import telegramnote.data.dto.User_;
import telegramnote.service.RestServiceInterface;

import java.util.List;

@Service
@Profile("prod")
public final class RestService implements RestServiceInterface {

    private final RestTemplate restTemplate;

    public RestService(@Value("http://localhost:8080") String baseUrl) {
        this.restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
    }

    @Override
    public CustomResponse<User_> postUser(User_ newUser) {
            return baseDoResponse(newUser, "/user", User_.class, Method.POST);
    }

    @Override
    public CustomResponse<User_> getUser(Long chatId) {
        String url = "/user/{chatId}";
        CustomResponse<User_> customResponse = new CustomResponse<>();
        try {
            User_ forObject = restTemplate.getForObject(url, User_.class, chatId);
            customResponse.setBody(forObject);
            return customResponse;
        } catch (HttpClientErrorException e) {
            customResponse.setErrorMessage(e.getResponseBodyAsString());
            customResponse.setStatusCode(e.getStatusCode());
            return customResponse;
        }
    }

    @Override
    public CustomResponse<Note> postNote(Note note) {
        return baseDoResponse(note, "/note", Note.class, Method.POST);
    }

    @Override
    public CustomResponse<List<Note>> getNotes(Long chatId, int sizePage, int offSet) {
        String url = "/note?chatId={chatId}&sizePage={sizePage}&offset={offset}";
        CustomResponse<List<Note>> response = new CustomResponse<>();
        try {
            ResponseEntity<List<Note>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
            }, chatId, sizePage, offSet);
            response.setBody(responseEntity.getBody());
            return response;
        } catch (HttpClientErrorException e) {
            response.setErrorMessage(e.getResponseBodyAsString());
            return response;
        }
    }

    @Override
    public CustomResponse<Note> getNote(Long chatId, Long idNote) {
        CustomResponse<Note> response = new CustomResponse<>();
        String url = "/notes/{idNote}?chatId={chatId}";
        try {
            ResponseEntity<Note> responseEntity = restTemplate.getForEntity(url, Note.class, idNote, chatId);
            response.setBody(responseEntity.getBody());
            return response;
        }catch (HttpClientErrorException e) {
            response.setErrorMessage(e.getResponseBodyAsString());
            return response;
        }
    }

    @Override
    public CustomResponse<Integer> countNotesForChatId(Long chatId) {
        String url = "/notes/count?chatId={chatId}";
        return baseDoResponse(chatId, url, Integer.class, Method.GET);
    }

    @Override
    public CustomResponse<Void> deleteNote(Long noteId, Long chatId) {
        String url = "/note/{id}?chatId={chatId}";
        CustomResponse<Void> customResponse = new CustomResponse<>();
        try {
            restTemplate.delete(url, noteId, chatId);
            customResponse.setSuccess(true);
        } catch (HttpClientErrorException e) {
            customResponse.setErrorMessage(e.getResponseBodyAsString());
        }
        return customResponse;
    }

    private <A, P> CustomResponse<A> baseDoResponse(P singleParameter, String url,
                                                    Class<A> answerType, Method method ) {
        CustomResponse<A> customResponse = new CustomResponse<>();
        try {
            ResponseEntity<A> response = null;
            switch (method) {
                case GET -> response = restTemplate.getForEntity(url, answerType, singleParameter);
                case POST -> response = restTemplate.postForEntity(url, singleParameter, answerType);
            }
            if (response != null) {
                customResponse.setBody(response.getBody());
            } else {
                customResponse.setErrorMessage("Неизвестная ошибка");
            }
            return customResponse;

        } catch (HttpClientErrorException e) {
            customResponse.setErrorMessage(e.getResponseBodyAsString());
            return customResponse;
        }
    }

    private enum Method {
        GET,POST, DELETE,
    }
}
