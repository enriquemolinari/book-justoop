package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class ShowTimeTest {

	private final ObjectsForTests tests = new ObjectsForTests();

	@Test
	public void showTimeStartTimeMustBeInTheFuture() {
		Exception e = assertThrows(BusinessException.class, () -> {
			new ShowTime(DateTimeProvider.create(),
					tests.createSmallFishMovie(),
					LocalDateTime.of(2023, 03, 10, 15, 0, 0, 0), 10f,
					new Theater("A Theater", Set.of(1)));
		});

		assertEquals(e.getMessage(), ShowTime.START_TIME_MUST_BE_IN_THE_FUTURE);
	}

	@Test
	public void showTimePriceMustNotBeFree() {
		Exception e = assertThrows(BusinessException.class, () -> {
			new ShowTime(DateTimeProvider.create(),
					tests.createSmallFishMovie(),
					LocalDateTime.now().plusDays(1), 0f,
					new Theater("A Theater", Set.of(1)));
		});

		assertEquals(e.getMessage(), ShowTime.PRICE_MUST_BE_POSITIVE);
	}

	@Test
	public void createShowTime() {
		var aShow = createShowForSmallFish();

		assertTrue(aShow.hasSeatNumbered(1));
		assertTrue(aShow.hasSeatNumbered(2));
		assertFalse(aShow.hasSeatNumbered(8));
		assertTrue(aShow.startAt(LocalDateTime.of(2023, 10, 10, 15, 0, 0, 0)));
	}

	@Test
	public void reserveAnAvailableSeat() {
		var aShow = createShowForSmallFish();
		var carlos = createCarlosUser();

		var seatsToReserve = Set.of(1, 2);
		aShow.reserveSeatsFor(carlos, seatsToReserve);

		assertTrue(aShow.areAllSeatsReservedBy(carlos, seatsToReserve));
	}

	@Test
	public void reserveAlreadyReservedSeats() {
		var aShow = createShowForSmallFish();
		var carlos = createCarlosUser();
		var jose = createJoseUser();

		var seatsToReserveByCarlos = Set.of(1, 2);
		aShow.reserveSeatsFor(carlos, seatsToReserveByCarlos);

		var seatsToReserveByJose = Set.of(2, 3);
		Exception e = assertThrows(BusinessException.class, () -> {
			aShow.reserveSeatsFor(carlos, seatsToReserveByJose);
		});

		assertEquals(e.getMessage(), ShowTime.SEATS_CHOSEN_ARE_BUSY);
		assertTrue(aShow.areAllSeatsReservedBy(carlos, seatsToReserveByCarlos));
		assertTrue(
				aShow.noneOfTheSeatsAreReservedBy(jose, seatsToReserveByJose));
	}

	private User createCarlosUser() {
		return new User(new Person("Carlos", "Garzia"), "cgarzia");
	}

	private User createJoseUser() {
		return new User(new Person("Jose", "Lopiz"), "jlopiz");
	}

	private ShowTime createShowForSmallFish() {
		return new ShowTime(DateTimeProvider.create(),
				tests.createSmallFishMovie(),
				LocalDateTime.of(2023, 10, 10, 15, 0, 0, 0), 10f,
				new Theater("a Theater", Set.of(1, 2, 3, 4, 5, 6)));
	}

}
