package model;

import model.api.BusinessException;
import org.jetbrains.annotations.NotNull;
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
        var cashier = new Cachier(paymentProvider);
        YearMonth expirationDate = getExpirationDate();
        var ticket = cashier.paySeatsFor(seatsForCarlos, aShow, carlos,
                Creditcard.of("789456",
                        expirationDate,
                        "123456"));
        assertEquals(20f, ticket.total());
        assertEquals(10, ticket.getPointsWon());
        assertTrue(ticket.hasSeats(seatsForCarlos));
        assertTrue(paymentProvider.hasBeanCalledWith("789456",
                expirationDate,
                "123456",
                20f));
    }

    private void reserveSeatsForCarlos(Set<Integer> seatsForCarlos) {
        aShow.reserveSeatsFor(carlos, seatsForCarlos, LocalDateTime.now().plusMinutes(15));
    }

    @NotNull
    private static YearMonth getExpirationDate() {
        return YearMonth.of(LocalDateTime.now().plusMonths(1).getYear(),
                LocalDateTime.now().plusMonths(1).getMonth().getValue());
    }

    @Test
    public void paymentProviderRejectingCreditCard() {
        reserveSeatsForCarlos(seatsForCarlos);
        var paymentProvider = tests.fakePaymenentProviderThrowE();
        var cashier = new Cachier(paymentProvider);
        YearMonth expirationDate = getExpirationDate();
        Exception e = assertThrows(BusinessException.class, () -> {
            var ticket = cashier.paySeatsFor(seatsForCarlos, aShow, carlos,
                    Creditcard.of("789456",
                            expirationDate,
                            "123456"));
        });
        assertEquals(e.getMessage(), Cachier.CREDIT_CARD_DEBIT_HAS_FAILED);
        assertTrue(aShow.noneOfTheSeatsAreConfirmedBy(carlos, seatsForCarlos));
    }
}