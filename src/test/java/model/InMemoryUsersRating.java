package model;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUsersRating implements UsersRating {

	private Map<String, String> usersRating = new HashMap<>();

	static UsersRating carlosRatedSmallFish() {
		var users = new InMemoryUsersRating();
		users.usersRating.put("cedgun", "Small Fish");
		return users;
	}

	static UsersRating newUsersRating() {
		return new InMemoryUsersRating();
	}

	@Override
	public boolean hasAlreadyRate(User user, Movie movie) {
		var movieName = this.usersRating.get(user.userName());
		if (movieName == null) {
			return false;
		}
		return movie.isNamedAs(movie);
	}
}
