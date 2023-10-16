package model.api;

public class Ticket {
	private String salesNumber;
	private float total;
	private int pointsWon;
	private String formattedSalesDate;
	private String userName;

	public Ticket(String salesNumber, float total, int pointsWon,
			String formattedSalesDate, String userName) {
		this.salesNumber = salesNumber;
		this.total = total;
		this.pointsWon = pointsWon;
		this.formattedSalesDate = formattedSalesDate;
		this.userName = userName;
	}

}
