package spring.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManagerFactory;
import model.Cinema;
import model.api.CinemaSystem;
import model.mail.TheBestEmailProvider;
import model.payment.PleasePayPaymentProvider;
import model.token.PasetoToken;

@Configuration
public class AppConfiguration {

	@Autowired
	private EntityManagerFactory entityManagerFactory;
	private String secret = "nXXh3Xjr2T0ofFilg3kw8BwDEyHmS6OIe4cjWUm2Sm0=";

	@Bean
	public CinemaSystem create() {
		addSampleData();
		return new Cinema(entityManagerFactory, new PleasePayPaymentProvider(),
				new TheBestEmailProvider(),
				new PasetoToken(secret), 2 /*
											 * page size
											 */);
	}

	private void addSampleData() {
		new SetUpDb(entityManagerFactory)
				.createSchemaAndPopulateSampleData();
	}
}
