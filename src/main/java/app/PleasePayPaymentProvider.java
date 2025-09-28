package app;

import app.api.CreditCardPaymentProvider;

import java.time.YearMonth;

public class PleasePayPaymentProvider implements CreditCardPaymentProvider {

    @Override
    public void pay(String creditCardNumber, YearMonth expire,
                    String securityCode, float totalAmount) {
        // always succeed
    }

}
