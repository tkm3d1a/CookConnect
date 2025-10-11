package com.tkforgeworks.cookconnect.socialservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class CookBookEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long recipeId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "entry_note_id", referencedColumnName = "id")
    private EntryNote entryNote;
}
