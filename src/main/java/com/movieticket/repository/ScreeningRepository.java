package com.movieticket.repository;

import com.movieticket.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @Query("SELECT s FROM Screening s JOIN FETCH s.movie WHERE s.screenTime > :now ORDER BY s.screenTime ASC")
    List<Screening> findUpcomingScreenings(LocalDateTime now);

    @Query("SELECT s FROM Screening s JOIN FETCH s.movie ORDER BY s.screenTime DESC")
    List<Screening> findAllWithMovie();

    @Query("SELECT s FROM Screening s JOIN FETCH s.movie WHERE s.movie.id = :movieId AND s.screenTime > :now ORDER BY s.screenTime ASC")
    List<Screening> findUpcomingByMovieId(Long movieId, LocalDateTime now);

    @Query("SELECT s FROM Screening s JOIN FETCH s.movie WHERE s.screenTime > :now AND (LOWER(s.movie.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Screening> searchUpcoming(String keyword, LocalDateTime now);

    @Query("SELECT s FROM Screening s JOIN FETCH s.movie WHERE CAST(s.screenTime AS date) = CAST(:date AS date) AND s.screenTime > :now ORDER BY s.screenTime ASC")
    List<Screening> findByDate(LocalDateTime date, LocalDateTime now);
}
