package com.moviebooking.service;

import com.moviebooking.dto.MovieRequest;
import com.moviebooking.dto.MovieResponse;
import com.moviebooking.entity.Genre;
import com.moviebooking.entity.Movie;
import com.moviebooking.repository.GenreRepository;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ShowtimeRepository showtimeRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository, 
                        GenreRepository genreRepository, 
                        ShowtimeRepository showtimeRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.showtimeRepository = showtimeRepository;
    }

    @Transactional(readOnly = true)
    public Page<MovieResponse> getMovies(String keyword, Long genreId, String genreName, String status, String userRole, Pageable pageable) {
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String filterGenreName = (genreName != null && !genreName.trim().isEmpty()) ? genreName.trim() : null;
        String filterStatus = (status != null && !status.trim().isEmpty()) ? status.trim() : null;
        
        // BR-MOVIE-16, BR-MOVIE-28: Guest & Customer không được xem phim INACTIVE
        boolean excludeInactive = !"ADMIN".equalsIgnoreCase(userRole);

        Page<Movie> moviePage = movieRepository.searchMovies(searchKeyword, genreId, filterGenreName, filterStatus, excludeInactive, pageable);
        return moviePage.map(MovieResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id, String userRole) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ phim với ID: " + id));

        // BR-MOVIE-20: Guest & Customer không được xem chi tiết phim INACTIVE
        if (!"ADMIN".equalsIgnoreCase(userRole) && "INACTIVE".equalsIgnoreCase(movie.getStatus())) {
            throw new IllegalArgumentException("Phim không tồn tại hoặc đã dừng công chiếu");
        }

        return MovieResponse.fromEntity(movie);
    }

    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        // BR-MOVIE-04: Ngày kết thúc phải lớn hơn hoặc bằng ngày khởi chiếu
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getReleaseDate())) {
            throw new IllegalArgumentException("Ngày kết thúc phải lớn hơn hoặc bằng ngày khởi chiếu");
        }

        // BR-MOVIE-05: Mỗi phim phải thuộc ít nhất 1 thể loại và thể loại phải tồn tại
        if (request.getGenreIds() == null || request.getGenreIds().isEmpty()) {
            throw new IllegalArgumentException("Mỗi phim phải thuộc ít nhất một thể loại");
        }

        List<Genre> foundGenres = genreRepository.findAllById(request.getGenreIds());
        if (foundGenres.size() != request.getGenreIds().size()) {
            throw new IllegalArgumentException("Một hoặc nhiều thể loại chọn không tồn tại trên hệ thống");
        }

        Set<Genre> genreSet = new HashSet<>(foundGenres);

        // Mặc định trạng thái nếu người dùng không chọn
        String status = (request.getStatus() != null && !request.getStatus().trim().isEmpty()) 
                ? request.getStatus() 
                : "UPCOMING";

        Movie movie = Movie.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .duration(request.getDuration())
                .releaseDate(request.getReleaseDate())
                .endDate(request.getEndDate())
                .ageRating(request.getAgeRating() != null ? request.getAgeRating().trim() : "P")
                .language(request.getLanguage() != null ? request.getLanguage().trim() : "Tiếng Việt")
                .director(request.getDirector() != null ? request.getDirector().trim() : null)
                .cast(request.getCast() != null ? request.getCast().trim() : null)
                .posterUrl(request.getPosterUrl() != null ? request.getPosterUrl().trim() : null)
                .trailerUrl(request.getTrailerUrl() != null ? request.getTrailerUrl().trim() : null)
                .status(status)
                .genres(genreSet)
                .build();

        Movie savedMovie = movieRepository.save(movie);
        return MovieResponse.fromEntity(savedMovie);
    }

    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ phim với ID: " + id));

        // BR-MOVIE-11: Ngày kết thúc không được nhỏ hơn ngày khởi chiếu
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getReleaseDate())) {
            throw new IllegalArgumentException("Ngày kết thúc phải lớn hơn hoặc bằng ngày khởi chiếu");
        }

        // BR-MOVIE-09: Nếu phim đang có lịch chiếu, không được sửa thời lượng phim
        boolean hasShowtimes = showtimeRepository.existsByMovieId(id);
        if (hasShowtimes && !movie.getDuration().equals(request.getDuration())) {
            throw new IllegalArgumentException("Phim đang có lịch chiếu, không thể thay đổi thời lượng phim");
        }

        // BR-MOVIE-05: Mỗi phim phải thuộc ít nhất một thể loại
        if (request.getGenreIds() == null || request.getGenreIds().isEmpty()) {
            throw new IllegalArgumentException("Mỗi phim phải thuộc ít nhất một thể loại");
        }

        List<Genre> foundGenres = genreRepository.findAllById(request.getGenreIds());
        if (foundGenres.size() != request.getGenreIds().size()) {
            throw new IllegalArgumentException("Một hoặc nhiều thể loại chọn không tồn tại trên hệ thống");
        }

        Set<Genre> genreSet = new HashSet<>(foundGenres);

        String status = (request.getStatus() != null && !request.getStatus().trim().isEmpty()) 
                ? request.getStatus() 
                : movie.getStatus();

        movie.setTitle(request.getTitle().trim());
        movie.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        movie.setDuration(request.getDuration());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setEndDate(request.getEndDate());
        movie.setAgeRating(request.getAgeRating() != null ? request.getAgeRating().trim() : movie.getAgeRating());
        movie.setLanguage(request.getLanguage() != null ? request.getLanguage().trim() : movie.getLanguage());
        movie.setDirector(request.getDirector() != null ? request.getDirector().trim() : null);
        movie.setCast(request.getCast() != null ? request.getCast().trim() : null);
        movie.setPosterUrl(request.getPosterUrl() != null ? request.getPosterUrl().trim() : null);
        movie.setTrailerUrl(request.getTrailerUrl() != null ? request.getTrailerUrl().trim() : null);
        movie.setStatus(status);
        movie.setGenres(genreSet);

        Movie updatedMovie = movieRepository.save(movie);
        return MovieResponse.fromEntity(updatedMovie);
    }

    @Transactional
    public String deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ phim với ID: " + id));

        // BR-MOVIE-13: Không được xóa phim có lịch chiếu trong tương lai
        boolean hasFutureShowtimes = showtimeRepository.existsByMovieIdAndStartTimeAfter(id, LocalDateTime.now());
        if (hasFutureShowtimes) {
            throw new IllegalArgumentException("Không thể xóa bộ phim đang có lịch chiếu trong tương lai");
        }

        // BR-MOVIE-14: Phim đã có dữ liệu liên quan (lịch chiếu quá khứ/booking) -> Soft delete sang INACTIVE
        boolean hasAnyShowtimes = showtimeRepository.existsByMovieId(id);
        if (hasAnyShowtimes) {
            movie.setStatus("INACTIVE");
            movieRepository.save(movie);
            return "Bộ phim đã được chuyển sang trạng thái ngừng hiển thị (INACTIVE)";
        } else {
            // Chưa phát sinh lịch chiếu -> Hard delete
            movieRepository.delete(movie);
            return "Xóa bộ phim khỏi hệ thống thành công";
        }
    }
}
