package com.tkforgeworks.userservice.model;

import com.tkforgeworks.userservice.model.enums.Country;
import com.tkforgeworks.userservice.model.enums.State;
import jakarta.persistence.*;

@Entity
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
