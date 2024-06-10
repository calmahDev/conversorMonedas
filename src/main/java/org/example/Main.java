package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // URL de la API
            String url = "https://v6.exchangerate-api.com/v6/a5118714474958cab7b89fa9/latest/USD";

            // Crear una solicitud HTTP
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            // Enviar la solicitud y obtener la respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Manejar la respuesta
            JsonObject conversionRates = handleResponse(response);

            if (conversionRates != null) {
                // Interactuar con el usuario a través de un menú
                Scanner scanner = new Scanner(System.in);
                boolean exit = false;

                while (!exit) {
                    System.out.println("Conversor de Monedas");
                    System.out.println("1. Convertir moneda");
                    System.out.println("2. Salir");
                    System.out.print("Seleccione una opción: ");
                    int option = scanner.nextInt();

                    switch (option) {
                        case 1:
                            System.out.print("Ingrese la cantidad que desea convertir: ");
                            double amount = scanner.nextDouble();
                            System.out.print("Ingrese el código de la moneda de origen (ej. USD): ");
                            String fromCurrency = scanner.next().toUpperCase();
                            System.out.print("Ingrese el código de la moneda de destino (ej. EUR): ");
                            String toCurrency = scanner.next().toUpperCase();

                            try {
                                double convertedAmount = convertCurrency(amount, fromCurrency, toCurrency, conversionRates);
                                System.out.println(amount + " " + fromCurrency + " son " + convertedAmount + " " + toCurrency);
                            } catch (IllegalArgumentException e) {
                                System.out.println("Código de moneda inválido. Intente nuevamente.");
                            }
                            break;
                        case 2:
                            exit = true;
                            System.out.println("Gracias por usar el conversor de monedas.");
                            break;
                        default:
                            System.out.println("Opción inválida. Intente nuevamente.");
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static JsonObject handleResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            System.out.println("Status Code: " + statusCode);

            // Parsear el cuerpo de la respuesta
            String responseBody = response.body();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

            // Extraer las tasas de cambio
            return jsonObject.getAsJsonObject("conversion_rates");
        } else {
            System.err.println("Error: " + statusCode);
            return null;
        }
    }

    private static double convertCurrency(double amount, String fromCurrency, String toCurrency, JsonObject conversionRates) {
        if (conversionRates.has(fromCurrency) && conversionRates.has(toCurrency)) {
            double fromRate = conversionRates.get(fromCurrency).getAsDouble();
            double toRate = conversionRates.get(toCurrency).getAsDouble();
            return amount * (toRate / fromRate);
        } else {
            throw new IllegalArgumentException("Invalid currency code");
        }
    }
}
