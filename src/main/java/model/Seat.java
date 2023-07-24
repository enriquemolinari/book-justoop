package model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

//@Entity
//@Table(uniqueConstraints = {
//		@UniqueConstraint(columnNames = {"theater", "seatNumber"})})
//@NoArgsConstructor
//@Setter(value = AccessLevel.PRIVATE)
//@Getter(value = AccessLevel.PRIVATE)
//@EqualsAndHashCode(of = {"theater", "seatNumber"})
class Seat {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private int seatNumber;
	@OneToOne
	private Theater theater;

	public Seat(Theater theater, int seatNumber) {
		this.theater = theater;
		// TODO: validate
		this.seatNumber = seatNumber;
	}

	public boolean isNumbered(int aSeatNumber) {
		return seatNumber == aSeatNumber;
	}

}
