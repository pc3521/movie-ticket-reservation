package com.movieticket.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screenings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "screen_time", nullable = false)
    private LocalDateTime screenTime;

    @Column(name = "hall_name", length = 50)
    private String hallName;

    @Column(name = "total_rows")
    @Builder.Default
    private Integer totalRows = 8;

    @Column(name = "seats_per_row")
    @Builder.Default
    private Integer seatsPerRow = 10;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    public int getTotalSeats() {
        return totalRows * seatsPerRow;
    }
}
