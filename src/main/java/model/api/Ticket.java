package model.api;

import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
//TODO: getters para pasar a json
//TODO: en dos sessiones diferentes compro otros asientos y 
//me duevuelve todos los que compre antes y ahora
@Getter(value = AccessLevel.PUBLIC)
public class Ticket {
	private float total;
	private int pointsWon;
	private String formattedSalesDate;
	private String userName;
	private List<Integer> payedSeats;

	public Ticket(float total, int pointsWon,
			String formattedSalesDate, String userName,
			List<Integer> payedSeats) {
		this.total = total;
		this.pointsWon = pointsWon;
		this.formattedSalesDate = formattedSalesDate;
		this.userName = userName;
		this.payedSeats = payedSeats;
	}

	public boolean hasSeats(Set<Integer> seats) {
		return seats.containsAll(seats);
	}

	public boolean isPurchaserUserName(String aUserName) {
		return this.userName.equals(aUserName);
	}

	public float total() {
		return this.total;
	}
}
