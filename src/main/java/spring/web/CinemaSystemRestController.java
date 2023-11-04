package spring.web;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import model.api.AuthException;
import model.api.CinemaSystem;
import model.api.DetailedShowInfo;
import model.api.MovieInfo;
import model.api.MovieShows;
import model.api.UserMovieRate;
import model.api.UserProfile;

@CrossOrigin(originPatterns = "http://localhost*")
@RestController
public class CinemaSystemRestController {

	private static final String AUTHENTICATION_REQUIRED = "You must be logged in to perform this action...";
	private static final String TOKEN_COOKIE_NAME = "token";
	CinemaSystem cinema;

	public CinemaSystemRestController(CinemaSystem cinema) {
		this.cinema = cinema;
	}

	@GetMapping("/movies/{id}")
	public ResponseEntity<MovieInfo> movies(@PathVariable Long id) {
		return ResponseEntity.ok(cinema.movie(id));
	}

	@GetMapping("/movies")
	public ResponseEntity<List<MovieInfo>> allMovies(
			@RequestParam int page) {
		return ResponseEntity.ok(cinema.pagedMoviesSortedByName(page));
	}

	@GetMapping("/shows")
	public List<MovieShows> playingTheseDays() {
		return cinema.showsUntil(LocalDateTime.now().plusDays(10));
	}

	@GetMapping("/shows/{id}")
	public ResponseEntity<DetailedShowInfo> showDetail(
			@PathVariable Long id) {
		return ResponseEntity.ok(cinema.show(id));
	}

	@GetMapping("/movies/{id}/rate")
	public ResponseEntity<List<UserMovieRate>> pagedRatesOfOrderedDate(
			@PathVariable Long id,
			@RequestParam("page") int page) {
		return ResponseEntity.ok(cinema.pagedRatesOfOrderedDate(id, page));
	}

	@PostMapping("/login")
	public ResponseEntity<UserProfile> login(@RequestBody LoginForm form) {
		String token = cinema.login(form.username(), form.password());
		var profile = cinema.profileFrom(cinema.userIdFrom(token));

		var cookie = ResponseCookie.from(TOKEN_COOKIE_NAME, token)
				.httpOnly(true).build();
		var headers = new HttpHeaders();
		headers.add(HttpHeaders.COOKIE, cookie.toString());
		return ResponseEntity.ok().headers(headers).body(profile);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(
			@CookieValue(required = false) String token) {
		return ifAuthenticatedDo(token, (userId) -> {
			var cookie = ResponseCookie.from(TOKEN_COOKIE_NAME, null)
					.httpOnly(true).maxAge(0).build();
			var headers = new HttpHeaders();
			headers.add(HttpHeaders.COOKIE, cookie.toString());
			return ResponseEntity.ok().headers(headers).build();
		});

	}

	private <S> S ifAuthenticatedDo(String token, Function<Long, S> method) {
		var userId = Optional.ofNullable(token).map(a -> {
			var uid = this.cinema.userIdFrom(token);
			return uid;
		}).orElseThrow(() -> new AuthException(
				AUTHENTICATION_REQUIRED));

		return method.apply(userId);
	}

	@PostMapping("/movies/{id}/rates")
	public ResponseEntity<UserMovieRate> rateMovie(
			@CookieValue(required = false) String token,
			@PathVariable Long id) {

		var bla = ifAuthenticatedDo(token, userId -> {
			return this.cinema.rateMovieBy(userId, id, 10, "");
		});

		return ResponseEntity.ok(bla);
	}
}
