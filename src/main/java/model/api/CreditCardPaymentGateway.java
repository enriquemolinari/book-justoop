package model.api;

import java.time.YearMonth;

public interface CreditCardPaymentGateway {
	void pay(String creditCardNumber, YearMonth expire, String securityCode,
			float totalAmount);
}
