package com.tkforgeworks.cookconnect.userservice.model.dto;

import com.tkforgeworks.cookconnect.userservice.model.enums.Country;
import com.tkforgeworks.cookconnect.userservice.model.enums.State;

import java.io.Serializable;

/**
 * DTO for {@link com.tkforgeworks.cookconnect.userservice.model.Address}
 */
public record AddressDto(Long id, String street, String apartmentNumber, String city, String zipCode, State state,
                         Country country) implements Serializable {
}