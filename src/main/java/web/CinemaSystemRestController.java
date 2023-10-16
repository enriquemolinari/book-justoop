package web;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import model.api.CinemaSystem;
import model.api.DetailedMovieInfo;
import model.api.MovieInfo;
import model.api.MovieShows;

@RestController
public class CinemaSystemRestController {

	CinemaSystem cinema;

	public CinemaSystemRestController(CinemaSystem cinema) {
		this.cinema = cinema;
	}

	@GetMapping("/movies/{id}")
	public DetailedMovieInfo movies(@PathVariable Long id) {
		return cinema.movie(id);
	}

	@GetMapping("/movies")
	public List<MovieInfo> allMovies() {
		return cinema.moviesSortedByName();
	}

	@GetMapping("/shows")
	public List<MovieShows> all() {
		return cinema.showsUntil(LocalDateTime.now().plusDays(10));
	}
	// @GetMapping("/bla")
	// public Set<SeatResponse> bla() {
	// return cinema.seatsAvailableFor(1L);
	// }
	//
	// @GetMapping("/user/{id}")
	// public Map<String, String> userById(@PathVariable Long id) {
	// return cinema.byId(id);
	// }

}
