package model.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

//TODO: implementar la verificacion de estas authenticado aqui adentro!
// sino, si lo hago a nivel Web, y quiero mover a Javalin u otro, duplico esta logica.
public interface CinemaSystem {

	List<MovieShows> showsUntil(LocalDateTime untilTo);

	List<MovieInfo> moviesSortedByName(int pageNumber);

	MovieInfo movie(Long id);

	MovieInfo addNewMovie(String name, int duration,
			LocalDate releaseDate, String plot, Set<Genre> genres);

	MovieInfo addActorToMovie(Long movieId, String name, String surname,
			String email, String characterName);

	MovieInfo addDirectorToMovie(Long movieId, String name,
			String surname, String email);

	Long addNewTheater(String name, Set<Integer> seatsNumbers);

	ShowInfo addNewShowToMovie(Long movieId, LocalDateTime startTime,
			float price, Long theaterId, int pointsToWin);

	DetailedShowInfo reserve(Long userId, Long showTimeId,
			Set<Integer> selectedSeats);

	Ticket pay(Long userId, Long showTimeId, Set<Integer> selectedSeats,
			String creditCardNumber, YearMonth expirationDate,
			String secturityCode);

	UserMovieRate rateMovieBy(Long userId, Long idMovie, int rateValue,
			String comment);

	List<UserMovieRate> pagedRatesOfOrderedDate(Long movieId, int pageNumber);

	List<MovieInfo> pagedMoviesOrderedByRate(int pageNumber);

	// search Movie by name

	// showById

	// UserRecord login(String username, String password);

	// (private en Cinema) Long userIdFrom(String token) throws
	// UnauthorizedException;

	// Profile profileFromUser(userId)

	Long registerUser(String name, String surname, String email,
			String userName,
			String password, String repeatPassword);
}
