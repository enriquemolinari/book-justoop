package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class ForTests {

	Movie createSmallFishMovie() {
		return new Movie("Small Fish", 102,
				LocalDate.of(2023, 10, 10) /* release data */,
				Set.of(Genre.COMEDY, Genre.ACTION)/* genre */,
				Set.of(new Actor(new Person("aName", "aSurname"),
						"George Bix")),
				Set.of(new Person("aDirectorName", "aDirectorSurname")),
				(user, movie) -> {
					return false;
				});
	}

	EmailProvider emptyProvider() {
		return (to, subject, body) -> {
		};
	}

	CreditCardPaymentGateway emptyPayment() {
		return (creditCardNumber, expire, securityCode, totalAmount) -> {
		};
	}

	Movie createSmallFishMovieWithRates() {
		return new Movie("Small Fish", 102,
				LocalDate.of(2023, 10, 10) /* release data */,
				Set.of(Genre.COMEDY, Genre.ACTION)/* genre */,
				Set.of(new Actor(new Person("aName", "aSurname"),
						"George Bix")),
				Set.of(new Person("aDirectorName", "aDirectorSurname")),
				InMemoryUsersRating.carlosRatedSmallFish());
	}

	ShowTime createShowForSmallFish(DateTimeProvider provider) {
		return new ShowTime(DateTimeProvider.create(), createSmallFishMovie(),
				LocalDateTime.of(2023, 10, 10, 15, 0, 0, 0), 10f,
				new Theater("a Theater", Set.of(1, 2, 3, 4, 5, 6), provider),
				emptyPayment(), emptyProvider());
	}

	ShowTime createShowForSmallFish() {
		return new ShowTime(DateTimeProvider.create(), createSmallFishMovie(),
				LocalDateTime.of(2023, 10, 10, 15, 0, 0, 0), 10f,
				new Theater("a Theater", Set.of(1, 2, 3, 4, 5, 6),
						DateTimeProvider.create()),
				emptyPayment(), emptyProvider());
	}

	User createUserCharly() {
		return new User(new Person("Carlos", "Edgun"), "cedgun",
				"cedgun@mysite.com", "afbcdefghigg");
	}

	User createUserJoseph() {
		return new User(new Person("Joseph", "Valdun"), "jvaldun",
				"jvaldun@wabla.com", "tabcd1234igg");
	}

	User createUserNicolas() {
		return new User(new Person("Nicolas", "Molinari"), "nmolinari",
				"nmolinari@yesmy.com", "oneplayminebrawl");
	}
}
