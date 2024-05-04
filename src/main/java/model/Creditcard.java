package model;

import java.time.YearMonth;

public class Creditcard {
    private final String creditCardNumber;
    private final YearMonth expirationDate;
    private final String secturityCode;

    private Creditcard(String creditCardNumber, YearMonth expirationDate, String secturityCode) {
        this.creditCardNumber = creditCardNumber;
        this.expirationDate = expirationDate;
        this.secturityCode = secturityCode;
    }

    public static Creditcard of(String creditCardNumber, YearMonth expirationDate, String secturityCode) {
        return new Creditcard(creditCardNumber, expirationDate, secturityCode);
    }

    public String number() {
        return this.creditCardNumber;
    }

    public YearMonth expiration() {
        return this.expirationDate;
    }

    public String secturityCode() {
        return this.secturityCode;
    }
}
