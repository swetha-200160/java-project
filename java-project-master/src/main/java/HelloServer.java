import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class HelloServer {

    public static void main(String[] args) throws Exception {

        int tempPort = 9090;

        String envPort = System.getenv("APP_PORT");
        if (envPort != null) {
            tempPort = Integer.parseInt(envPort);
        }

        final int port = tempPort; // ✅ FINAL variable for lambda

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> {
            String response = "Hello! Server running on port " + port;
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        });

        server.start();
        System.out.println("Server started on port " + port);
    }
}
