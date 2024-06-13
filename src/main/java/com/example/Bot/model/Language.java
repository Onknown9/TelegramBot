package com.example.Bot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "language_preference_table")
public class Language {
    @Id
    private Integer id;
    private String lang;
}
