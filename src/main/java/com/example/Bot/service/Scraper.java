package com.example.Bot.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scraper {

    public static void main(String[] args) {
        String query = "информация о машинах";
        String result = scrapeFirstResult(query);
        System.out.println(result);
    }

    public static String scrapeFirstResult(String query) {
        try {
            // Выполнение поиска в Bing
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String searchUrl = "https://www.bing.com/search?q=" + encodedQuery;
            String searchResultPage = getPageContent(searchUrl);

            // Извлечение URL первого результата
            String firstResultUrl = extractFirstResultUrl(searchResultPage);
            if (firstResultUrl == null) {
                return "Failed to find first result URL.";
            }

            // Получение содержимого первого результата
            String firstResultPage = getPageContent(firstResultUrl);

            // Извлечение HTML-тегов и их содержимого
            String extractedText = extractContentFromHtml(firstResultPage);

            // Удаление HTML-тегов
            extractedText = removeHtmlTags(extractedText);

            // Обрезаем текст, если он превышает 4000 символов
            if (extractedText.length() > 4000) {
                extractedText = extractedText.substring(0, 4000);
            }
            String beforeLastDot = extractedText.substring(0, extractedText.lastIndexOf(".")).trim();
            return beforeLastDot + "\n Информация была взята из: " + firstResultUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка ввода, измените запрос для корректной работы краулера";
        }
    }

    private static String encodeQuery(String query) {
        return query.replace(" ", "+");
    }

    private static String getPageContent(String pageUrl) throws Exception {
        StringBuilder content = new StringBuilder();
        URL url = new URL(pageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        return content.toString();
    }
    private static String extractHtmlTags(String html) {
        StringBuilder resultBuilder = new StringBuilder();
        int pos = 0;
        while (pos < html.length()) {
            int tagStart = html.indexOf('<', pos);
            if (tagStart == -1) {
                break;
            }
            int tagEnd = html.indexOf('>', tagStart);
            if (tagEnd == -1) {
                break;
            }
            String tag = html.substring(tagStart, tagEnd + 1);

            resultBuilder.append(tag);
            pos = tagEnd + 1;
        }
        return resultBuilder.toString();
    }

    private static String removeHtmlTags(String htmlContent) {
        Pattern pattern = Pattern.compile("<[^>]+>");
        Matcher matcher = pattern.matcher(htmlContent);
        return matcher.replaceAll("");
    }

    private static String extractFirstResultUrl(String html) {
        // Ищем блок с результатами поиска
        int startMarkerIndex = html.indexOf("<ol id=\"b_results\"");
        if (startMarkerIndex == -1) {
            return null;
        }
        // Ищем первую ссылку на результат поиска
        int urlStartIndex = html.indexOf("<a href=\"", startMarkerIndex);
        if (urlStartIndex == -1) {
            return null;
        }
        urlStartIndex += "<a href=\"".length();
        int urlEndIndex = html.indexOf("\"", urlStartIndex);
        if (urlEndIndex == -1) {
            return null;
        }
        return html.substring(urlStartIndex, urlEndIndex);
    }

    private static String extractContentFromHtml(String html) {
        StringBuilder extractedContent = new StringBuilder();

        // Указываем тег, содержимое которого мы хотим извлечь
        String tag = "p";

        // Ищем все вхождения тега <p>
        int startIndex = html.indexOf("<" + tag + ">");
        while (startIndex != -1) {
            // Находим индекс конца тега </p>
            int endIndex = html.indexOf("</" + tag + ">", startIndex);
            if (endIndex == -1) {
                break;
            }

            // Извлекаем содержимое тега и добавляем его к общему результату
            String content = html.substring(startIndex + ("<" + tag + ">").length(), endIndex).trim();
            if (!content.isEmpty()) {
                extractedContent.append(content).append("\n");
            }

            // Переходим к следующему вхождению тега <p>
            startIndex = html.indexOf("<" + tag + ">", endIndex);
        }

        return extractedContent.toString();
    }
}