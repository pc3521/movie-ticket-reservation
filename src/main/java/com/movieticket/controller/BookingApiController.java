package com.movieticket.controller;

import com.movieticket.dto.SeatSelectionDto;
import com.movieticket.model.Booking;
import com.movieticket.model.User;
import com.movieticket.service.BookingService;
import com.movieticket.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api")
public class BookingApiController {

    private final BookingService bookingService;
    private final UserService userService;

    public BookingApiController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping("/seats/{screeningId}")
    public ResponseEntity<Map<String, Object>> getSeats(@PathVariable Long screeningId) {
        Map<String, Object> response = new HashMap<>();
        response.put("booked", bookingService.getBookedSeats(screeningId));
        response.put("locked", bookingService.getLockedSeats(screeningId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/seats/lock")
    public ResponseEntity<Map<String, Object>> lockSeat(@RequestBody Map<String, Object> request, Principal principal) {
        Long screeningId = Long.valueOf(request.get("screeningId").toString());
        int row = Integer.parseInt(request.get("row").toString());
        int number = Integer.parseInt(request.get("number").toString());

        boolean locked = bookingService.lockSeat(screeningId, row, number, principal.getName());
        Map<String, Object> response = new HashMap<>();
        response.put("success", locked);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/seats/unlock")
    public ResponseEntity<Map<String, Object>> unlockSeat(@RequestBody Map<String, Object> request) {
        Long screeningId = Long.valueOf(request.get("screeningId").toString());
        int row = Integer.parseInt(request.get("row").toString());
        int number = Integer.parseInt(request.get("number").toString());

        bookingService.unlockSeat(screeningId, row, number);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bookings/create")
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody SeatSelectionDto seatSelection,
                                                              Principal principal) {
        try {
            User user = userService.findByUsername(principal.getName());
            Booking booking = bookingService.createBooking(user, seatSelection.getScreeningId(), seatSelection);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("bookingRef", booking.getBookingRef());
            response.put("bookingId", booking.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/bookings/cancel/{bookingId}")
    public ResponseEntity<Map<String, Object>> cancelBooking(@PathVariable Long bookingId, Principal principal) {
        try {
            bookingService.cancelBooking(bookingId, principal.getName());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
