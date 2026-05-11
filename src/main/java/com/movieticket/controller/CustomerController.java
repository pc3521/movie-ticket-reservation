package com.movieticket.controller;

import com.movieticket.model.*;
import com.movieticket.repository.ScreeningRepository;
import com.movieticket.service.BookingService;
import com.movieticket.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final ScreeningRepository screeningRepository;
    private final BookingService bookingService;
    private final UserService userService;

    public CustomerController(ScreeningRepository screeningRepository,
                              BookingService bookingService,
                              UserService userService) {
        this.screeningRepository = screeningRepository;
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @RequestParam(required = false) String search) {
        var screenings = (search != null && !search.isBlank())
                ? screeningRepository.searchUpcoming(search, LocalDateTime.now())
                : screeningRepository.findUpcomingScreenings(LocalDateTime.now());

        screenings.forEach(s ->
            model.addAttribute("availableSeats_" + s.getId(), bookingService.getAvailableSeats(s.getId()))
        );

        model.addAttribute("screenings", screenings);
        model.addAttribute("search", search);
        return "customer/dashboard";
    }

    @GetMapping("/book/{screeningId}")
    public String bookingForm(@PathVariable Long screeningId, @RequestParam int quantity, Model model) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Screening not found"));
        model.addAttribute("screening", screening);
        model.addAttribute("quantity", quantity);
        model.addAttribute("totalPrice", screening.getPrice().multiply(java.math.BigDecimal.valueOf(quantity)));
        return "customer/booking-form";
    }

    @GetMapping("/select-seats/{screeningId}")
    public String seatSelection(@PathVariable Long screeningId, @RequestParam int quantity, Model model) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Screening not found"));
        model.addAttribute("screening", screening);
        model.addAttribute("quantity", quantity);
        return "customer/seat-selection";
    }

    @GetMapping("/bookings")
    public String myBookings(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("bookings", bookingService.getUserBookings(user.getId()));
        return "customer/my-bookings";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "customer/profile";
    }

    @GetMapping("/booking-confirmation/{bookingId}")
    public String bookingConfirmation(@PathVariable Long bookingId, Model model) {
        var booking = bookingService.getScreeningBookings(bookingId); // reuse
        // Actually get by ID
        model.addAttribute("bookingId", bookingId);
        return "customer/booking-confirmation";
    }
}
