package com.movieticket.controller;

import com.movieticket.repository.ScreeningRepository;
import com.movieticket.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class HomeController {

    private final ScreeningRepository screeningRepository;
    private final BookingService bookingService;

    public HomeController(ScreeningRepository screeningRepository, BookingService bookingService) {
        this.screeningRepository = screeningRepository;
        this.bookingService = bookingService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model, @RequestParam(required = false) String search) {
        var screenings = (search != null && !search.isBlank())
                ? screeningRepository.searchUpcoming(search, LocalDateTime.now())
                : screeningRepository.findUpcomingScreenings(LocalDateTime.now());

        // Add available seat count
        screenings.forEach(s ->
            model.addAttribute("availableSeats_" + s.getId(), bookingService.getAvailableSeats(s.getId()))
        );

        model.addAttribute("screenings", screenings);
        model.addAttribute("search", search);
        return "home";
    }
}
