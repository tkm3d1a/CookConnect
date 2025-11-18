package com.tkforgeworks.cookconnect.socialservice.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
public class SocialInteraction {
    @Id
    private String forUserId;

    @Setter(AccessLevel.NONE)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "following_user_id")
    private Set<String> followingIds = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_followed_by",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "follower_user_id")
    private Set<String> followerIds = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "bookmarked_recipes",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "recipe_id")
    private Set<Long> bookmarkedRecipeIds = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Cookbook> cookbooks = new HashSet<>();
}
