package model.api;

import java.util.List;

public record DetailedShowInfo(ShowInfo info, List<Seat> currentSeats) {

}
