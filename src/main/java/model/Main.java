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

			createData(em);

			// var ssq = em.createQuery(
			// "from ShowSeat s where s.show.id = :idshow and s.seat.id =
			// :idseat",
			// ShowSeat.class);
			// ssq.setParameter("idshow", 1L);
			// ssq.setParameter("idseat", 1L);
			//
			// var ss = ssq.getResultList().get(0);
			//
			// var user = em.find(User.class, 1L);
			//
			// ss.reserveFor(user);

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

		User u = new User(p1, "user1");
		em.persist(u);

		var smallFish = new Movie("Small Fish", 102,
				LocalDate.of(2023, 10, 10) /* release data */,
				Set.of(Genre.COMEDY, Genre.ACTION)/* genre */,
				Set.of(new Actor(p1, "George Bix")), Set.of(p1, p2));

		var t = new Theater("una sala", Set.of(1));
		em.persist(t);

		em.persist(smallFish);

		// var s = new Seat(1);
		// em.persist(s);

		var st = new ShowTime(DateTimeProvider.create(), smallFish,
				LocalDateTime.now().plusHours(1), 10f, t);

		em.persist(st);
	}
}
