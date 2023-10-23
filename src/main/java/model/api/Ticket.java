package model.api;

import java.util.List;
import java.util.Set;

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
