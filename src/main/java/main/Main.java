package main;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("derby-cinema");

		var db = new SetUpDb(emf);
		db.createSchemaAndPopulateSampleData();
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
