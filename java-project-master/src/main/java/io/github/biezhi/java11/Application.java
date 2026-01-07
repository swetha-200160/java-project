package io.github.biezhi.java11;

public class Application {
    public static void main(String[] args) throws Exception {
        System.out.println("Java app started");

        // keep container alive
        while (true) {
            Thread.sleep(60000);
        }
    }
}
