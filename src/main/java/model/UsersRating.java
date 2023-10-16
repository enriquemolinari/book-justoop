package model;

@FunctionalInterface
public interface UsersRating {
	boolean hasAlreadyRate(User user, Movie movie);

	static UsersRating defaultProvider() {
		return (user, movie) -> {
			return false;
		};
	}
}
