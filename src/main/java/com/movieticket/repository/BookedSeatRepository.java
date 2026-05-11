package com.movieticket.repository;

import com.movieticket.model.BookedSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BookedSeatRepository extends JpaRepository<BookedSeat, Long> {

    @Query("SELECT bs FROM BookedSeat bs WHERE bs.screening.id = :screeningId AND bs.booking.bookingStatus = 'CONFIRMED'")
    List<BookedSeat> findConfirmedByScreeningId(Long screeningId);

    @Query("SELECT CASE WHEN COUNT(bs) > 0 THEN true ELSE false END FROM BookedSeat bs WHERE bs.screening.id = :screeningId AND bs.seatRow = :row AND bs.seatNumber = :number AND bs.booking.bookingStatus = 'CONFIRMED'")
    boolean isSeatBooked(Long screeningId, Integer row, Integer number);
}
