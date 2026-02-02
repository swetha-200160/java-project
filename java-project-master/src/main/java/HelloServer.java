import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class HelloServer {
    public static void main(String[] args) throws Exception {

        int port = 9090; // default

        if (System.getenv("APP_PORT") != null) {
            port = Integer.parseInt(System.getenv("APP_PORT"));
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.start();

        System.out.println("Server started on port " + port);
    }
}

