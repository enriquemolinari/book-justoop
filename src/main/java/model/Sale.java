package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.api.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private float total;
    private LocalDateTime salesDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User purchaser;

    private int pointsWon;

    @ManyToOne
    @JoinColumn(name = "id_showtime")
    private ShowTime soldShow;

    private Set<Integer> selectedSeats;

    public Sale(float totalAmount, User userThatPurchased, ShowTime soldShow,
                int pointsWon, Set<Integer> selectedSeats) {
        this.total = totalAmount;
        this.purchaser = userThatPurchased;
        this.soldShow = soldShow;
        this.selectedSeats = selectedSeats;
        this.salesDate = LocalDateTime.now();
        this.pointsWon = pointsWon;
        userThatPurchased.newPurchase(this, pointsWon);
    }

    private String formattedSalesDate() {
        return new FormattedDateTime(salesDate).toString();
    }

    List<Integer> confirmedSeatNumbers() {
        return this.selectedSeats.stream().toList();
    }

    public Ticket ticket() {
        return new Ticket(total, pointsWon, formattedSalesDate(),
                purchaser.userName(), confirmedSeatNumbers(),
                soldShow.movieName(), soldShow.startDateTime());
    }
}
