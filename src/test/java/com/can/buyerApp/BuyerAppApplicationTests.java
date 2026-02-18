package com.can.buyerApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.can.buyerApp.repository")
@EntityScan(basePackages = "com.can.buyerApp.entity")
public class BuyerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(BuyerAppApplication.class, args);
    }
}
