package model;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"name"})
public
class Theater {

	static final String NAME_INVALID = "Theater name cannot be blank";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(unique = true)
	private String name;
	@Transient
	// This allows testing
	private DateTimeProvider provider = DateTimeProvider.create();

	// @OneToMany(cascade = CascadeType.PERSIST)
	// @JoinColumn(name = "theater_id")
	// private Set<Seat> seats;
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<Integer> seatNumbers;

	public Theater(String name, Set<Integer> seats, DateTimeProvider provider) {
		this.name = new NotBlankString(name, NAME_INVALID).value();
		this.seatNumbers = seats;
		this.provider = provider;
	}

	Set<ShowSeat> seatsForShow(ShowTime show) {
		return this.seatNumbers.stream()
				.map(s -> new ShowSeat(show, s, this.provider))
				.collect(Collectors.toUnmodifiableSet());
	}
}
