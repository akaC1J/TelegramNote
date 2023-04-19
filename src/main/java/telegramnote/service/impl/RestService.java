package telegramnote.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
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
    public CustomResponse<String> register(User_ newUser) {
        return baseDoResponse(newUser, "/register", String.class, Method.POST);
    }

    @Override
    public CustomResponse<Note> postNote(Note note) {
        return baseDoResponse(note, "/note", Note.class, Method.POST);
    }

    @Override
    public CustomResponse<List<Note>> getNotes(Long chatId) {
        @SuppressWarnings("rawtypes")
        CustomResponse<List> listCustomResponse = baseDoResponse(chatId, "/n", List.class, Method.GET);
        CustomResponse<List<Note>> response = new CustomResponse<>();
        response.setSuccess(listCustomResponse.isSuccess());
        //noinspection unchecked
        response.setBody(listCustomResponse.getBody());
        response.setErrorMessage(listCustomResponse.getErrorMessage());
        return response;
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
