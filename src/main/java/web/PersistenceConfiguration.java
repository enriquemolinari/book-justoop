package web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;

/**
 * This is required to use the persistence.xml
 */
@Configuration
public class PersistenceConfiguration {

	private static final String PERSISTENCE_UNIT = "derby-cinema";

	@Bean(name = "entityManagerFactory")
	public LocalEntityManagerFactoryBean createEntityManagerFactory() {
		LocalEntityManagerFactoryBean factory = new LocalEntityManagerFactoryBean();
		factory.setPersistenceUnitName(PERSISTENCE_UNIT);
		return factory;
	}
}
