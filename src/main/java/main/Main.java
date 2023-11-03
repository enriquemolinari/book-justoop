package main;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Cinema;

public class Main {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("derby-cinema");

		var cinema = new Cinema(emf, null, null, null);

		cinema.registerUser("aaaa", "bbb", "em@bla.com", "userName18",
				"abcdrefsdfsd234", "abcdrefsdfsd234");

		// var movieInfo = cinema.addNewMovie("super", 109,
		// LocalDate.of(2023, 04, 05),
		// "plot",
		// Set.of(Genre.ACTION, Genre.ADVENTURE));
		//
		// cinema.addActorToMovie(movieInfo.id(), "Carlos", "Kalchi",
		// "carlosk@bla.com", "aCharacterName");
		//
		// cinema.addActorToMovie(movieInfo.id(), "Jose", "Hermes",
		// "jose@bla.com", "anotherCharacterName");
		//
		// cinema.addDirectorToMovie(movieInfo.id(), "aDirectorName", "surname",
		// "adir@bla.com");
		//
		// long theaterId = cinema.addNewTheater("a Theater",
		// Set.of(1, 2, 3, 4, 5, 6));
		//
		// var showInfo = cinema.addNewShowToMovie(movieInfo.id(),
		// LocalDateTime.of(2024, 10, 10, 13, 30), 10f, theaterId, 20);
		//
		// var userId = cinema.registerUser("aUser", "aSurname",
		// "enrique@bla.com",
		// "username",
		// "password12345678", "password12345678");
		//
		// var info = cinema.reserve(userId, showInfo.idShow(), Set.of(1, 5));

		// var movieInfo = cinema.addNewMovie("a super movie", 109,
		// LocalDate.of(2023, 04, 05),
		// "a super movie that shows the life of ...",
		// Set.of(Genre.ACTION, Genre.ADVENTURE));
		//
		// long theaterId = cinema.addNewTheater("a Theater",
		// Set.of(1, 2, 3, 4, 5, 6));
		//
		// var show = cinema.addNewShowToMovie(movieInfo.id(),
		// LocalDateTime.of(2024, 10, 10, 13, 30), 10f, theaterId, 20);
		//
		// var db = new SetUpDb(emf);
		// db.createSchemaAndPopulateSampleData();
		// EntityManager em = emf.createEntityManager();
		// EntityTransaction tx = em.getTransaction();
		//
		// try {
		// tx.begin();
		// var user = em.find(User.class, 1L);
		// System.out.println(user.toMap());
		//
		// tx.commit();
		// } catch (Exception e) {
		// tx.rollback();
		// System.out.println(e);
		// } finally {
		// em.close();
		// emf.close();
		// }

		// try (EntityManager em = emf.createEntityManager()) {
		//
		// var user = em.find(User.class, 1L);
		// System.out.println(user.toMap());
		//
		// }

	}
}
