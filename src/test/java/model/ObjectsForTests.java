package model;

import java.time.LocalDate;
import java.util.Set;

public class ObjectsForTests {

	Movie createSmallFishMovie() {
		return new Movie("Small Fish", 102,
				LocalDate.of(2023, 10, 10) /* release data */,
				Set.of(Genre.COMEDY, Genre.ACTION)/* genre */,
				Set.of(new Actor(new Person("aName", "aSurname"),
						"George Bix")),
				Set.of(new Person("aDirectorName", "aDirectorSurname")));
	}

}
