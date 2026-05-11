package com.movieticket.repository;

import com.movieticket.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.screening s JOIN FETCH s.movie WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
    List<Booking> findByUserIdWithDetails(Long userId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.screening s JOIN FETCH s.movie JOIN FETCH b.bookedSeats WHERE b.screening.id = :screeningId")
    List<Booking> findByScreeningIdWithDetails(Long screeningId);

    Optional<Booking> findByBookingRef(String bookingRef);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.screening.id = :screeningId AND b.bookingStatus = 'CONFIRMED'")
    long countConfirmedByScreeningId(Long screeningId);
}
