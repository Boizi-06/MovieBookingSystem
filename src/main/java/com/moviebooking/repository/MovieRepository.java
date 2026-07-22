package com.moviebooking.repository;

import com.moviebooking.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    boolean existsByTitle(String title);
    boolean existsByGenresId(Long genreId);

    @Query("SELECT DISTINCT m FROM Movie m LEFT JOIN m.genres g WHERE " +
           "(:keyword IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:genreId IS NULL OR g.id = :genreId) AND " +
           "(:genreName IS NULL OR LOWER(g.name) = LOWER(:genreName)) AND " +
           "(:status IS NULL OR m.status = :status) AND " +
           "(:excludeInactive = false OR m.status != 'INACTIVE')")
    Page<Movie> searchMovies(
            @Param("keyword") String keyword,
            @Param("genreId") Long genreId,
            @Param("genreName") String genreName,
            @Param("status") String status,
            @Param("excludeInactive") boolean excludeInactive,
            Pageable pageable);
}
