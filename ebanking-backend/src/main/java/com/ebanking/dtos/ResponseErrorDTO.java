package com.ebanking.dtos;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;


@AllArgsConstructor
public class ResponseErrorDTO implements ErrorResponse {

    private String title;
    private String errorMessage;
    private HttpStatusCode httpStatusCode;

    @Override
    public HttpStatusCode getStatusCode() {
        return this.httpStatusCode;
    }

    @Override
    public ProblemDetail getBody() {
        return new CauseError(this.title, this.errorMessage, this.httpStatusCode.value());
    }

    private class CauseError extends ProblemDetail{
        private String ErrorMessage;
        private int status;

        public CauseError(String title, String message, int status){
            super();
            this.setDetail(message);
            this.setStatus(status);
            this.setTitle(title);
        }
    }
}
