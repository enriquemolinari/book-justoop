package model;

import model.api.BusinessException;
import model.api.CreditCardPaymentProvider;
import model.api.Ticket;

import java.util.Set;

public class Cashier {
    private final CreditCardPaymentProvider paymentGateway;
    static final String CREDIT_CARD_DEBIT_HAS_FAILED = "Credit card debit have failed";

    public Cashier(CreditCardPaymentProvider paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public Ticket paySeatsFor(Set<Integer> selectedSeats,
                              ShowTime showTime,
                              User user,
                              CreditCard creditCard) {
        var total = showTime.totalAmountForTheseSeats(selectedSeats);
        try {
            // In this scenario, we have a service operation executed outside a Tx boundary.
            this.paymentGateway.pay(creditCard.number(), creditCard.expiration(),
                    creditCard.secturityCode(), total);
        } catch (Exception e) {
            throw new BusinessException(CREDIT_CARD_DEBIT_HAS_FAILED, e);
        }
        // If an exception occurs from now on, the transaction is rolled back.
        // It's imperative to ensure that the user is refunded promptly.
        // To handle this gracefully we should set up a compensation mechanism
        // not covered in this book
        var showSeats = showTime.confirmSeatsForUser(user, selectedSeats);
        return Sale.registerNewSaleFor(user, total, showTime.pointsToEarn(), showSeats);
    }
}
