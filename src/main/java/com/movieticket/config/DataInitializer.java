package com.movieticket.config;

import com.movieticket.model.*;
import com.movieticket.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepo, MovieRepository movieRepo,
                               ScreeningRepository screeningRepo, PasswordEncoder encoder) {
        return args -> {
            if (userRepo.count() > 0) return;

            // Create admin user
            User admin = User.builder()
                    .username("admin")
                    .email("admin@movieticket.com")
                    .password(encoder.encode("admin123"))
                    .role(User.Role.ADMIN)
                    .build();
            userRepo.save(admin);

            // Create customer user
            User customer = User.builder()
                    .username("john")
                    .email("john@example.com")
                    .password(encoder.encode("john123"))
                    .role(User.Role.CUSTOMER)
                    .build();
            userRepo.save(customer);

            // Create movies
            Movie movie1 = Movie.builder()
                    .title("The Dark Knight Returns")
                    .description("A gripping tale of heroism and sacrifice in a city on the brink of chaos.")
                    .genre("Action, Thriller")
                    .durationMinutes(152)
                    .rating("PG-13")
                    .posterUrl("https://placehold.co/300x450/1a1a2e/e94560?text=Dark+Knight")
                    .build();
            movieRepo.save(movie1);

            Movie movie2 = Movie.builder()
                    .title("Interstellar Odyssey")
                    .description("A team of explorers travel through a wormhole in space to ensure humanity's survival.")
                    .genre("Sci-Fi, Adventure")
                    .durationMinutes(169)
                    .rating("PG-13")
                    .posterUrl("https://placehold.co/300x450/16213e/0f3460?text=Interstellar")
                    .build();
            movieRepo.save(movie2);

            Movie movie3 = Movie.builder()
                    .title("The Grand Budapest Hotel")
                    .description("A quirky comedy about a legendary concierge and a lobby boy in a famous European hotel.")
                    .genre("Comedy, Drama")
                    .durationMinutes(99)
                    .rating("R")
                    .posterUrl("https://placehold.co/300x450/e8d5b7/b8860b?text=Budapest")
                    .build();
            movieRepo.save(movie3);

            Movie movie4 = Movie.builder()
                    .title("Midnight in Tokyo")
                    .description("A romantic drama set against the neon-lit streets of Tokyo's nightlife district.")
                    .genre("Romance, Drama")
                    .durationMinutes(118)
                    .rating("PG-13")
                    .posterUrl("https://placehold.co/300x450/2d033b/810034?text=Tokyo")
                    .build();
            movieRepo.save(movie4);

            // Create screenings
            LocalDateTime now = LocalDateTime.now();
            createScreening(screeningRepo, movie1, now.plusDays(1).withHour(14).withMinute(0), "Hall A", new BigDecimal("12.99"));
            createScreening(screeningRepo, movie1, now.plusDays(1).withHour(19).withMinute(30), "Hall A", new BigDecimal("15.99"));
            createScreening(screeningRepo, movie2, now.plusDays(1).withHour(16).withMinute(0), "Hall B", new BigDecimal("14.99"));
            createScreening(screeningRepo, movie2, now.plusDays(2).withHour(20).withMinute(0), "Hall B", new BigDecimal("16.99"));
            createScreening(screeningRepo, movie3, now.plusDays(2).withHour(11).withMinute(0), "Hall C", new BigDecimal("10.99"));
            createScreening(screeningRepo, movie3, now.plusDays(3).withHour(15).withMinute(0), "Hall C", new BigDecimal("12.99"));
            createScreening(screeningRepo, movie4, now.plusDays(1).withHour(21).withMinute(0), "Hall A", new BigDecimal("13.99"));
            createScreening(screeningRepo, movie4, now.plusDays(3).withHour(18).withMinute(30), "Hall B", new BigDecimal("14.99"));

            System.out.println("=== Sample data initialized ===");
            System.out.println("Admin login: admin / admin123");
            System.out.println("Customer login: john / john123");
        };
    }

    private void createScreening(ScreeningRepository repo, Movie movie, LocalDateTime time, String hall, BigDecimal price) {
        Screening s = Screening.builder()
                .movie(movie)
                .screenTime(time)
                .hallName(hall)
                .totalRows(8)
                .seatsPerRow(10)
                .price(price)
                .build();
        repo.save(s);
    }
}
