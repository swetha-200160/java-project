package io.github.biezhi.java11;

import java.io.FileWriter;
import java.time.LocalDateTime;

public class HttpClientExample {

    public static void main(String[] args) throws Exception {
        FileWriter fw = new FileWriter("/data/app.log", true);
        fw.write("App started at " + LocalDateTime.now() + "\n");
        fw.close();

        System.out.println("Log written to /data/app.log");
        Thread.sleep(600000); // keep container running
    }
}
