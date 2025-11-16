package com.tkforgeworks.cookconnect.userservice.model;

import com.tkforgeworks.cookconnect.userservice.model.enums.SkillLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CCUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String keycloakId;
    private String username;
    private String email;
    private boolean hasSocialInteraction;
    private boolean privateAccount;
    private boolean closedAccount;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    private SkillLevel skillLevel;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private ProfileInfo profileInfo;

    public void setProfileInfo(ProfileInfo profileInfo) {
        if (profileInfo == null) {
            if (this.profileInfo != null) {
                this.profileInfo.setUser(null);
            }
        } else {
            profileInfo.setUser(this);
        }
        this.profileInfo = profileInfo;
    }
}
