package spring.web;

import jakarta.persistence.EntityManagerFactory;
import model.Cinema;
import model.PasetoToken;
import model.api.CinemaSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import spring.main.SetUpDb;

import java.time.YearMonth;

@Configuration
@Profile("test")
public class AppTestConfiguration {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    // this secret should not be here

    @Bean
    public CinemaSystem createForTest() {
        String ANY_SECRET = "Kdj5zuBIBBgcWpv9zjKOINl2yUKUXVKO+SkOVE3VuZ4=";
        new SetUpDb(entityManagerFactory)
                .createSchemaAndPopulateSampleData();
        return new Cinema(entityManagerFactory,
                (String creditCardNumber, YearMonth expire, String securityCode,
                 float totalAmount) -> {
                },
                (String to, String subject, String body) -> {
                },
                new PasetoToken(ANY_SECRET),
                2 /*
         * page size
         */);
    }

}
