package com.tkforgeworks.cookconnect.socialservice.errorhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
public class ResponseWrapper {
    private Object data;
    private Object metaData;
    private List<ErrorMessage> errorMessages;
}
