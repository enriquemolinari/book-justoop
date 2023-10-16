package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import main.SetUpDb;
import model.api.ActorInMovieName;

public class CinemaTest {

	private static final String RUNNING_FAR_AWAY_MOVIE_NAME = "Running far Away";
	private static final String CRASH_TEA_MOVIE_NAME = "Crash Tea";
	private static final String SMALL_FISH_MOVIE_NAME = "Small Fish";
	private final ForTests tests = new ForTests();

	private static EntityManagerFactory emf;

	@BeforeAll
	public static void setUp() {
		emf = Persistence.createEntityManagerFactory("test-derby-cinema");
		// Important: Since this system is not fully implemented, I'm going to
		// take advantage of the setupDb sample data to write tests.
		// However, if the full system is implemented,
		// movie, shows and users creation services needs to be implemented and
		// in that case I would rather use them to populate the system in each
		// test.
		// of course this couple these test to that setup sample data
		new SetUpDb(emf).createSchemaAndPopulateSampleData();
	}

	@Test
	public void bla() {
		var cinema = new Cinema(emf, tests.doNothingPaymentProvider(),
				tests.doNothingEmailProvider());
		var showInfo = cinema.addNewShowToMovie(8L,
				LocalDateTime.now().plusDays(1), 10f, 1L, 10);

		System.out.println(showInfo);
	}

	@Test
	public void aShowIsPlayingNow() {
		var cinema = new Cinema(emf, tests.doNothingPaymentProvider(),
				tests.doNothingEmailProvider());

		// var movieInfo = cinema.addNewMovie("a super movie", 109,
		// LocalDate.of(2023, 04, 05),
		// "a super movie that shows the life of ...",
		// Set.of(Genre.ACTION, Genre.ADVENTURE));

		var movieShows = cinema.showsUntil(LocalDateTime.now().plusHours(3));

		assertEquals(1, movieShows.size());
		assertEquals(1, movieShows.get(0).shows().size());
		assertTrue(movieShows.get(0).shows().get(0).price() == 19f);
		assertTrue(movieShows.get(0).movieName()
				.equals(RUNNING_FAR_AWAY_MOVIE_NAME));
	}

	@Test
	public void retrieveAllMovies() {
		var cinema = new Cinema(emf, tests.doNothingPaymentProvider(),
				tests.doNothingEmailProvider());
		var movies = cinema.moviesSortedByName();

		assertEquals(4, movies.size());
		assertTrue(movies.get(0).name().equals(CRASH_TEA_MOVIE_NAME));
		assertTrue(movies.get(3).name().equals(SMALL_FISH_MOVIE_NAME));
	}

	@Test
	public void addDirectorAndActor() {
		var cinema = new Cinema(emf, tests.doNothingPaymentProvider(),
				tests.doNothingEmailProvider());
		var movieInfo = cinema.addActorToMovie(1L, "Carlos", "Kalchi",
				"carlosk@bla.com", "aCharacterName");

		assertTrue(movieInfo.actors().contains(
				new ActorInMovieName("Carlos Kalchi", "aCharacterName")));
	}

	@AfterAll
	public static void tearDown() {
		emf.close();
	}

}
