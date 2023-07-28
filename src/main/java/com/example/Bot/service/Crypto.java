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
        String output = getUrlContent("https://api.coincap.io/v2/assets/"+cryptoName);
        JSONObject object = new JSONObject(output);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        priceUsd = "Цена: " + decimalFormat.format(object.getJSONObject("data").getDouble("priceUsd")) + "$";
        changePercent = "Рост за 24 часа: " + decimalFormat.format(object.getJSONObject("data").getDouble("changePercent24Hr")) + "%";
        response = priceUsd + "\n" + changePercent;
        return response;
    }
    private static String getUrlContent(String urlAdress){
        StringBuffer content = new StringBuffer();
        try{
            URL url = new URL(urlAdress);
            URLConnection urlConnection = url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while((line = bufferedReader.readLine())!=null){
                content.append(line+"\n");
            }
            bufferedReader.close();
        }catch (Exception e){
            System.out.println("Crypto not found");
        }
        return content.toString();
    }
}
