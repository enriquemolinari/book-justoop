package model;

import java.util.Set;

public class Cinema {

	public Set<SeatResponse> seatsAvailableFor(Long showId) {

		return null;
	}
}

class SeatResponse {
	private final int seatNumber;
	private final boolean available;

	public SeatResponse(int seatNumber, boolean available) {
		this.seatNumber = seatNumber;
		this.available = available;
	}
}
