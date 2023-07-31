package com.example.Bot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity(name = "usersDataTable")
public class User {
    @Id
    private Long chatId;
    private String userName;
}
