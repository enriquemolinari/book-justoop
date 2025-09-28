package app;

import app.api.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShowSeatTest {
    private final ForTests tests = new ForTests();

    @Test
    void cannotReserveAlreadyReservedSeat() {
        ShowTime show = tests.createShowForSmallFish();
        User userNico = tests.createUserNicolas();
        User userCharly = tests.createUserCharly();
        ShowSeat seat = new ShowSeat(show, 1);
        LocalDateTime hasta = LocalDateTime.now().plusMinutes(10);

        seat.doReserveForUser(userCharly, hasta);

        var e = assertThrows(BusinessException.class, () -> {
            seat.doReserveForUser(userNico, hasta);
        });
        assertEquals(ShowSeat.SEAT_BUSY, e.getMessage());
    }

    @Test
    void cannotConfirmAlreadyConfirmedSeat() {
        ShowTime show = tests.createShowForSmallFish();
        User userNico = tests.createUserNicolas();
        User userCharly = tests.createUserCharly();
        ShowSeat seat = new ShowSeat(show, 2);
        LocalDateTime hasta = LocalDateTime.now().plusMinutes(10);

        seat.doReserveForUser(userCharly, hasta);
        seat.doConfirmForUser(userCharly);

        var e = assertThrows(BusinessException.class, () -> {
            seat.doConfirmForUser(userNico);
        });
        assertEquals(ShowSeat.SEAT_NOT_RESERVED_OR_ALREADY_CONFIRMED, e.getMessage());

    }
}
