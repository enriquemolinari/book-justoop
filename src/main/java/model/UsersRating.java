package model;

@FunctionalInterface
public interface UsersRating {
	boolean hasAlreadyRate(User user, Movie movie);
}
