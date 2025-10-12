package com.tkforgeworks.cookconnect.userservice.model;

import com.tkforgeworks.cookconnect.userservice.model.enums.Country;
import com.tkforgeworks.cookconnect.userservice.model.enums.State;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private ProfileInfo profileInfo;

    private String street;
    private String apartmentNumber;
    private String city;
    private String zipCode;
    @Enumerated(EnumType.STRING)
    private State state;
    @Enumerated(EnumType.STRING)
    private Country country;
}
