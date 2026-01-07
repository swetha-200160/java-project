package io.github.biezhi.java11.http;

import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleServer {

    public static void main(String[] args) throws Exception {
        System.out.println("Server started");

        while (true) {
            Thread.sleep(10000);
            System.out.println("Still running...");
        }
    }
}
