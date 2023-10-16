package model.api;

import java.util.List;

public record DetailedMovieInfo(MovieInfo movieInfo,
		List<UserMovieRate> usersRate) {

}
