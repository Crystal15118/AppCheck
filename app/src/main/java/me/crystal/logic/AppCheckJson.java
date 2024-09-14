package me.crystal.logic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AppCheckJson {

    /**
     * Fetches JSON data from the specified URL and returns it as a JsonObject.
     *
     * @param urlString the URL of the JSON file.
     * @return a JsonObject containing the JSON data.
     * @throws IOException if an I/O error occurs.
     */
    public static JsonObject fetchJsonFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream());
             Scanner scanner = new Scanner(reader)) {
            StringBuilder jsonString = new StringBuilder();
            while (scanner.hasNext()) {
                jsonString.append(scanner.nextLine());
            }
            return JsonParser.parseString(jsonString.toString()).getAsJsonObject();
        }
    }

    /**
     * Fetches and parses JSON data from the specified URL.
     *
     * @param urlString the URL of the JSON file.
     * @return a JsonObject containing the parsed JSON data.
     */
    public static JsonObject getJsonData(String urlString) {
        try {
            return fetchJsonFromUrl(urlString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}