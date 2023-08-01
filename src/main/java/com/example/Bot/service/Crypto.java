package com.example.Bot.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class Crypto {
    private String response;
    private String priceUsd;
    private String changePercent;
    public String initializeCrypto(String cryptoName) {
        try{
            String output = getUrlContent("https://api.coincap.io/v2/assets/"+cryptoName);
            JSONObject object = new JSONObject(output);
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            priceUsd = "Цена: " + decimalFormat.format(object.getJSONObject("data").getDouble("priceUsd")) + "$";
            changePercent = "Рост за 24 часа: " + decimalFormat.format(object.getJSONObject("data").getDouble("changePercent24Hr")) + "%";
            response = priceUsd + "\n" + changePercent;
            return response;
        }
        catch (Exception e){
            return "Неверный формат или такой криптовалюты нет.";
        }
    }
    public String listOfFavorites(){
        String output = getUrlContent("https://api.coincap.io/v2/assets");
        JSONArray dataArray = new JSONObject(output).getJSONArray("data");
        StringBuilder currencyNames = new StringBuilder();
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject currencyData = dataArray.getJSONObject(i);
            String name = currencyData.getString("name");
            String id = currencyData.getString("id");
            if(id.equals("binance-coin")||id.equals("bitcoin")){
                currencyNames.append(name).append("\n").append(initializeCrypto(id)).append("\n");
            }
        }
        System.out.println(currencyNames.toString());
        return currencyNames.toString();
    }
    public String listOfCrypto(){
        String output = getUrlContent("https://api.coincap.io/v2/assets");
        JSONArray dataArray = new JSONObject(output).getJSONArray("data");
        StringBuilder currencyNames = new StringBuilder();
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject currencyData = dataArray.getJSONObject(i);
            String name = currencyData.getString("name");
            currencyNames.append(name).append("\n");
        }
        return currencyNames.toString();
    }
    private static String getUrlContent(String urlAdress){
        StringBuilder content = new StringBuilder();
        try{
            URL url = new URL(urlAdress);
            URLConnection urlConnection = url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while((line = bufferedReader.readLine())!=null){
                content.append(line).append("\n");
            }
            bufferedReader.close();
        }catch (Exception e){
            System.out.println("Crypto not found");
        }
        return content.toString();
    }
}
