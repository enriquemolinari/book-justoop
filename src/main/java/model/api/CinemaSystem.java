package model.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

//TODO: implementar la verificacion de estas authenticado aqui adentro!
// sino, si lo hago a nivel Web, y quiero mover a Javalin u otro, duplico esta logica.
public interface CinemaSystem {

	public List<MovieShows> showsUntil(LocalDateTime untilTo);

	public List<MovieInfo> moviesSortedByName();

	DetailedMovieInfo movie(Long id);

	public MovieInfo addNewMovie(String name, int duration,
			LocalDate releaseDate, String plot, Set<Genre> genres);

	public MovieInfo addActorToMovie(Long movieId, String name, String surname,
			String email, String characterName);

	public MovieInfo addDirectorToMovie(Long movieId, String name,
			String surname, String email);

	public ShowInfo addNewShowToMovie(Long movieId, LocalDateTime startTime,
			float price, Long theaterId, int pointsToWin);

	// showById

	// pay
	// TODO: replace with authentication token
	public Ticket pay(Long userId, Long showTimeId, Set<Integer> selectedSeats,
			String creditCardNumber, YearMonth expirationDate,
			String secturityCode);

	// reserve

	// void rateMovie(Long userId, Long idMovie, int rateValu, String comment);

	// public RatingRecord rating(Long idMovie);

	// UserRecord login(String username, String password);

	// Long userIdFrom(String token) throws UnauthorizedException;

	// Profile profileFromUser(userId)

	// register User

	// create Movie

	// addShowToMovie

}
