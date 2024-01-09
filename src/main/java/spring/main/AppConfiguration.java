package spring.main;

import jakarta.persistence.EntityManagerFactory;
import model.Cinema;
import model.api.CinemaSystem;
import model.mail.TheBestEmailProvider;
import model.payment.PleasePayPaymentProvider;
import model.token.PasetoToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.YearMonth;

@Configuration
public class AppConfiguration {

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    // this secret should not be here
    private static final String SECRET = "nXXh3Xjr2T0ofFilg3kw8BwDEyHmS6OIe4cjWUm2Sm0=";

    @Bean
    @Profile("default")
    public CinemaSystem create() {
        addSampleData();
        return new Cinema(entityManagerFactory, new PleasePayPaymentProvider(),
                new TheBestEmailProvider(),
                new PasetoToken(SECRET), 10 /*
         * page size
         */);
    }

    @Bean
    @Profile("test")
    public CinemaSystem createForTest() {
        String ANY_SECRET = "Kdj5zuBIBBgcWpv9zjKOINl2yUKUXVKO+SkOVE3VuZ4=";
        addSampleData();
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

    private void addSampleData() {
        new SetUpDb(entityManagerFactory)
                .createSchemaAndPopulateSampleData();
    }
}
