package model.api;

import java.time.LocalDateTime;

public record ShowInfo(Long idShow, LocalDateTime playingTime,
		String theatreName, float price) {

}
