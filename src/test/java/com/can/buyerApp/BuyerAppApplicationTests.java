@SpringBootApplication
@ComponentScan(basePackages = "com.can.buyerApp")
@EnableJpaRepositories(basePackages = "com.can.buyerApp.repository")
public class BuyerAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(BuyerAppApplication.class, args);
    }
}

