package web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManagerFactory;
import model.Cinema;
import model.api.CinemaSystem;

@Configuration
public class AppConfiguration {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	// TODO: definir como inyectar las implementaciones de email y payment
	@Bean
	public CinemaSystem create() {
		return new Cinema(entityManagerFactory, null, null);
	}
}
