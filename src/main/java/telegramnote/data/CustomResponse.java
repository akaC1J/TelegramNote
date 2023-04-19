package telegramnote.data;

import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
public class CustomResponse<T> {
    private Boolean success;

    private String errorMessage;
    private HttpStatusCode statusCode;

    private T body;

    public void setErrorMessage(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            success = false;
        }
        this.errorMessage = errorMessage;
    }

    public Boolean isSuccess() {
        return success != null && success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setBody(T body) {
        if (body != null) {
            success = true;
        }
        this.body = body;
    }


}
