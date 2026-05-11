package com.movieticket.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booked_seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"screening_id", "seat_row", "seat_number"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookedSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Column(name = "seat_row", nullable = false)
    private Integer seatRow;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    public String getSeatLabel() {
        char rowLetter = (char) ('A' + seatRow - 1);
        return rowLetter + String.valueOf(seatNumber);
    }
}
