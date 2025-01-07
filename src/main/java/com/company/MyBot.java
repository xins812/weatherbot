package com.company;


import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class MyBot extends TelegramLongPollingBot {

    private static final String API_KEY = "2f90e84247af1397d5f6e30ff9ce56e7";
    private String response;

    @Override
    public String getBotUsername() {
        return "https://t.me/Obhavopdpbot";
    }

    @Override
    public String getBotToken() {
        return "7316975724:AAHhIY0MfRCOtBHPmpAKYr5MLRt1r6mAVGc";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/start")) {
                sendMessage(chatId, "salom shaxarni nomini yozing va ob havoni bilib oling");
            } else if (messageText.startsWith("/weather")) {
                String city = messageText.substring(9).trim();
                if (!city.isEmpty()) {
                    String weatherInfo = getWeather(city);
                    sendMessage(chatId, weatherInfo);
                } else {
                    sendMessage(chatId, "iltimos shaxar nomini kirirting");
                }
            } else if (messageText.startsWith("/help")) {
                sendMessage(chatId, "ishlating /weather <shaxar nomi > ob havoni bilish uchun");
            } else {
                sendMessage(chatId, "men sizni tushunmadim bunda bor komandalarni korishingiz mumkun /help ");
            }
        }
    }
    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private String getWeather(String city) {
        try {

            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric&lang=ru";


            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");

            // Проверяем статус ответа
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Читаем ответ
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Парсим JSON-ответ
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject main = jsonResponse.getJSONObject("main");
                JSONObject weather = jsonResponse.getJSONArray("weather").getJSONObject(0);

                double temperature = main.getDouble("temp");
                int humidity = main.getInt("humidity");
                String weatherDescription = weather.getString("description");

                return String.format("Ob havo shaxarda %s:\nTempratura: %.1f°C\nNamlik: %d%%\nIzoh: %s",
                        city, temperature, humidity, weatherDescription);
            } else {
                return "Shaxar topilmadi. Yana bir marta urinib ko'ring.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Xato yuz berdi. Keyinroq urinib ko'ring.";
        }
    }}