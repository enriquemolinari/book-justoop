package model;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
@NoArgsConstructor
class ShowTime {

	static final String START_TIME_MUST_BE_IN_THE_FUTURE = "The show start time must be in the future";
	static final String PRICE_MUST_BE_POSITIVE = "The price must be greater than zero";
	static final String SEATS_CHOSEN_ARE_BUSY = "All or some of the seats chosen are busy";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private LocalDateTime startTime;

	@Transient
	// When hibernate creates an instance of this class, this will be
	// null if I don't initialize here.
	private DateTimeProvider timeProvider = DateTimeProvider.create();

	@OneToOne(fetch = FetchType.LAZY)
	private Movie movieToBeScreened;
	private float showPrice;
	@OneToOne(fetch = FetchType.LAZY)
	private Theater screenedIn;
	@OneToMany(mappedBy = "show", cascade = CascadeType.PERSIST)
	private Set<ShowSeat> seatsForThisShow;

	public ShowTime(DateTimeProvider provider, Movie movie,
			LocalDateTime startTime, float price, Theater screenedIn) {
		this.timeProvider = provider;
		this.movieToBeScreened = movie;
		checkStartTimeIsInTheFuture(startTime);
		checkPriceIsPositiveAndNotFree(price);

		this.showPrice = price;
		this.startTime = startTime;
		this.screenedIn = screenedIn;
		this.seatsForThisShow = screenedIn.seatsForShow(this);
	}

	public boolean startAt(LocalDateTime of) {
		return this.startTime.equals(startTime);
	}

	private Set<ShowSeat> filterSelectedSeats(Set<Integer> selectedSeats) {
		return this.seatsForThisShow.stream().filter(s -> s.isIn(selectedSeats))
				.collect(Collectors.toUnmodifiableSet());
	}

	public void reserveSeatsFor(User user, Set<Integer> selectedSeats) {
		var selection = filterSelectedSeats(selectedSeats);
		checkAllSelectedSeatsAreAvailable(selection);
		reserveAllSeatsBy(user, selection);
	}

	private void reserveAllSeatsBy(User user, Set<ShowSeat> selection) {
		selection.stream().forEach(s -> s.reserveFor(user));
	}

	private void checkAllSelectedSeatsAreAvailable(Set<ShowSeat> selection) {
		if (selection.stream().anyMatch(s -> s.isBusy())) {
			throw new BusinessException(SEATS_CHOSEN_ARE_BUSY);
		}
	}

	public void confirmSeatsFor(User user, Set<ShowSeat> chosenSeats) {
		throw new RuntimeException("not implemented yet...");
	}

	// public Set<ShowSeat> availableSeats() {
	// // return the showtime's seats busy and available
	// return null;
	// }

	private void checkPriceIsPositiveAndNotFree(float price) {
		if (price <= 0) {
			throw new BusinessException(PRICE_MUST_BE_POSITIVE);
		}
	}

	private void checkStartTimeIsInTheFuture(LocalDateTime startTime) {
		if (startTime.isBefore(this.timeProvider.now())) {
			throw new BusinessException(START_TIME_MUST_BE_IN_THE_FUTURE);
		}
	}

	public boolean hasSeatNumbered(int aSeatNumber) {
		return this.seatsForThisShow.stream()
				.anyMatch(s -> s.isSeatNumbered(aSeatNumber));
	}

	boolean noneOfTheSeatsAreReservedBy(User aUser, Set<Integer> seatsToReserve) {
		return allReservedBy(aUser, seatsToReserve,
				s -> !s.isReservedBy(aUser));
	}

	boolean areAllSeatsReservedBy(User aUser, Set<Integer> seatsToReserve) {
		return allReservedBy(aUser, seatsToReserve, s -> s.isReservedBy(aUser));
	}

	boolean allReservedBy(User aUser, Set<Integer> seatsToReserve,
			Predicate<ShowSeat> predicate) {
		var selectedSeats = filterSelectedSeats(seatsToReserve);
		return selectedSeats.stream().allMatch(predicate);

	}
}
