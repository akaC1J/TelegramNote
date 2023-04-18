package telegramnote.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import telegramnote.data.CustomResponse;
import telegramnote.data.dto.User_;

@Service
public final class RestService {

    private final RestTemplate restTemplate;

    public RestService(@Value("http://localhost:8080") String baseUrl) {
        this.restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));

    }

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

}
