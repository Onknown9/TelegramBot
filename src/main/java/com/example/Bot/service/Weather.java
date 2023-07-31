package com.example.Bot.service;

import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@Data
public class Weather {
    private String temp;
    private String temp_feels;
    private String min_temp;
    private String max_temp;
    private String response;
    private String general;
    public String initializeWeather(String city){
        try{
            String output = getUrlContent("https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=4c4614e4a6f6015f4db106a89b8c1f3f&units=metric");
            JSONObject object = new JSONObject(output);
            JSONArray weatherArray = object.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            general="Погода: "+weatherObject.getString("main");
            temp="Температура: "+object.getJSONObject("main").getDouble("temp");
            temp_feels="Ощущается как: "+object.getJSONObject("main").getDouble("feels_like");
            min_temp="Минимальная температура: "+object.getJSONObject("main").getDouble("temp_min");
            max_temp="Максимальная температура: "+object.getJSONObject("main").getDouble("temp_max");
            response=general+"\n"+temp+"\n"+temp_feels+"\n"+min_temp+"\n"+max_temp;
            return response;
        }
        catch(Exception e){
            return "Неверный формат или такого города нет.";
        }
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
            System.out.println("City not found");
        }
        return content.toString();
    }
}