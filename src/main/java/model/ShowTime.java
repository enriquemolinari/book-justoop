package model;

import static model.CreditCardPaymentGateway.defaultGateway;
import static model.EmailProvider.defaultProvider;

import java.time.LocalDateTime;
import java.time.YearMonth;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
class ShowTime {

	static final String START_TIME_MUST_BE_IN_THE_FUTURE = "The show start time must be in the future";
	static final String PRICE_MUST_BE_POSITIVE = "The price must be greater than zero";
	static final String SELECTED_SEATS_ARE_BUSY = "All or some of the seats chosen are busy";
	static final String RESERVATION_IS_REQUIRED_TO_CONFIRM = "Reservation is required before confirm";
	private static final int DEFAULT_TOTAL_POINTS_FOR_A_PURCHASE = 10;
	static final String EMAIL_SUBJECT_SALE = "You have new tickets!";
	// TODO: add email template
	static final String EMAIL_BODY_SALE = "Body...";

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
	private float price;
	@OneToOne(fetch = FetchType.LAZY)
	private Theater screenedIn;
	@OneToMany(mappedBy = "show", cascade = CascadeType.PERSIST)
	private Set<ShowSeat> seatsForThisShow;

	@Transient
	// When ShowTime is retrieved from DB by Hibernate, this collaborator is
	// null if I don't set it here
	private CreditCardPaymentGateway payment = defaultGateway();

	@Transient
	// When ShowTime is retrieved from DB by Hibernate, this collaborator is
	// null if I don't set it here
	private EmailProvider emailProvider = defaultProvider();

	private int pointsThatAUserWinForPurchaseThisShowTime = DEFAULT_TOTAL_POINTS_FOR_A_PURCHASE;

	public ShowTime(DateTimeProvider provider, Movie movie,
			LocalDateTime startTime, float price, Theater screenedIn,
			CreditCardPaymentGateway payment, EmailProvider emailProvider) {
		this.timeProvider = provider;
		this.movieToBeScreened = movie;
		checkStartTimeIsInTheFuture(startTime);
		checkPriceIsPositiveAndNotFree(price);

		this.price = price;
		this.startTime = startTime;
		this.screenedIn = screenedIn;
		this.seatsForThisShow = screenedIn.seatsForShow(this);
		this.payment = payment;
		this.emailProvider = emailProvider;
	}

	public ShowTime(DateTimeProvider provider, Movie movie,
			LocalDateTime startTime, float price, Theater screenedIn,
			CreditCardPaymentGateway payment, EmailProvider emailProvider,
			int totalPointsToWin) {
		this(provider, movie, startTime, price, screenedIn, payment,
				emailProvider);
		this.pointsThatAUserWinForPurchaseThisShowTime = totalPointsToWin;
	}

	public boolean isStartingAt(LocalDateTime of) {
		return this.startTime.equals(startTime);
	}

	private Set<ShowSeat> filterSelectedSeats(Set<Integer> selectedSeats) {
		return this.seatsForThisShow.stream()
				.filter(seat -> seat.isIncludedIn(selectedSeats))
				.collect(Collectors.toUnmodifiableSet());
	}

	public void reserveSeatsFor(User user, Set<Integer> selectedSeats) {
		var selection = filterSelectedSeats(selectedSeats);
		checkAllSelectedSeatsAreAvailable(selection);
		reserveAllSeatsFor(user, selection);
	}

	public Sale confirmSeatsFor(User user, Set<Integer> selectedSeats,
			String creditCardNumber, YearMonth expirationDate,
			String secturityCode) {
		var selection = filterSelectedSeats(selectedSeats);
		checkAllSelectedSeatsAreReservedBy(user, selection);
		confirmAllSeatsFor(user, selection);
		// TODO assert YearMonth is in the future

		// total amount
		var totalAmount = Math.round(selectedSeats.size() * this.price * 100.0f)
				/ 100.0f;

		// do payment
		this.payment.pay(creditCardNumber, expirationDate, secturityCode,
				totalAmount);

		var sale = new Sale(totalAmount, user, this,
				pointsThatAUserWinForPurchaseThisShowTime);

		// send notifications
		this.emailProvider.send(user.email(), EMAIL_SUBJECT_SALE,
				EMAIL_BODY_SALE);

		return sale;
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

}
