package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.api.BusinessException;
import model.api.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
class Sale {

    public static final String SALE_CANNOT_BE_CREATED_WITHOUT_SEATS = "Sale cannot be created without seats";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private float total;
    private LocalDateTime salesDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User purchaser;

    private int pointsWon;

    @OneToMany
    @JoinColumn(name = "id_sale")
    private Set<ShowSeat> seatsSold;

    private Sale(float totalAmount,
                 User userThatPurchased,
                 Set<ShowSeat> seatsSold,
                 int pointsWon) {
        this.total = totalAmount;
        this.purchaser = userThatPurchased;
        this.seatsSold = seatsSold;
        this.salesDate = LocalDateTime.now();
        this.pointsWon = pointsWon;
        userThatPurchased.newPurchase(this, pointsWon);
    }

    public static Ticket registerNewSaleFor(User userThatPurchased,
                                            float totalAmount,
                                            int pointsWon,
                                            Set<ShowSeat> seatsSold) {
        checkSeatsNotEmpty(seatsSold);
        return new Sale(totalAmount, userThatPurchased, seatsSold,
                pointsWon).ticket();
    }

    private static void checkSeatsNotEmpty(Set<ShowSeat> seatsSold) {
        if (seatsSold.isEmpty()) {
            throw new BusinessException(SALE_CANNOT_BE_CREATED_WITHOUT_SEATS);
        }
    }

    private String formattedSalesDate() {
        return new FormattedDateTime(salesDate).toString();
    }

    List<Integer> confirmedSeatNumbers() {
        return this.seatsSold.stream().map(seat -> seat.seatNumber()).toList();
    }

    private Ticket ticket() {
        ShowSeat first = this.seatsSold.stream().findFirst().get();
        String movieName = first.showMovieName();
        String startTime = first.showStartTime();
        return new Ticket(total, pointsWon, formattedSalesDate(),
                purchaser.userName(), confirmedSeatNumbers(),
                movieName, startTime);
    }
}
