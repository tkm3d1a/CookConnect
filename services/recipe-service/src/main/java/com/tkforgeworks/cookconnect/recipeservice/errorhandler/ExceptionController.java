package com.tkforgeworks.cookconnect.recipeservice.errorhandler;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
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
    @ExceptionHandler(value = { UserNotFoundException.class })
    public @ResponseBody ResponseEntity<ResponseWrapper> handleUserNotFoundException(HttpServletRequest request,
                                                                                     RuntimeException e) {
        log.warn("User not found: {}", e.getMessage());
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

    @ExceptionHandler(RequestNotPermitted.class)
    public @ResponseBody ResponseEntity<ResponseWrapper> handleRateLimitException(
            HttpServletRequest request,
            RequestNotPermitted e) {

        log.warn("Rate limit exceeded: {}", e.getMessage());

        RestErrorList errorList = new RestErrorList(
                HttpStatus.TOO_MANY_REQUESTS,
                ErrorMessage.withMessage("Rate limit exceeded. Please try again later.")
        );

        ResponseWrapper responseWrapper = new ResponseWrapper(
                null,
                Collections.singletonMap("status", HttpStatus.TOO_MANY_REQUESTS),
                errorList
        );

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(responseWrapper);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public @ResponseBody ResponseEntity<ResponseWrapper> handleCircuitBreakerException(
            HttpServletRequest request,
            CallNotPermittedException e) {

        log.warn("Circuit breaker open: {}", e.getMessage());

        RestErrorList errorList = new RestErrorList(
                HttpStatus.SERVICE_UNAVAILABLE,
                ErrorMessage.withMessage("Service temporarily unavailable. Please try again later.")
        );

        ResponseWrapper responseWrapper = new ResponseWrapper(
                null,
                Collections.singletonMap("status", HttpStatus.SERVICE_UNAVAILABLE),
                errorList
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(responseWrapper);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public @ResponseBody ResponseEntity<ResponseWrapper> handleIllegalArgumentException(
            HttpServletRequest request,
            IllegalArgumentException e) {

        log.warn("Invalid argument: {}", e.getMessage());

        RestErrorList errorList = new RestErrorList(
                HttpStatus.BAD_REQUEST,
                ErrorMessage.withMessage(e.getMessage())
        );

        ResponseWrapper responseWrapper = new ResponseWrapper(
                null,
                Collections.singletonMap("status", HttpStatus.BAD_REQUEST),
                errorList
        );

        return ResponseEntity.badRequest().body(responseWrapper);
    }

    @ExceptionHandler(IllegalStateException.class)
    public @ResponseBody ResponseEntity<ResponseWrapper> handleIllegalStateException(
            HttpServletRequest request,
            IllegalStateException e) {

        log.error("Illegal state: {}", e.getMessage());

        RestErrorList errorList = new RestErrorList(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorMessage.withMessage("An unexpected error occurred")
        );

        ResponseWrapper responseWrapper = new ResponseWrapper(
                null,
                Collections.singletonMap("status", HttpStatus.INTERNAL_SERVER_ERROR),
                errorList
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseWrapper);
    }

    @ExceptionHandler(value = { Exception.class })
    public @ResponseBody ResponseEntity<ResponseWrapper> handleException(HttpServletRequest request,
                                                                         Exception e) {
        log.error("Unexpected error occurred", e);
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
}
