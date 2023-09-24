package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
class Movie {

	static final String MOVIE_NAME_INVALID = "Movie name must not be null or blank";
	static final String DURATION_INVALID = "Movie's duration must be greater than 0";
	static final String GENRES_INVALID = "You must add at least one genre to the movie";
	static final String ACTORS_INVALID = "The movie must have at least one actor";
	static final String DIRECTORS_INVALID = "The movie must have at least one director";
	static final String USER_HAS_ALREADY_RATE = "The user has already rate the movie";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String name;
	private int duration;
	private LocalDate releaseDate;

	@ElementCollection(targetClass = Genre.class)
	@CollectionTable
	@Enumerated(EnumType.STRING)
	private Set<Genre> genres;
	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "movie_id")
	private Set<Actor> actors;
	@OneToMany
	private Set<Person> directorNames;
	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "movie")
	// List does not load the entire collection for adding new elements
	// if there is a bidirectional mapping
	private List<UserRate> userRates;

	// this is pre-calculated rating for this movie
	@Embedded
	private Rating rating;
	@Transient
	private UsersRating usersRating;

	public Movie(String name, int duration, LocalDate releaseDate,
			Set<Genre> genres, Set<Actor> actors, Set<Person> directors,
			UsersRating usersRating) {
		checkDurationGreaterThanZero(duration);
		checkGenresAtLeastHasOne(genres);
		checkActorsAtLeastHasOne(actors);
		checkDirectorAtLeastHasOne(directors);

		this.name = new NotBlankString(name, MOVIE_NAME_INVALID).value();
		this.duration = duration;
		this.releaseDate = releaseDate;
		this.genres = genres;
		this.actors = actors;
		this.directorNames = directors;
		this.userRates = new ArrayList<>();
		this.rating = Rating.notRatedYet();
		this.usersRating = usersRating;
	}

	private void checkActorsAtLeastHasOne(Set<Actor> actors) {
		checkCollectionSize(actors, ACTORS_INVALID);
	}

	private void checkDirectorAtLeastHasOne(Set<Person> directors) {
		checkCollectionSize(directors, DIRECTORS_INVALID);
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
		return this.directorNames.stream()
				.anyMatch(d -> d.isNamed(aDirectorName));
	}

	public void rateBy(User user, int value, String comment) {
		// Cannot validate this using userRates (oneToMany association)
		// because Hibernate will load the entire collection in memory. Than
		// would not perform if my site is successful
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

}
