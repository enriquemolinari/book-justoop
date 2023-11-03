package web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManagerFactory;
import model.Cinema;
import model.PasetoToken;
import model.api.CinemaSystem;

@Configuration
public class AppConfiguration {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	private static final String PERSISTENCE_UNIT = "derby-cinema";

	private String secret = "nXXh3Xjr2T0ofFilg3kw8BwDEyHmS6OIe4cjWUm2Sm0=";

	// TODO: definir como inyectar las implementaciones de email y payment
	@Bean
	public CinemaSystem create() {
		addSampleData();
		return new Cinema(entityManagerFactory, null, null,
				new PasetoToken(secret), 2 /*
											 * page size
											 */);
	}

	private void addSampleData() {
		new SetUpDb(entityManagerFactory)
				.createSchemaAndPopulateSampleData();
	}
}
