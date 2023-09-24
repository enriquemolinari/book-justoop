package model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	@ManyToOne
	@JoinColumn(name = "id_user")
	private User purchaser;

	@ManyToOne
	@JoinColumn(name = "id_showtime")
	private ShowTime soldShow;

	public Sale(float totalAmount, User userThatPurchased, ShowTime soldShow) {
		this.total = totalAmount;
		this.purchaser = userThatPurchased;
		this.soldShow = soldShow;
		this.salesDate = LocalDateTime.now();
	}

	public boolean hasTotalOf(float aTotal) {
		return this.total == aTotal;
	}

	public String salesNumber() {
		return String.format("%i-%i", this.id, this.salesDate.getYear());
	}

}
