package com.example.Bot.service;

import com.example.Bot.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Random;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;
    public TelegramBot(BotConfig config){
        this.config = config;
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if(messageText.toLowerCase().contains("монетку")||messageText.toLowerCase().contains("монетка")){
                flipTheCoin(chatId);
            }
            else if (messageText.toLowerCase().contains("/start")) {
                startCommandReceived(chatId,update.getMessage().getChat().getFirstName());
            }
            else if(messageText.toLowerCase().contains("погода")||messageText.toLowerCase().contains("прогноз")){
                String s=messageText.toLowerCase();
                weatherForecast(chatId,s);
            }
            else if(messageText.toLowerCase().contains("криптовалюта")||messageText.toLowerCase().contains("валюта")||messageText.toLowerCase().contains("валюте")){
                String s=messageText.toLowerCase();
                getCrypto(chatId,s);
            }
            else if(messageText.toLowerCase().contains("список")&&messageText.toLowerCase().contains("криптовалют") || messageText.toLowerCase().contains("список")&&messageText.toLowerCase().contains("валют")){
                getListOfCrypto(chatId);
            }
            else{
                sendMessage(chatId, "Такой команды еще не знаю.");
            }
        }
    }
    private void getCrypto(long chatId, String s){
        /*s = s.replace("криптовалюта","");
        s = s.replace("валюта","");
        s = s.replace("валюте","");
        s = s.replace("о","");
        s = s.replace("расскажи","");
        s = s.replace("про","");
        s = s.trim();*/
        String[] strArray = s.split(" ");
        String cryptoName = strArray[strArray.length-1];
        Crypto crypto = new Crypto();
        String response = crypto.initializeCrypto(cryptoName);
        sendMessage(chatId,response);
    }

    private void getListOfCrypto(long chatId){
        Crypto crypto = new Crypto();
        String response = crypto.listOfCrypto();
        sendMessage(chatId, response );
    }
    private void weatherForecast(long chatId,String s){
        s = s.replace("прогноз","");
        s = s.replace("погода","");
        s = s.replace(" в ","");
        s = s.trim();
        Weather weather = new Weather();
        String response = weather.initializeWeather(s);
        sendMessage(chatId, response);
    }
    private void startCommandReceived(long chatId , String name){
        String response = "Начало положено, " + name + ".";
        sendMessage(chatId, response);
    }
    private void flipTheCoin(long chatId){
        Random rand = new Random();
        int num = rand.nextInt(2);
        if(num==0){
            sendMessage(chatId,"Орел");
        }
        else{
            sendMessage(chatId, "Решка");
        }
    }
    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try{
            execute(message);
        }
        catch(TelegramApiException e){
            throw new RuntimeException(e);
        }

    }
}
