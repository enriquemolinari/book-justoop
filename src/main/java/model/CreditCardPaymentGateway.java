package model;

import java.time.YearMonth;

public interface CreditCardPaymentGateway {

	void pay(String creditCardNumber, YearMonth expire, String securityCode,
			float totalAmount);

	static CreditCardPaymentGateway defaultGateway() {
		return (creditCardNumber, expire, securityCode, totalAmount) -> {
		};
	}
}
