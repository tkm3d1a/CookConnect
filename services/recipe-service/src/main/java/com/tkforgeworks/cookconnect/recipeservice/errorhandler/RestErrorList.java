package com.tkforgeworks.cookconnect.recipeservice.errorhandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
public class RestErrorList extends ArrayList<ErrorMessage> {
    @Serial
    private static final long serialVersionUID = 1L;
    private HttpStatus status;

    public RestErrorList(HttpStatus status, ErrorMessage... errorMessages) {
        this(status.value(), errorMessages);
    }

    public RestErrorList(int status, ErrorMessage... errorMessages) {
        super();
        this.status = HttpStatus.valueOf(status);
        addAll(Arrays.asList(errorMessages));
    }
}
