import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {

```
public static void main(String[] args) throws IOException {

    HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0
    );

    server.createContext("/", (HttpExchange exchange) -> {

        String response = "CI/CD Security Project is running!";

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/plain"
        );

        exchange.sendResponseHeaders(
                200,
                response.getBytes().length
        );

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response.getBytes());
        }
    });

    server.start();

    System.out.println("Application running on port 8080");
}
```

}
