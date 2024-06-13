package com.example.Bot.service;

import com.example.Bot.model.Language;
import com.example.Bot.model.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LanguageService {
    private final LanguageRepository languageRepository;

    @Autowired
    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public String getLanguageById(int id) {
        Optional<Language> languageOptional = languageRepository.findById(id);
        return languageOptional.map(Language::getLang).orElse(null);
    }
}
