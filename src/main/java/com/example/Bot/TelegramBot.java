package com.example.Bot;

import com.example.Bot.config.BotConfig;
import com.example.Bot.defence.Literature;
import com.example.Bot.model.Language;
import com.example.Bot.model.LanguageRepository;
import com.example.Bot.model.User;
import com.example.Bot.model.UserRepository;
import com.example.Bot.service.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    @Autowired
    private LanguageService languageService;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
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
            else if(messageText.toLowerCase().contains("русский")||messageText.toLowerCase().contains("рус")){
                Language languagePreference = new Language();
                languagePreference.setId(1);
                languagePreference.setLang("rus");
                languageRepository.save(languagePreference);
                sendMessage(chatId,"Успешно заменен язык на русский");
            }
            else if(messageText.toLowerCase().contains("украинский")||messageText.toLowerCase().contains("укр")){
                Language languagePreference = new Language();
                languagePreference.setId(1);
                languagePreference.setLang("ukr");
                languageRepository.save(languagePreference);
                sendMessage(chatId,"Успешно заменен язык на украинский");
            }
            else if(messageText.toLowerCase().contains("английский")||messageText.toLowerCase().contains("англ")){
                Language languagePreference = new Language();
                languagePreference.setId(1);
                languagePreference.setLang("eng");
                languageRepository.save(languagePreference);
                sendMessage(chatId,"Успешно заменен язык на английский");
            }
            else if (messageText.toLowerCase().contains("/start")) {
                registerUser(update.getMessage());
                startCommandReceived(chatId,update.getMessage().getChat().getFirstName());
            }
            else if (messageText.toLowerCase().contains("о ")||messageText.toLowerCase().contains("about")) {
                sendMessage(chatId, scrape(String.valueOf(update.getMessage().getText())) );
            }
            else if(messageText.toLowerCase().contains("погода")||messageText.toLowerCase().contains("прогноз")||
                    messageText.toLowerCase().contains("прогнозу")||messageText.toLowerCase().contains("погоде")){
                String s=messageText.toLowerCase();
                weatherForecast(chatId,s);
            }
            else if(messageText.toLowerCase().contains("криптовалюта")||messageText.toLowerCase().contains("валюта")
                    ||messageText.toLowerCase().contains("валюте")||messageText.toLowerCase().contains("валюту")){
                String s=messageText.toLowerCase();
                getCrypto(chatId,s);
            }
            else if(messageText.toLowerCase().contains("список")&&messageText.toLowerCase().contains("криптовалют") ||
                    messageText.toLowerCase().contains("список")&&messageText.toLowerCase().contains("валют")){
                getListOfCrypto(chatId);
            }
            else if (messageText.toLowerCase().contains("команды")) {
                sendMessage(chatId, "Список команд: \n узнать список криптовалют взятых из API:" +
                        " \"список\", \n узнать более детальную информацию о какой-либо валюте: \"валюта + *название валюты*\"," +
                        " \n узнать погоду нужного города: погода + *название населенного пункта*\" \n \"узнать" +
                        " список литературы: *литература*\n \n \"для создания поискового запроса добавьте ключевое слово \"о\" перед запросом");
            }
            else if (messageText.toLowerCase().contains("литература") || messageText.toLowerCase().contains("литературу")
                    || messageText.toLowerCase().contains("литературе") || messageText.toLowerCase().contains("литературы")){
                sendMessage(chatId,receiveSpringLiterature());
                sendMessage(chatId,receiveJavaLiterature());
                sendMessage(chatId,receiveTesseractLiterature());
                sendMessage(chatId,receiveOtherLiterature());
                sendMessage(chatId,receiveBotsSources());
            }
            else{
                sendMessage(chatId, "Такой команды еще не знаю. Что бы узнать список команд введите: команды");
            }
        }

        if (update.hasMessage() && update.getMessage().hasDocument()) {
            String preferredLanguage = getLanguageById(1);
            long chatId = update.getMessage().getChatId();
            Document document = update.getMessage().getDocument();
            String fileId = document.getFileId();

            // Create GetFile method and set the file ID
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(fileId);

            try {
                // Execute the GetFile method
                File file = execute(getFileMethod);

                // Download the file
                java.io.File downloadedFile = downloadFile(file.getFilePath());

                if (downloadedFile != null && preferredLanguage.equals("eng")) {
                    TesseractPDFService tesseractService = new TesseractPDFService();
                    String extractedText = tesseractService.extractText(downloadedFile);
                    List<String> messageParts = new ArrayList<>();
                    int length = extractedText.length();
                    int partSize = 3500;

                    for (int i = 0; i < length; i += partSize) {
                        int endIndex = Math.min(i + partSize, length);
                        String part = extractedText.substring(i, endIndex);
                        messageParts.add(part);
                    }

                    // Send each part back to the user
                    for (String part : messageParts) {
                        sendMessage(chatId, part);
                    }
                }
                else if (downloadedFile != null && downloadedFile.exists() && preferredLanguage.equals("rus")) {
                    TesseractPDFRus tesseractService = new TesseractPDFRus();
                    String extractedText = tesseractService.extractText(downloadedFile);
                    List<String> messageParts = new ArrayList<>();
                    int length = extractedText.length();
                    int partSize = 3500;

                    for (int i = 0; i < length; i += partSize) {
                        int endIndex = Math.min(i + partSize, length);
                        String part = extractedText.substring(i, endIndex);
                        messageParts.add(part);
                    }

                    // Send each part back to the user
                    for (String part : messageParts) {
                        sendMessage(chatId, part);
                    }
                }
                else if (downloadedFile != null && downloadedFile.exists() && preferredLanguage.equals("ukr")) {
                    TesseractPDFUkr tesseractService = new TesseractPDFUkr();
                    String extractedText = tesseractService.extractText(downloadedFile);
                    List<String> messageParts = new ArrayList<>();
                    int length = extractedText.length();
                    int partSize = 3500;

                    for (int i = 0; i < length; i += partSize) {
                        int endIndex = Math.min(i + partSize, length);
                        String part = extractedText.substring(i, endIndex);
                        messageParts.add(part);
                    }

                    // Send each part back to the user
                    for (String part : messageParts) {
                        sendMessage(chatId, part);
                    }
                }
                else {
                    sendMessage(chatId, "Не удалось скачать файл.");
                }
            } catch (Exception e) {
                if (e.getMessage().contains("message is too long")) {
                    sendMessage(chatId, "Слишком большой файл");
                }
                throw new RuntimeException(e);
            }
        }

        if(update.hasMessage() && update.getMessage().hasPhoto()){
            String preferredLanguage = getLanguageById(1);
            long chatId = update.getMessage().getChatId();
            List<PhotoSize> photos = update.getMessage().getPhoto();
            // Get the largest photo available
            PhotoSize photo = photos.get(photos.size() - 1); // Get the last photo which is the largest
            String fileId = photo.getFileId();
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(fileId);
            if (preferredLanguage.equals("eng")){
                try {
                    File file = execute(getFileMethod);
                    String filePath = file.getFilePath();

                    // Download the file
                    java.io.File downloadedFile = downloadFile(filePath);


                    TesseractService tesseractService = new TesseractService();
                    String extractedText = tesseractService.extractText(downloadedFile);

                    // Send the extracted text back to the user
                    sendMessage(chatId, extractedText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                    sendMessage(chatId, "Failed to process the image.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else if (preferredLanguage.equals("rus")){
                try {
                    File file = execute(getFileMethod);
                    String filePath = file.getFilePath();

                    // Download the file
                    java.io.File downloadedFile = downloadFile(filePath);


                    TesseractRus tesseractService = new TesseractRus();
                    String extractedText = tesseractService.extractText(downloadedFile);

                    // Send the extracted text back to the user
                    sendMessage(chatId, extractedText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                    sendMessage(chatId, "Failed to process the image.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else if (preferredLanguage.equals("ukr")){
                try {
                    File file = execute(getFileMethod);
                    String filePath = file.getFilePath();

                    // Download the file
                    java.io.File downloadedFile = downloadFile(filePath);


                    TesseractUkr tesseractService = new TesseractUkr();
                    String extractedText = tesseractService.extractText(downloadedFile);

                    // Send the extracted text back to the user
                    sendMessage(chatId, extractedText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                    sendMessage(chatId, "Failed to process the image.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private String receiveSpringLiterature(){
        Literature lit = new Literature();
        return lit.getSpringSources();
    }
    private String receiveJavaLiterature(){
        Literature lit = new Literature();
        return lit.getJavaSources();
    }
    private String receiveTesseractLiterature(){
        Literature lit = new Literature();
        return lit.getTesseractSources();
    }
    private String receiveOtherLiterature(){
        Literature lit = new Literature();
        return lit.getOtherSources();
    }
    private String receiveBotsSources(){
        Literature lit = new Literature();
        return lit.getBotsSources();
    }
    public String getLanguageById(int id) {
        String language = null;
        // Get a new Session instance from the session factory
        try (Session session = sessionFactory.openSession()) {
            // Begin a transaction
            Transaction transaction = session.beginTransaction();

            // Fetch the language entity by ID
            Language langEntity = session.get(Language.class, id);

            if (langEntity != null) {
                language = langEntity.getLang();
            }

            // Commit the transaction
            transaction.commit();
        } catch (Exception e) {
            System.out.println(e);
        }
        return language;
    }

    public void close() {
        // Close the session factory
        sessionFactory.close();
    }
    private String fetchFirstString() {
        Optional<Language> languageOptional = languageRepository.findById(1);

        if (languageOptional.isPresent()) {
            Language language = languageOptional.get();
            return language.getLang();
        } else {
            // Handle the case where the entity with ID 1 is not found
            return null; // or throw an exception, log a message, etc.
        }
    }
    private String scrape(String s){
        Scraper scraper = new Scraper();
        String scrapeResult = scraper.scrapeFirstResult(s);
        StringBuilder builder = new StringBuilder();
        if (scrapeResult.length()>4000){
            for(int i=0; i<4000; i++){
                builder.append(scrapeResult.charAt(i));
            }
            return builder.substring(0, builder.toString().lastIndexOf(".")).trim();
        }
        return scrapeResult;
    }
    private void registerUser(Message msg){
        if(userRepository.findById(msg.getChatId()).isEmpty()){
            var chatId = msg.getChatId();
            var chat = msg.getChat();
            User user = new User();
            user.setChatId(chatId);
            user.setUserName(chat.getUserName());
            userRepository.save(user);
        }
    }
    private void getCrypto(long chatId, String s){
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
        String[] strArray = s.split(" ");
        String cityName = strArray[strArray.length-1];
        Weather weather = new Weather();
        String response = weather.initializeWeather(cityName);
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
    private void executeMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Error");
        }
    }
    private void prepareAndSendMessage(long chatId,String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText((textToSend));
        executeMessage(message);
    }
    @Scheduled (cron = "0 0 10 * * *")
    private void sendScheduledMessage(){
        Weather weather = new Weather();
        Crypto crypto = new Crypto();
        String weatherSchedule = "Погода Bodo \n"+weather.initializeWeather("Bodo");
        prepareAndSendMessage(config.getOwnerId(),"Информация по криптовалютам:"+"\n"+ crypto.listOfFavorites());
        prepareAndSendMessage(config.getOwnerId(),weatherSchedule);
    }
}
