package com.movieticket.controller;

import com.movieticket.dto.ScreeningDto;
import com.movieticket.model.*;
import com.movieticket.repository.*;
import com.movieticket.service.BookingService;
import com.movieticket.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final BookingService bookingService;
    private final UserService userService;

    public AdminController(ScreeningRepository screeningRepository,
                           MovieRepository movieRepository,
                           BookingService bookingService,
                           UserService userService) {
        this.screeningRepository = screeningRepository;
        this.movieRepository = movieRepository;
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("screenings", screeningRepository.findAllWithMovie());
        return "admin/dashboard";
    }

    @GetMapping("/add-screening")
    public String addScreeningForm(Model model) {
        model.addAttribute("screeningDto", new ScreeningDto());
        model.addAttribute("movies", movieRepository.findAll());
        return "admin/add-screening";
    }

    @PostMapping("/add-screening")
    public String addScreening(@Valid @ModelAttribute ScreeningDto dto,
                               BindingResult result, Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("movies", movieRepository.findAll());
            return "admin/add-screening";
        }

        try {
            Movie movie;
            if (dto.getMovieId() != null) {
                movie = movieRepository.findById(dto.getMovieId())
                        .orElseThrow(() -> new RuntimeException("Movie not found"));
            } else {
                movie = Movie.builder()
                        .title(dto.getMovieTitle())
                        .description(dto.getMovieDescription())
                        .genre(dto.getMovieGenre())
                        .durationMinutes(dto.getMovieDuration())
                        .rating(dto.getMovieRating())
                        .posterUrl(dto.getMoviePosterUrl() != null ? dto.getMoviePosterUrl()
                                : "https://placehold.co/300x450/333/fff?text=" + dto.getMovieTitle().replace(" ", "+"))
                        .build();
                movie = movieRepository.save(movie);
            }

            Screening screening = Screening.builder()
                    .movie(movie)
                    .screenTime(LocalDateTime.parse(dto.getScreenTime()))
                    .hallName(dto.getHallName() != null ? dto.getHallName() : "Hall A")
                    .totalRows(dto.getTotalRows() != null ? dto.getTotalRows() : 8)
                    .seatsPerRow(dto.getSeatsPerRow() != null ? dto.getSeatsPerRow() : 10)
                    .price(dto.getPrice())
                    .build();
            screeningRepository.save(screening);

            redirectAttributes.addFlashAttribute("success", "Screening added successfully!");
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("movies", movieRepository.findAll());
            return "admin/add-screening";
        }
    }

    @GetMapping("/edit-screening/{id}")
    public String editScreeningForm(@PathVariable Long id, Model model) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        if (screening.getScreenTime().isBefore(LocalDateTime.now())) {
            return "redirect:/admin/dashboard";
        }

        ScreeningDto dto = new ScreeningDto();
        dto.setId(screening.getId());
        dto.setMovieTitle(screening.getMovie().getTitle());
        dto.setScreenTime(screening.getScreenTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setHallName(screening.getHallName());
        dto.setPrice(screening.getPrice());
        dto.setMovieId(screening.getMovie().getId());

        model.addAttribute("screeningDto", dto);
        return "admin/edit-screening";
    }

    @PostMapping("/edit-screening/{id}")
    public String editScreening(@PathVariable Long id, @ModelAttribute ScreeningDto dto,
                                RedirectAttributes redirectAttributes) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        screening.setScreenTime(LocalDateTime.parse(dto.getScreenTime()));
        screening.setPrice(dto.getPrice());
        if (dto.getHallName() != null) screening.setHallName(dto.getHallName());
        screeningRepository.save(screening);

        redirectAttributes.addFlashAttribute("success", "Screening updated!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/delete-screening/{id}")
    public String deleteScreening(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        long confirmedCount = bookingService.getScreeningBookings(id).stream()
                .filter(b -> b.getBookingStatus() == Booking.BookingStatus.CONFIRMED)
                .count();

        if (confirmedCount > 0) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete screening with confirmed bookings");
            return "redirect:/admin/dashboard";
        }

        screeningRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Screening deleted!");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/statistics/{screeningId}")
    public String bookingStatistics(@PathVariable Long screeningId, Model model) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Screening not found"));
        List<Booking> bookings = bookingService.getScreeningBookings(screeningId);
        var bookedSeats = bookingService.getBookedSeats(screeningId);

        model.addAttribute("screening", screening);
        model.addAttribute("bookings", bookings);
        model.addAttribute("bookedSeats", bookedSeats);
        model.addAttribute("totalSeats", screening.getTotalSeats());
        model.addAttribute("occupiedSeats", bookedSeats.size());

        return "admin/statistics";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "customer/profile";
    }
}
