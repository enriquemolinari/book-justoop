package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class Main {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("derby-cinema");
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			// createData(em);

			var ssq = em.createQuery("from ShowTime s where s.id = :idshow",
					ShowTime.class);

			// var ssq = em.createQuery(
			// "from ShowSeat s where s.show.id = :idshow and s.seatNumber =
			// :idseat",
			// ShowSeat.class);
			ssq.setParameter("idshow", 1L);

			var st = ssq.getResultList().get(0);

			var user = em.find(User.class, 1L);

			st.reserveSeatsFor(user, Set.of(1));
			Sale s = st.confirmSeatsFor(user, Set.of(1), "", null, "");
			System.out.println(s);
			// var movie = em.find(Movie.class, 1L);
			// var user = em.find(User.class, 1L);
			// movie.rateBy(user, 5, "comment 1");

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			em.close();
			emf.close();
		}
	}

	private static void createData(EntityManager em) {
		var p1 = new Person("an Actor Name", "an actor surname");
		em.persist(p1);

		var p2 = new Person("a Director Name", "A director surname");
		em.persist(p2);

		User u = new User(p1, "user1", "abc@bla.com", "abcdefg12hi34");
		em.persist(u);

		var smallFish = new Movie("Small Fish", 102,
				LocalDate.of(2023, 10, 10) /* release data */,
				Set.of(Genre.COMEDY, Genre.ACTION)/* genre */,
				Set.of(new Actor(p1, "George Bix")), Set.of(p1, p2),
				(user, movie) -> {
					return false;
				});

		var t = new Theater("una sala", Set.of(1), DateTimeProvider.create());
		em.persist(t);

		em.persist(smallFish);

		// var s = new Seat(1);
		// em.persist(s);

		var st = new ShowTime(DateTimeProvider.create(), smallFish,
				LocalDateTime.now().plusHours(1), 10f, t,
				(creditCardNumber, expire, securityCode, totalAmount) -> {
				}, (to, subject, body) -> {
				});

		em.persist(st);

	}
}
