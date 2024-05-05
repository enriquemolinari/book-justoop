package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.api.BusinessException;
import model.api.Seat;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PRIVATE)
class ShowSeat {

    static final String SEAT_BUSY = "Seat is currently busy";
    static final String SEAT_NOT_RESERVED_OR_ALREADY_CONFIRMED = "The seat cannot be confirmed";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private boolean reserved;
    private boolean confirmed;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_show")
    private ShowTime show;
    private LocalDateTime reservedUntil;
    private Integer seatNumber;
    @Version
    private int version;

    public ShowSeat(ShowTime s, Integer seatNumber) {
        this.show = s;
        this.seatNumber = seatNumber;
        this.reserved = false;
        this.confirmed = false;
    }

    public void doReserveForUser(User user, LocalDateTime until) {
        if (!isAvailable()) {
            throw new BusinessException(SEAT_BUSY);
        }
        this.reserved = true;
        this.user = user;
        this.reservedUntil = until;
    }

    public boolean isBusy() {
        return !isAvailable();
    }

    public boolean isAvailable() {
        return (!reserved || LocalDateTime.now().isAfter(this.reservedUntil)) && !confirmed;
    }

    public void doConfirmForUser(User user) {
        if (!isReservedBy(user) || confirmed) {
            throw new BusinessException(SEAT_NOT_RESERVED_OR_ALREADY_CONFIRMED);
        }
        this.confirmed = true;
        this.user = user;
    }

    boolean isConfirmedBy(User user) {
        if (this.user == null) {
            return false;
        }
        return confirmed && this.user.equals(user);
    }

    boolean isReservedBy(User user) {
        if (this.user == null) {
            return false;
        }
        return reserved && this.user.equals(user)
                && LocalDateTime.now().isBefore(this.reservedUntil);
    }

    public boolean isSeatNumbered(int aSeatNumber) {
        return this.seatNumber.equals(aSeatNumber);
    }

    public boolean isIncludedIn(Set<Integer> selectedSeats) {
        return selectedSeats.stream()
                .anyMatch(ss -> ss.equals(this.seatNumber));
    }

    int seatNumber() {
        return seatNumber;
    }

    public Seat toSeat() {
        return new Seat(seatNumber, isAvailable());
    }

    public String showMovieName() {
        return this.show.movieName();
    }

    public String showStartTime() {
        return this.show.startDateTime();
    }
}
