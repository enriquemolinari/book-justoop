package app;

import app.api.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SaleTest {
    private final ForTests tests = new ForTests();

    @Test
    public void saleCannotBeCreatedWithEmptySeats() {
        Exception e = assertThrows(BusinessException.class, () -> {
            var sale = Sale.registerNewSaleFor(tests.createUserNicolas(),
                    100f,
                    10,
                    Set.of());
        });
        assertEquals(Sale.SALE_CANNOT_BE_CREATED_WITHOUT_SEATS, e.getMessage());
    }
}
