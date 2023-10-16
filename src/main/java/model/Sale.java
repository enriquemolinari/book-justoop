package model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.api.Ticket;

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

	public Sale(float totalAmount, User userThatPurchased, ShowTime soldShow,
			int pointsWon) {
		this.total = totalAmount;
		this.purchaser = userThatPurchased;
		this.soldShow = soldShow;
		this.salesDate = LocalDateTime.now();
		this.pointsWon = pointsWon;
		userThatPurchased.newPurchase(this, pointsWon);
	}

	public boolean hasTotalOf(float aTotal) {
		return this.total == aTotal;
	}

	private String salesNumber() {
		return String.format("%i-%i", this.id, this.salesDate.getYear());
	}

	private String formattedSalesDate() {
		return new FormattedDateTime(salesDate).toString();
	}

	boolean purchaseBy(User aUser) {
		return this.purchaser.equals(aUser);
	}

	public Ticket ticket() {
		return new Ticket(salesNumber(), total, pointsWon, formattedSalesDate(),
				purchaser.userName());
	}
}
