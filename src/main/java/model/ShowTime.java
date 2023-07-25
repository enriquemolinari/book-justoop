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
	static final String SELECTED_SEATS_ARE_BUSY = "All or some of the seats chosen are busy";
	static final String RESERVATION_IS_REQUIRED_TO_CONFIRM = "Reservation is required before confirm";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private LocalDateTime startTime;

	@Transient
	// When hibernate creates an instance of this class, this will be
	// null if I don't initialize it here.
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
		return this.seatsForThisShow.stream()
				.filter(seat -> seat.isIn(selectedSeats))
				.collect(Collectors.toUnmodifiableSet());
	}

	public void reserveSeatsFor(User user, Set<Integer> selectedSeats) {
		var selection = filterSelectedSeats(selectedSeats);
		checkAllSelectedSeatsAreAvailable(selection);
		reserveAllSeatsFor(user, selection);
	}

	private void reserveAllSeatsFor(User user, Set<ShowSeat> selection) {
		selection.stream().forEach(seat -> seat.reserveFor(user));
	}

	private void confirmAllSeatsFor(User user, Set<ShowSeat> selection) {
		selection.stream().forEach(seat -> seat.confirmFor(user));
	}

	private void checkAllSelectedSeatsAreAvailable(Set<ShowSeat> selection) {
		checkAtLeastOneMatchConditionFor(selection, seat -> seat.isBusy(),
				SELECTED_SEATS_ARE_BUSY);
	}

	private void checkAllSelectedSeatsAreReservedBy(User user,
			Set<ShowSeat> selection) {
		checkAtLeastOneMatchConditionFor(selection,
				seat -> !seat.isReservedBy(user),
				RESERVATION_IS_REQUIRED_TO_CONFIRM);
	}

	public void confirmSeatsFor(User user, Set<Integer> selectedSeats) {
		var selection = filterSelectedSeats(selectedSeats);
		checkAllSelectedSeatsAreReservedBy(user, selection);
		confirmAllSeatsFor(user, selection);
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
				.anyMatch(seat -> seat.isSeatNumbered(aSeatNumber));
	}

	boolean noneOfTheSeatsAreReservedBy(User aUser,
			Set<Integer> seatsToReserve) {
		return !areAllSeatsReservedBy(aUser, seatsToReserve);
	}

	public boolean noneOfTheSeatsAreConfirmedBy(User carlos,
			Set<Integer> seatsToConfirmByCarlos) {
		return !areAllSeatsConfirmedBy(carlos, seatsToConfirmByCarlos);
	}

	boolean areAllSeatsConfirmedBy(User aUser, Set<Integer> seatsToReserve) {
		var selectedSeats = filterSelectedSeats(seatsToReserve);
		return allMatchConditionFor(selectedSeats,
				seat -> seat.isConfirmedBy(aUser));
	}

	boolean areAllSeatsReservedBy(User aUser, Set<Integer> seatsToReserve) {
		var selectedSeats = filterSelectedSeats(seatsToReserve);
		return allMatchConditionFor(selectedSeats,
				seat -> seat.isReservedBy(aUser));
	}

	private void checkAtLeastOneMatchConditionFor(Set<ShowSeat> seatsToReserve,
			Predicate<ShowSeat> condition, String errorMsg) {
		if (seatsToReserve.stream().anyMatch(condition)) {
			throw new BusinessException(errorMsg);
		}
	}

	private boolean allMatchConditionFor(Set<ShowSeat> seatsToReserve,
			Predicate<ShowSeat> condition) {
		return seatsToReserve.stream().allMatch(condition);
	}

}
