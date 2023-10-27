package com.projects.currencyconverter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller {

    @FXML
    private ComboBox currencyOneBox, currencyTwoBox;
    private String currencyOne, currencyTwo, apiKey;
    private int currencyOneAmount, currencyTwoAmount;
    private ArrayList<String> currencyList;
    @FXML
    private ImageView logo;
    @FXML
    private TextField enterAmountField;
    @FXML
    private Label resultLabel;
    @FXML
    private Button convertButton;

    public void initialize() {
        getApiKey();

        loadLogo();
        try {
            currencyList = getCurrencies();
            ObservableList<String> observableCurrencyList = FXCollections.observableArrayList(currencyList);
            currencyOneBox.setItems(observableCurrencyList);
            currencyTwoBox.setItems(observableCurrencyList);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    @FXML
    private void setCurrencyOne() {
        currencyOne = currencyOneBox.getValue().toString();
        System.out.println(currencyOne);
    }

    @FXML
    private void setCurrencyTwo() {
        currencyTwo = currencyTwoBox.getValue().toString();
        System.out.println(currencyTwo);
    }

    private ArrayList<String> getCurrencies() throws IOException {
        try {
            URL url = new URL("https://api.apilayer.com/currency_data/list");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("apikey", apiKey);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response as an InputStream
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                ObjectMapper objectMapper = new ObjectMapper();

                JsonNode jsonNode = objectMapper.readTree(response.toString());


                // Get the currencies as a JsonNode
                JsonNode currencies = jsonNode.get("currencies");
//                System.out.println(currencies);
                HashMap<String, String> currencyMap = objectMapper.convertValue(currencies, HashMap.class);

                ArrayList<String> returnList = new ArrayList<>(currencyMap.keySet());
                returnList.sort(String::compareToIgnoreCase);
                connection.disconnect();

                return returnList;
            } else {
                System.out.println("Failed to get a valid HTTP response.");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    private void loadLogo() {
        logo.setImage(new javafx.scene.image.Image("com/projects/currencyconverter/logo.png"));
    }

    private void getApiKey() {
        BufferedReader reader = null;
        try {
            String filePath = "src/main/resources/com/projects/currencyconverter/apikey.txt";
            reader = new BufferedReader(new java.io.FileReader(filePath));
            apiKey = reader.readLine();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }


    @FXML
    private void convertCurrency() throws IOException {
        try {
            int amount = Integer.parseInt(enterAmountField.getText());
            URL url = new URL("https://api.apilayer.com/currency_data/convert?from=" + currencyOne + "&to=" + currencyTwo + "&amount=" + amount + "&apikey=" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.toString());

                System.out.println(jsonNode.toString());

                JsonNode result = jsonNode.get("result");
                System.out.println(result);
                String resultString = currencyOne + " " + amount + " = " + currencyTwo + " " + result.toString();
                resultLabel.setText(resultString);
            }
            else {
                System.out.println("Failed to get a valid HTTP response.");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}