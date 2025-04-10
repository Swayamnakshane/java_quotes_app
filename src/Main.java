import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    private static List<String> quotes;

    public static void main(String[] args) throws IOException {
        // Load quotes from an external file
        quotes = loadQuotesFromFile("quotes.txt");

        if (quotes.isEmpty()) {
            System.err.println("No quotes found in the file. Please ensure 'quotes.txt' has content.");
            return;
        }

        // Create an HTTP server listening on 0.0.0.0:8000 for external access
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8000), 0);

        // Define a context for the root path ("/")
        server.createContext("/", exchange -> {
            try {
                handleRequest(exchange);
            } catch (Exception e) {
                e.printStackTrace();
                String error = "{\"error\": \"Internal Server Error\"}";
                exchange.sendResponseHeaders(500, error.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(error.getBytes());
                }
            }
        });

        // Start the server
        server.start();
        System.out.println("Server is running on http://0.0.0.0:8000/");
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        // Only allow GET requests
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        // Get a random quote
        String quote = getRandomQuote();

        // Create a JSON response
        String jsonResponse = String.format("{\"quote\": \"%s\"}", quote.replace("\"", "\\\""));

        // Convert the JSON response to bytes
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

        // Set the response headers
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseBytes.length);

        // Write the JSON response to the output stream
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    // Helper method to get a random quote
    private static String getRandomQuote() {
        Random random = new Random();
        return quotes.get(random.nextInt(quotes.size()));
    }

    // Helper method to load quotes from an external file
    private static List<String> loadQuotesFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {
            return reader.lines().filter(line -> !line.trim().isEmpty()).collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error reading quotes file: " + e.getMessage());
            return List.of();
        }
    }
}
