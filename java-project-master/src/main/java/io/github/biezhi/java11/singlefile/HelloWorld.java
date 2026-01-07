package io.github.biezhi.java11.singlefile;

public class HelloWorld {

    public static void main(String[] args) throws Exception {
        System.out.println("App started");

        // KEEP THE JAVA PROCESS RUNNING
        Thread.sleep(Long.MAX_VALUE);
    }
}
