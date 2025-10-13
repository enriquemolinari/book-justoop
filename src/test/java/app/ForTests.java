package app;

import app.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ForTests {

    static final String SUPER_MOVIE_PLOT = "a super movie that shows the life of ...";
    static final String SUPER_MOVIE_NAME = "a super movie";
    static final String OTHER_SUPER_MOVIE_NAME = "another super movie";
    static final String SUPER_MOVIE_DIRECTOR_NAME = "aDirectorName surname";
    static final ActorCharacter SUPER_MOVIE_ACTOR_CARLOS = new ActorCharacter(
            "Carlos Kalchi",
            "aCharacterName");

    EmailProviderFake fakeEmailProvider() {
        return new EmailProviderFake();
    }

    PaymenentProviderFake fakePaymenentProvider() {
        return new PaymenentProviderFake();
    }

    PaymenentProviderThrowException fakePaymenentProviderThrowE() {
        return new PaymenentProviderThrowException();
    }

    Movie createSmallFishMovie() {
        return createSmallFishMovie(LocalDate.of(2023, 10, 10));
    }

    Movie createSmallFishMovie(LocalDate releaseDate) {
        return new Movie("Small Fish", "plot x", 102,
                releaseDate,
                Set.of(Genre.COMEDY, Genre.ACTION)/* genre */,
                List.of(new Actor(
                        new Person("aName", "aSurname", "anEmail@mail.com"),
                        "George Bix")),
                List.of(new Person("aDirectorName", "aDirectorSurname",
                        "anotherEmail@mail.com")));
    }

    EmailProvider doNothingEmailProvider() {
        return (to, subject, body) -> {
        };
    }

    CreditCardPaymentProvider doNothingPaymentProvider() {
        return (creditCardNumber, expire, securityCode, totalAmount) -> {
        };
    }

    Token doNothingToken() {
        return new Token() {
            @Override
            public Long verifyAndGetUserIdFrom(String token) {
                return 0L;
            }

            @Override
            public String tokenFrom(Map<String, Object> payload) {
                return "aToken";
            }
        };
    }

    ShowTime createShowForSmallFish() {
        return ShowTime.scheduleFor(createSmallFishMovie())
                .at(LocalDateTime.now().plusDays(1))
                .pricedAt(10f)
                .in(new Theater("a Theater", Set.of(1, 2, 3, 4, 5, 6)))
                .build();
    }

    User createUserCharly() {
        return new User(new Person("Carlos", "Edgun", "cedgun@mysite.com"),
                "cedgun", "afbcdefghigg", "afbcdefghigg");
    }

    User createUserJoseph() {
        return new User(new Person("Joseph", "Valdun", "jvaldun@wabla.com"),
                "jvaldun", "tabcd1234igg", "tabcd1234igg");
    }

    User createUserNicolas() {
        return new User(
                new Person("Nicolas", "Molinari", "nmolinari@yesmy.com"),
                "nmolinari", "oneplayminebrawl", "oneplayminebrawl");
    }

    MovieInfo createSuperMovie(Cinema cinema) {
        var movieInfo = cinema.addNewMovie(SUPER_MOVIE_NAME, 109,
                LocalDate.of(2023, 4, 5),
                SUPER_MOVIE_PLOT,
                Set.of(Genre.ACTION, Genre.ADVENTURE));

        cinema.addActorTo(movieInfo.id(), "Carlos", "Kalchi",
                "carlosk@bla.com", "aCharacterName");

        cinema.addActorTo(movieInfo.id(), "Jose", "Hermes",
                "jose@bla.com", "anotherCharacterName");

        cinema.addDirectorToMovie(movieInfo.id(), "aDirectorName", "surname",
                "adir@bla.com");

        return movieInfo;
    }

    MovieInfo createOtherSuperMovie(Cinema cinema) {
        var movieInfo = cinema.addNewMovie(OTHER_SUPER_MOVIE_NAME, 80,
                LocalDate.of(2022, 4, 5),
                "other super movie ...",
                Set.of(Genre.COMEDY, Genre.FANTASY));

        cinema.addActorTo(movieInfo.id(), "Nico", "Cochix",
                "nico@bla.com", "super Character Name");

        cinema.addDirectorToMovie(movieInfo.id(), "aSuper DirectorName",
                "sur name",
                "asuper@bla.com");

        return movieInfo;
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

class PaymenentProviderFake implements CreditCardPaymentProvider {
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

class PaymenentProviderThrowException implements CreditCardPaymentProvider {
    @Override
    public void pay(String creditCardNumber, YearMonth expire,
                    String securityCode, float totalAmount) {
        throw new RuntimeException("very bad...");
    }
}