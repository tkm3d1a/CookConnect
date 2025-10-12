package com.tkforgeworks.cookconnect.userservice.model;

import com.tkforgeworks.cookconnect.userservice.model.enums.SkillLevel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private boolean hasSocialInteraction;
    private boolean isPrivate;
    private boolean isClosed;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    private SkillLevel skillLevel;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private ProfileInfo profileInfo;
}
