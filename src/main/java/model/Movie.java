package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.api.ActorInMovieName;
import model.api.DetailedMovieInfo;
import model.api.Genre;
import model.api.MovieInfo;
import model.api.MovieShows;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
public class Movie {

	static final String MOVIE_PLOT_INVALID = "Movie plot must not be null or blank";
	static final String MOVIE_NAME_INVALID = "Movie name must not be null or blank";
	static final String DURATION_INVALID = "Movie's duration must be greater than 0";
	static final String GENRES_INVALID = "You must add at least one genre to the movie";
	static final String USER_HAS_ALREADY_RATE = "The user has already rate the movie";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String name;
	private int duration;
	private LocalDate releaseDate;
	private String plot;

	@ElementCollection(targetClass = Genre.class)
	@CollectionTable
	@Enumerated(EnumType.STRING)
	private Set<Genre> genres;
	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "id_movie")
	private List<Actor> actors;
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<Person> directors;
	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "movie")
	// List does not load the entire collection for adding new elements
	// if there is a bidirectional mapping
	private List<UserRate> userRates;

	// this is pre-calculated rating for this movie
	@Embedded
	private Rating rating;

	// TODO: mover esto a Cinema. Dejar comentario que lo muevo por cuestiones
	// de performance.
	@Transient
	private UsersRating usersRating = UsersRating.defaultProvider();
	@OneToMany(mappedBy = "movieToBeScreened")
	private List<ShowTime> showTimes;

	public Movie(String name, String plot, int duration, LocalDate releaseDate,
			Set<Genre> genres, List<Actor> actors, List<Person> directors,
			UsersRating usersRating) {
		checkDurationGreaterThanZero(duration);
		checkGenresAtLeastHasOne(genres);
		this.name = new NotBlankString(name, MOVIE_NAME_INVALID).value();
		this.plot = new NotBlankString(name, MOVIE_PLOT_INVALID).value();
		this.duration = duration;
		this.releaseDate = releaseDate;
		this.genres = genres;
		this.actors = actors;
		this.directors = directors;
		this.userRates = new ArrayList<>();
		this.rating = Rating.notRatedYet();
		this.usersRating = usersRating;
	}

	public Movie(String name, String plot, int duration, LocalDate releaseDate,
			Set<Genre> genres) {
		this(name, plot, duration, releaseDate, genres, new ArrayList<Actor>(),
				new ArrayList<Person>(), UsersRating.defaultProvider());
	}

	private <T> void checkCollectionSize(Set<T> collection, String errorMsg) {
		if (collection.size() == 0) {
			throw new BusinessException(errorMsg);
		}
	}

	private void checkGenresAtLeastHasOne(Set<Genre> genres) {
		checkCollectionSize(genres, GENRES_INVALID);
	}

	private void checkDurationGreaterThanZero(int duration) {
		if (duration <= 0) {
			throw new BusinessException(DURATION_INVALID);
		}
	}

	public boolean hasDurationOf(int aDuration) {
		return this.duration == aDuration;
	}

	public boolean isNamed(String aName) {
		return this.name.equals(aName);
	}

	public boolean isNamedAs(Movie aMovie) {
		return this.name.equals(aMovie.name);
	}

	public boolean hasReleaseDateOf(LocalDate aDate) {
		return releaseDate.equals(aDate);
	}

	public boolean hasGenresOf(List<Genre> genddres) {
		return this.genres.stream().allMatch(g -> genddres.contains(g));
	}

	public boolean hasARole(String anActorName) {
		return this.actors.stream().anyMatch(a -> a.isNamed(anActorName));
	}

	public boolean isCharacterNamed(String aCharacterName) {
		return this.actors.stream()
				.anyMatch(a -> a.hasCharacterName(aCharacterName));
	}

	public boolean isDirectedBy(String aDirectorName) {
		return this.directors.stream().anyMatch(d -> d.isNamed(aDirectorName));
	}

	public void rateBy(User user, int value, String comment) {
		// Cannot validate this using userRates (oneToMany association)
		// because Hibernate will load the entire collection in memory. That
		// would hurt performance as the collection gets bigger.
		if (this.usersRating.hasAlreadyRate(user, this)) {
			throw new BusinessException(USER_HAS_ALREADY_RATE);
		}

		this.rating.calculaNewRate(value);
		this.userRates.add(new UserRate(user, value, comment, this));
	}

	boolean hasRateValue(float aValue) {
		return this.rating.hasValue(aValue);
	}

	public boolean hasTotalVotes(int votes) {
		return this.rating.hastTotalVotesOf(votes);
	}

	String name() {
		return this.name;
	}

	public MovieShows toMovieShow() {
		return new MovieShows(this.id, this.name, this.duration,
				genreAsListOfString(), this.showTimes.stream()
						.map(show -> show.toShowInfo()).toList());
	}

	public DetailedMovieInfo toDetailedInfo() {
		return new DetailedMovieInfo(toInfo(), this.userRates.stream()
				.map(ur -> ur.toUserMovieRate()).toList());
	}

	public void addAnActor(String name, String surname, String email,
			String characterName) {
		this.actors.add(
				new Actor(new Person(name, surname, email), characterName));
	}

	public void addADirector(String name, String surname, String email) {
		this.directors.add(new Person(name, surname, email));
	}

	public MovieInfo toInfo() {
		// TODO: format releaseDate and duration
		return new MovieInfo(id, name, String.valueOf(duration), plot,
				genreAsListOfString(), directorsNamesAsString(),
				releaseDate.toString(), rating.actualRate(),
				toActorsInMovieNames());
	}

	private List<String> directorsNamesAsString() {
		return directors.stream().map(d -> d.fullName()).toList();
	}

	private List<ActorInMovieName> toActorsInMovieNames() {
		return this.actors.stream()
				.map(actor -> new ActorInMovieName(actor.fullName(),
						actor.characterName()))
				.toList();
	}

	private Set<String> genreAsListOfString() {
		return Stream.of(Genre.values()).map(Genre::name)
				.map(g -> g.toLowerCase()).collect(Collectors.toSet());
	}

}
