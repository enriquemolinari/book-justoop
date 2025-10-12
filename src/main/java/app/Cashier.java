package app;

import app.api.BusinessException;
import app.api.CreditCardPaymentProvider;
import app.api.Ticket;

import java.util.Set;

public class Cashier {
    static final String SELECTED_SEATS_SHOULD_NOT_BE_EMPTY = "Selected Seats should not be empty";
    public static final String CANNOT_COMPLETE_THE_SALE = "Cannot complete the sale, refund is being processed";
    private final CreditCardPaymentProvider paymentGateway;
    static final String CREDIT_CARD_DEBIT_HAS_FAILED = "Credit card debit have failed";

    public Cashier(CreditCardPaymentProvider paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public Ticket paySeatsFor(Set<Integer> selectedSeats,
                              ShowTime showTime,
                              User user,
                              CreditCard creditCard) {
        checkSeatsAreNotEmpty(selectedSeats);
        showTime.checkAllSeatsAreReservedBy(user, selectedSeats);
        var total = showTime.totalAmountForTheseSeats(selectedSeats);
        doPayment(creditCard, total);
        return confirmSeatsAndRegisterSale(selectedSeats, showTime, user, creditCard, total);
    }

    private Ticket confirmSeatsAndRegisterSale(Set<Integer> selectedSeats, ShowTime showTime, User user, CreditCard creditCard, float total) {
        try {
            var showSeats = showTime.confirmSeatsForUser(user, selectedSeats);
            return Sale.registerNewSaleFor(user, total, showTime.pointsToEarn(), showSeats);
        } catch (Exception e) {
            refundPayment(user, creditCard, total);
            throw new BusinessException(CANNOT_COMPLETE_THE_SALE, e);
        }
    }

    private void refundPayment(User user, CreditCard creditCard, float total) {
        //do log/execute logic for compensation
    }

    private void doPayment(CreditCard creditCard, float total) {
        try {
            // Here we have a service operation executed outside a Tx boundary.
            this.paymentGateway.pay(creditCard.number(), creditCard.expiration(),
                    creditCard.secturityCode(), total);
        } catch (Exception e) {
            throw new BusinessException(CREDIT_CARD_DEBIT_HAS_FAILED, e);
        }
    }

    private void checkSeatsAreNotEmpty(Set<Integer> selectedSeats) {
        if (selectedSeats.isEmpty()) {
            throw new BusinessException(SELECTED_SEATS_SHOULD_NOT_BE_EMPTY);
        }
    }
}
