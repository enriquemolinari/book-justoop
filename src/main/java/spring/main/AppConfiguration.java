package spring.main;

import jakarta.persistence.EntityManagerFactory;
import model.Cinema;
import model.PasetoToken;
import model.PleasePayPaymentProvider;
import model.TheBestEmailProvider;
import model.api.CinemaSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AppConfiguration {

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    // this secret should not be here
    private static final String SECRET = "nXXh3Xjr2T0ofFilg3kw8BwDEyHmS6OIe4cjWUm2Sm0=";

    @Bean
    @Profile("default")
    public CinemaSystem create() {
        new SetUpDb(entityManagerFactory)
                .createSchemaAndPopulateSampleData();
        return new Cinema(entityManagerFactory, new PleasePayPaymentProvider(),
                new TheBestEmailProvider(),
                new PasetoToken(SECRET), 10 /*
         * page size
         */);
    }

}
