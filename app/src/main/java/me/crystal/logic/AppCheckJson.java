package me.crystal.logic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

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
     * Fetches and parses JSON data from the specified URL and returns a map of application names to download links.
     * @return a map containing application names as keys and download links as values.
     */
    public static Map<String, String> getContentFromAppCheckJson() {
        try {
            String urlString = "https://raw.githubusercontent.com/Crystal15118/AppCheck/main/resources/Applications.json";
            JsonObject jsonObject = fetchJsonFromUrl(urlString);
            JsonArray applications = jsonObject.getAsJsonArray("applications");
            Map<String, String> appsMap = new TreeMap<>();

            for (int i = 0; i < applications.size(); i++) {
                JsonObject app = applications.get(i).getAsJsonObject();
                String name = app.get("name").getAsString();
                String downloadLink = app.get("downloadLink").getAsString();
                appsMap.put(name, downloadLink);
            }

            return appsMap;
        } catch (IOException e) {
            throw new RuntimeException("Error fetching or parsing JSON data", e);
        }
    }
}
