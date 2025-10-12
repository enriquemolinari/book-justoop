package app;

import app.api.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CashierTest {
    private final ForTests tests = new ForTests();
    private ShowTime aShow;
    private User carlos;
    private Set<Integer> seatsForCarlos;

    @BeforeEach
    public void before() {
        aShow = tests.createShowForSmallFish();
        carlos = tests.createUserNicolas();
        seatsForCarlos = Set.of(1, 2);
    }

    @Test
    public void payOk() {
        reserveSeatsForCarlos(seatsForCarlos);
        var paymentProvider = tests.fakePaymenentProvider();
        var cashier = new Cashier(paymentProvider);
        YearMonth expirationDate = getExpirationDate();
        var ticket = cashier.paySeatsFor(seatsForCarlos, aShow, carlos,
                CreditCard.of("789456",
                        expirationDate,
                        "123456"));
        assertEquals(20f, ticket.total());
        assertEquals("Small Fish", ticket.getMovieName());
        assertEquals(10, ticket.getPointsWon());
        assertTrue(ticket.hasSeats(seatsForCarlos));
        assertTrue(paymentProvider.hasBeanCalledWith("789456",
                expirationDate,
                "123456",
                20f));
    }

    @Test
    public void payWithoutSeatsCannotBeCompleted() {
        Exception e = assertThrows(BusinessException.class, () -> {
            var ticket = new Cashier(tests.fakePaymenentProvider())
                    .paySeatsFor(Set.of(), aShow, carlos,
                            CreditCard.of("789456",
                                    getExpirationDate(),
                                    "123456"));
        });
        assertEquals(e.getMessage(), Cashier.SELECTED_SEATS_SHOULD_NOT_BE_EMPTY);
    }

    private void reserveSeatsForCarlos(Set<Integer> seatsForCarlos) {
        aShow.reserveSeatsFor(carlos, seatsForCarlos, LocalDateTime.now().plusMinutes(15));
    }

    private static YearMonth getExpirationDate() {
        return YearMonth.of(LocalDateTime.now().plusMonths(1).getYear(),
                LocalDateTime.now().plusMonths(1).getMonth().getValue());
    }

    @Test
    public void paymentProviderRejectingCreditCard() {
        reserveSeatsForCarlos(seatsForCarlos);
        var paymentProvider = tests.fakePaymenentProviderThrowE();
        var cashier = new Cashier(paymentProvider);
        YearMonth expirationDate = getExpirationDate();
        Exception e = assertThrows(BusinessException.class, () -> {
            var ticket = cashier.paySeatsFor(seatsForCarlos, aShow, carlos,
                    CreditCard.of("789456",
                            expirationDate,
                            "123456"));
        });
        assertEquals(e.getMessage(), Cashier.CREDIT_CARD_DEBIT_HAS_FAILED);
        assertTrue(aShow.noneOfTheSeatsAreConfirmedBy(carlos, seatsForCarlos));
    }

    @Test
    public void paymentSucceedsButConfirmSeatsAndRegisterSaleFails() {
        reserveSeatsForCarlos(seatsForCarlos);
        var paymentProvider = tests.fakePaymenentProvider();
        var cashier = new Cashier(paymentProvider);
        YearMonth expirationDate = getExpirationDate();

        // A ShowTime wrapper that delegates to aShow but fails on confirmSeatsForUser
        ShowTime showTimeThatFailsOnConfirm = new ShowTime() {
            @Override
            void checkAllSeatsAreReservedBy(User user, Set<Integer> selectedSeats) {
                aShow.checkAllSeatsAreReservedBy(user, selectedSeats);
            }

            @Override
            float totalAmountForTheseSeats(Set<Integer> selectedSeats) {
                return aShow.totalAmountForTheseSeats(selectedSeats);
            }

            @Override
            Set<ShowSeat> confirmSeatsForUser(User user, Set<Integer> selectedSeats) {
                throw new RuntimeException("Any fake exception to simulate failure");
            }

            @Override
            int pointsToEarn() {
                return aShow.pointsToEarn();
            }
        };

        Exception e = assertThrows(BusinessException.class, () ->
                cashier.paySeatsFor(seatsForCarlos, showTimeThatFailsOnConfirm, carlos,
                        CreditCard.of("789456", expirationDate, "123456"))
        );

        assertEquals(Cashier.CANNOT_COMPLETE_THE_SALE, e.getMessage());
        assertTrue(paymentProvider.hasBeanCalledWith("789456",
                expirationDate,
                "123456",
                20f));
        assertTrue(aShow.noneOfTheSeatsAreConfirmedBy(carlos, seatsForCarlos));
    }
}