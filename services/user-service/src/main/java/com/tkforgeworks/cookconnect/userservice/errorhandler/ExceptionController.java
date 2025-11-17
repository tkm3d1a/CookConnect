package com.tkforgeworks.cookconnect.userservice.errorhandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Collections;

@ControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler(value = { Exception.class })
    public @ResponseBody ResponseEntity<ResponseWrapper> handleException(HttpServletRequest request,
                                                                         Exception e) {
        log.info("ExceptionController.handleException");
        RestErrorList errorList = new RestErrorList(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorMessage.withMessageDetail(e.getMessage(), Arrays.stream(e.getStackTrace()).findFirst().toString())
        );
        ResponseWrapper responseWrapper = new ResponseWrapper(
                null,
                Collections.singletonMap("status",HttpStatus.INTERNAL_SERVER_ERROR),
                errorList
        );
        return ResponseEntity.internalServerError().body(responseWrapper);
    }

    @ExceptionHandler(value = { RuntimeException.class })
    public @ResponseBody ResponseEntity<ResponseWrapper> handleRuntimeException(HttpServletRequest request,
                                                                                RuntimeException e) {
        log.info("ExceptionController.handleRuntimeException");
        RestErrorList errorList = new RestErrorList(
                HttpStatus.BAD_REQUEST,
                ErrorMessage.withMessage(e.getMessage())
        );
        ResponseWrapper responseWrapper = new ResponseWrapper(
                null,
                Collections.singletonMap("status",HttpStatus.BAD_REQUEST),
                errorList
        );
        return ResponseEntity.badRequest().body(responseWrapper);
    }

    @ExceptionHandler(value = { UserNotFoundException.class })
    public @ResponseBody ResponseEntity<ResponseWrapper> handleUserNotFoundException(HttpServletRequest request,
                                                                                RuntimeException e) {
        log.info("ExceptionController.handleUserNotFoundException");
        RestErrorList errorList = new RestErrorList(
                HttpStatus.NOT_FOUND,
                ErrorMessage.withMessage(e.getMessage())
        );
        ResponseWrapper responseWrapper = new ResponseWrapper(
                null,
                Collections.singletonMap("status",HttpStatus.NOT_FOUND),
                errorList
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
    }
}
