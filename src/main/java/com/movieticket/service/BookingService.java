package com.movieticket.service;

import com.movieticket.dto.SeatSelectionDto;
import com.movieticket.model.*;
import com.movieticket.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final ScreeningRepository screeningRepository;

    // In-memory seat locks: screeningId -> Set of "row-number" strings
    private final Map<Long, Set<String>> seatLocks = new ConcurrentHashMap<>();

    public BookingService(BookingRepository bookingRepository,
                          BookedSeatRepository bookedSeatRepository,
                          ScreeningRepository screeningRepository) {
        this.bookingRepository = bookingRepository;
        this.bookedSeatRepository = bookedSeatRepository;
        this.screeningRepository = screeningRepository;
    }

    public List<Map<String, Object>> getBookedSeats(Long screeningId) {
        List<BookedSeat> seats = bookedSeatRepository.findConfirmedByScreeningId(screeningId);
        return seats.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("row", s.getSeatRow());
            map.put("number", s.getSeatNumber());
            map.put("label", s.getSeatLabel());
            return map;
        }).collect(Collectors.toList());
    }

    public Set<String> getLockedSeats(Long screeningId) {
        return seatLocks.getOrDefault(screeningId, Collections.emptySet());
    }

    public boolean lockSeat(Long screeningId, int row, int number, String username) {
        String key = row + "-" + number;
        Set<String> locks = seatLocks.computeIfAbsent(screeningId, k -> ConcurrentHashMap.newKeySet());

        if (bookedSeatRepository.isSeatBooked(screeningId, row, number)) {
            return false;
        }
        return locks.add(key);
    }

    public void unlockSeat(Long screeningId, int row, int number) {
        String key = row + "-" + number;
        Set<String> locks = seatLocks.get(screeningId);
        if (locks != null) {
            locks.remove(key);
        }
    }

    @Transactional
    public Booking createBooking(User user, Long screeningId, SeatSelectionDto seatSelection) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        // Validate seats are available
        for (SeatSelectionDto.SeatDto seat : seatSelection.getSelectedSeats()) {
            if (bookedSeatRepository.isSeatBooked(screeningId, seat.getRow(), seat.getNumber())) {
                throw new RuntimeException("Seat " + (char)('A' + seat.getRow() - 1) + seat.getNumber() + " is already booked");
            }
        }

        BigDecimal totalAmount = screening.getPrice()
                .multiply(BigDecimal.valueOf(seatSelection.getSelectedSeats().size()));

        Booking booking = Booking.builder()
                .bookingRef(generateBookingRef())
                .user(user)
                .screening(screening)
                .totalAmount(totalAmount)
                .bookingStatus(Booking.BookingStatus.CONFIRMED)
                .paymentStatus(Booking.PaymentStatus.PAID)
                .build();

        booking = bookingRepository.save(booking);

        for (SeatSelectionDto.SeatDto seat : seatSelection.getSelectedSeats()) {
            BookedSeat bookedSeat = BookedSeat.builder()
                    .booking(booking)
                    .screening(screening)
                    .seatRow(seat.getRow())
                    .seatNumber(seat.getNumber())
                    .build();
            bookedSeatRepository.save(bookedSeat);
            booking.getBookedSeats().add(bookedSeat);

            // Release lock
            unlockSeat(screeningId, seat.getRow(), seat.getNumber());
        }

        return booking;
    }

    @Transactional
    public void cancelBooking(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
        booking.setPaymentStatus(Booking.PaymentStatus.REFUNDED);
        bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdWithDetails(userId);
    }

    public List<Booking> getScreeningBookings(Long screeningId) {
        return bookingRepository.findByScreeningIdWithDetails(screeningId);
    }

    public int getAvailableSeats(Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId).orElse(null);
        if (screening == null) return 0;
        long booked = bookedSeatRepository.findConfirmedByScreeningId(screeningId).size();
        return screening.getTotalSeats() - (int) booked;
    }

    private String generateBookingRef() {
        return "BK" + System.currentTimeMillis() % 100000000;
    }
}
