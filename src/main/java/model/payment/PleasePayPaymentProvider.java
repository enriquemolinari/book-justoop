package model.payment;

import java.time.YearMonth;

import model.api.CreditCardPaymentGateway;

public class PleasePayPaymentProvider implements CreditCardPaymentGateway {

	@Override
	public void pay(String creditCardNumber, YearMonth expire,
			String securityCode, float totalAmount) {
		// always succeed
	}

}
