package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import model.api.CreditCardPaymentGateway;
import model.api.EmailProvider;
import model.api.Genre;

public class ForTests {

	EmailProviderFake fakeEmailProvider() {
		return new EmailProviderFake();
	}

	PaymenentProviderFake fakePaymenentProvider() {
		return new PaymenentProviderFake();
	}

	String createEmailBody(String userName, String movieName,
			Set<Integer> seats, String showTime, float amount) {
		var orderedListofSeats = new ArrayList<>(seats);
		Collections.sort(orderedListofSeats);

		var body = new StringBuilder();
		body.append("Hello ").append(userName).append("!");
		body.append(System.lineSeparator());
		body.append("You have new tickets!");
		body.append(System.lineSeparator());
		body.append("Here are the details of your booking: ");
		body.append(System.lineSeparator());
		body.append("Movie: ").append(movieName);
		body.append(System.lineSeparator());
		body.append("Seats: ").append(orderedListofSeats.stream()
				.map(s -> s.toString()).collect(Collectors.joining(",")));
		body.append(System.lineSeparator());
		body.append("Showtime: ").append(showTime);
		body.append(System.lineSeparator());
		body.append("Total paid: ").append(amount);

		return body.toString();
	}

	ShowTime createShowTime(CreditCardPaymentGateway gProvider,
			EmailProvider eProvider, int pointsToWin) {
		return new ShowTime(
				DateTimeProvider.create(), this.createSmallFishMovie(),
				LocalDateTime.now().plusDays(2), 10f, new Theater("a Theater",
						Set.of(1, 2, 3, 4, 5, 6), DateTimeProvider.create()),
				pointsToWin);
	}

	Movie createSmallFishMovie() {
		return new Movie("Small Fish", "plot x", 102,
				LocalDate.of(2023, 10, 10) /* release data */,
				Set.of(Genre.COMEDY, Genre.ACTION)/* genre */,
				List.of(new Actor(
						new Person("aName", "aSurname", "anEmail@mail.com"),
						"George Bix")),
				List.of(new Person("aDirectorName", "aDirectorSurname",
						"anotherEmail@mail.com")),
				(user, movie) -> {
					return false;
				});
	}

	EmailProvider doNothingEmailProvider() {
		return (to, subject, body) -> {
		};
	}

	CreditCardPaymentGateway doNothingPaymentProvider() {
		return (creditCardNumber, expire, securityCode, totalAmount) -> {
		};
	}

	Movie createSmallFishMovieWithRates() {
		return new Movie("Small Fish", "plot ...", 102,
				LocalDate.of(2023, 10, 10) /* release data */,
				Set.of(Genre.COMEDY, Genre.ACTION)/* genre */,
				List.of(new Actor(
						new Person("aName", "aSurname", "anEmail@mail.com"),
						"George Bix")),
				List.of(new Person("aDirectorName", "aDirectorSurname",
						"anotherEmail@mail.com")),
				InMemoryUsersRating.carlosRatedSmallFish());
	}

	ShowTime createShowForSmallFish(DateTimeProvider provider) {
		return new ShowTime(DateTimeProvider.create(), createSmallFishMovie(),
				LocalDateTime.now().plusDays(1), 10f,
				new Theater("a Theater", Set.of(1, 2, 3, 4, 5, 6), provider));
	}

	ShowTime createShowForSmallFish() {
		return createShowForSmallFish(DateTimeProvider.create());
	}

	User createUserCharly() {
		return new User(new Person("Carlos", "Edgun", "cedgun@mysite.com"),
				"cedgun", "afbcdefghigg");
	}

	User createUserJoseph() {
		return new User(new Person("Joseph", "Valdun", "jvaldun@wabla.com"),
				"jvaldun", "tabcd1234igg");
	}

	User createUserNicolas() {
		return new User(
				new Person("Nicolas", "Molinari", "nmolinari@yesmy.com"),
				"nmolinari", "oneplayminebrawl");
	}
}

class EmailProviderFake implements EmailProvider {

	private String to;
	private String subject;
	private String body;

	@Override
	public void send(String to, String subject, String body) {
		this.to = to;
		this.subject = subject;
		this.body = body;
	}

	public boolean hasBeanCalledWith(String to, String subject, String body) {
		return this.to.equals(to) && this.subject.equals(subject)
				&& this.body.equals(body);
	}
}

class PaymenentProviderFake implements CreditCardPaymentGateway {
	private String creditCardNumber;
	private YearMonth expire;
	private String securityCode;
	private float totalAmount;

	@Override
	public void pay(String creditCardNumber, YearMonth expire,
			String securityCode, float totalAmount) {
		this.creditCardNumber = creditCardNumber;
		this.expire = expire;
		this.securityCode = securityCode;
		this.totalAmount = totalAmount;
	}

	public boolean hasBeanCalledWith(String creditCardNumber, YearMonth expire,
			String securityCode, float totalAmount) {
		return this.creditCardNumber.equals(creditCardNumber)
				&& this.expire.equals(expire)
				&& this.securityCode.equals(securityCode)
				&& this.totalAmount == totalAmount;
	}
}