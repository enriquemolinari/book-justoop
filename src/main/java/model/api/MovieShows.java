package model.api;

import java.util.List;
import java.util.Set;

public record MovieShows(Long movieId, String movieName, int duration,
		Set<String> genres, List<ShowInfo> shows) {
}
