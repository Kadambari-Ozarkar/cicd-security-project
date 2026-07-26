import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {

        // Create HTTP server on port 8080
        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        // Handle requests
        server.createContext("/", exchange -> {

            String response = "CI/CD Security Project is running!";

            exchange.sendResponseHeaders(
                    200,
                    response.getBytes().length
            );

            try (OutputStream output = exchange.getResponseBody()) {
                output.write(response.getBytes());
            }
        });

        // Start server
        server.start();

        System.out.println("Application running on port 8080");

        // Keep Java process alive
        Thread.currentThread().join();
    }
}
