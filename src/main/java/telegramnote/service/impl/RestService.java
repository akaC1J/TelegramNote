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
        CustomResponse<String> customResponse = new CustomResponse<>();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity("/register", newUser, String.class);
            customResponse.setBody(response.getBody());
            return customResponse;

        } catch (HttpClientErrorException e) {
            customResponse.setErrorMessage(e.getResponseBodyAsString());
            return customResponse;
        }
    }

    @Override
    public CustomResponse<Note> postNote(Note note) {
        CustomResponse<Note> customResponse = new CustomResponse<>();
        try {
            ResponseEntity<Note> response = restTemplate.postForEntity("/note", note, Note.class);
            customResponse.setBody(response.getBody());
            return customResponse;

        } catch (HttpClientErrorException e) {
            customResponse.setErrorMessage(e.getResponseBodyAsString());
            return customResponse;
        }
    }

}
