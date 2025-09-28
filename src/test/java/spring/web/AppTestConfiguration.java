package spring.web;

import app.Cinema;
import app.PasetoToken;
import app.api.CinemaSystem;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
