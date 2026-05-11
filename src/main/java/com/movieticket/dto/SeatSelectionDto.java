package com.movieticket.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SeatSelectionDto {
    private Long screeningId;
    private Long bookingId;
    private List<SeatDto> selectedSeats;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SeatDto {
        private int row;
        private int number;
    }
}
