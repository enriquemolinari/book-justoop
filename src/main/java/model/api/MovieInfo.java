package model.api;

import java.util.List;

public record MovieInfo(Long id, String name, String duration, String plot,
		Iterable<String> genres, Iterable<String> directorNames,
		String releaseDate, float ratingValue, List<ActorInMovieName> actors) {
}
