package model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class MovieTest {

	private final ObjectsForTests tests = new ObjectsForTests();

	@Test
	public void smallFishMovie() {
		var smallFish = tests.createSmallFishMovie();

		assertTrue(smallFish.hasDurationOf(102));
		assertFalse(smallFish.hasDurationOf(10));
		assertTrue(smallFish.isNamed("Small Fish"));
		assertTrue(smallFish.isCharacterNamed("George Bix"));
		assertTrue(smallFish.hasReleaseDateOf(LocalDate.of(2023, 10, 10)));
		assertTrue(smallFish.hasGenresOf(List.of(Genre.COMEDY, Genre.ACTION)));
		assertTrue(smallFish.hasARole("aName aSurname"));
		assertTrue(smallFish.isDirecting("aDirectorName aDirectorSurname"));
	}

	@Test
	public void movieNameIsInvalid() {
		Exception e = assertThrows(BusinessException.class, () -> {
			new Movie("  ", 102, LocalDate.of(2023, 10, 10) /* release data */,
					Set.of(Genre.COMEDY, Genre.ACTION)/* genre */,
					Set.of(new Actor(new Person("aName", "aSurname"),
							"George Bix")),
					Set.of(new Person("aDName", "aDSurname")));
		});

		assertTrue(e.getMessage().equals(Movie.MOVIE_NAME_INVALID));
	}

	@Test
	public void durationIsInvalid() {
		Exception e = assertThrows(BusinessException.class, () -> {
			new Movie("Small Fish", 0,
					LocalDate.of(2023, 10, 10) /* release data */,
					Set.of(Genre.COMEDY, Genre.ACTION)/* genre */,
					Set.of(new Actor(new Person("aName", "aSurname"),
							"George Bix")),
					Set.of(new Person("aDName", "aDSurname")));
		});

		assertTrue(e.getMessage().equals(Movie.DURATION_INVALID));
	}

	@Test
	public void genreIsInvalid() {
		Exception e = assertThrows(BusinessException.class, () -> {
			new Movie("Small Fish", 100,
					LocalDate.of(2023, 10, 10) /* release data */,
					Set.of()/* genre */,
					Set.of(new Actor(new Person("aName", "aSurname"),
							"George Bix")),
					Set.of(new Person("aDName", "aDSurname")));
		});

		assertTrue(e.getMessage().equals(Movie.GENRES_INVALID));
	}

	@Test
	public void actorsIsInvalid() {
		Exception e = assertThrows(BusinessException.class, () -> {
			new Movie("Small Fish", 100,
					LocalDate.of(2023, 10, 10) /* release data */,
					Set.of(Genre.ACTION)/* genre */, Set.of(),
					Set.of(new Person("aDName", "aDSurname")));
		});
		assertTrue(e.getMessage().equals(Movie.ACTORS_INVALID));
	}

	@Test
	public void directorsIsInvalid() {
		Exception e = assertThrows(BusinessException.class, () -> {
			new Movie("Small Fish", 100,
					LocalDate.of(2023, 10, 10) /* release data */,
					Set.of(Genre.ACTION)/* genre */,
					Set.of(new Actor(new Person("aName", "aSurname"),
							"George Bix")),
					Set.of());
		});

		assertTrue(e.getMessage().equals(Movie.DIRECTORS_INVALID));
	}

	@Test
	public void directorsWithBlankNames() {
		Exception e = assertThrows(BusinessException.class, () -> {
			new Movie("Small Fish", 100,
					LocalDate.of(2023, 10, 10) /* release data */,
					Set.of(Genre.ACTION)/* genre */,
					Set.of(new Actor(new Person("aName", "aSurname"),
							"George Bix")),
					Set.of(new Person(" ", "aSurname"),
							new Person("aName", "aSurname")));
		});

		assertTrue(e.getMessage().equals(Person.NAME_MUST_NOT_BE_BLANK));
	}
}
