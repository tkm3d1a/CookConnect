package com.tkforgeworks.cookconnect.recipeservice.errorhandler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {
    private String message;
    private String code;
    private String detail;

    private ErrorMessage(String message, String code, String detail) {
        this.message = message;
        this.code = code;
        this.detail = detail;
    }

    // Factory methods
    public static ErrorMessage withMessage(String message) {
        return new ErrorMessage(message, "NO CODE GIVEN", "NO DETAIL GIVEN");
    }
    public static ErrorMessage withMessageCode(String message, String code) {
        return new ErrorMessage(message, code, "NO DETAIL GIVEN");
    }
    public static ErrorMessage withMessageDetail(String message, String detail) {
        return new ErrorMessage(message, "NO CODE GIVEN", detail);
    }
    public static ErrorMessage withAll(String message, String code, String detail) {
        return new ErrorMessage(message, code, detail);
    }
}
