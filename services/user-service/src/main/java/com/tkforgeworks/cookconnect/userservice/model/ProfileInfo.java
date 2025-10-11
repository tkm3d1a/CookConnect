package com.tkforgeworks.cookconnect.userservice.model;

import com.tkforgeworks.cookconnect.userservice.model.enums.Gender;
import com.tkforgeworks.cookconnect.userservice.model.enums.Pronouns;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ProfileInfo {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private CCUser user;

    private String firstName;
    private String lastName;
    private Date birthDate;
    private int age;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Pronouns pronouns;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER,
            mappedBy = "id"
    )
    private Set<Address> addresses = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
