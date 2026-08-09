package com.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.dto.MovieRequest;
import com.moviebooking.entity.Cinema;
import com.moviebooking.entity.Genre;
import com.moviebooking.entity.Room;
import com.moviebooking.entity.Showtime;
import com.moviebooking.entity.User;
import com.moviebooking.repository.CinemaRepository;
import com.moviebooking.repository.GenreRepository;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.RoomRepository;
import com.moviebooking.repository.ShowtimeRepository;
import com.moviebooking.repository.UserRepository;
import com.moviebooking.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User customerUser;
    private String adminToken;
    private String customerToken;
    private Genre actionGenre;
    private Genre comedyGenre;
    private Room testRoom;

    @BeforeEach
    public void setUp() {
        // Tạo các User test
        adminUser = User.builder()
                .fullname("Test Movie Admin")
                .email("movieadmin@moviebooking.com")
                .password("Password123")
                .role("ADMIN")
                .status("ACTIVE")
                .build();
        adminUser = userRepository.save(adminUser);
        adminToken = "Bearer " + jwtTokenProvider.generateToken(adminUser);

        customerUser = User.builder()
                .fullname("Test Movie Customer")
                .email("moviecustomer@moviebooking.com")
                .password("Password123")
                .role("CUSTOMER")
                .status("ACTIVE")
                .build();
        customerUser = userRepository.save(customerUser);
        customerToken = "Bearer " + jwtTokenProvider.generateToken(customerUser);

        // Tạo các Genre test
        actionGenre = Genre.builder().name("Hành động Test").description("Thể loại hành động").build();
        actionGenre = genreRepository.save(actionGenre);

        comedyGenre = Genre.builder().name("Hài hước Test").description("Thể loại hài hước").build();
        comedyGenre = genreRepository.save(comedyGenre);

        // Tạo Cinema và Room test
        Cinema cinema = Cinema.builder()
                .name("CGV Test")
                .address("123 Test Street")
                .city("Hà Nội")
                .status("ACTIVE")
                .build();
        cinema = cinemaRepository.save(cinema);

        testRoom = Room.builder()
                .cinema(cinema)
                .name("Phòng 01")
                .roomType("STANDARD")
                .status("ACTIVE")
                .build();
        testRoom = roomRepository.save(testRoom);
    }

    @Test
    public void testCreateMovie_Success() throws Exception {
        MovieRequest request = MovieRequest.builder()
                .title("Lật Mặt 7: Một Điều Ước")
                .description("Phim về tình cảm gia đình kịch tính và cảm động.")
                .duration(138)
                .releaseDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(35))
                .ageRating("C13")
                .language("Tiếng Việt")
                .director("Lý Hải")
                .cast("Thanh Thức, Đinh Y Nhung, Trương Minh Cường")
                .posterUrl("http://example.com/poster.jpg")
                .trailerUrl("http://example.com/trailer.mp4")
                .status("UPCOMING")
                .genreIds(Set.of(actionGenre.getId(), comedyGenre.getId()))
                .build();

        mockMvc.perform(post("/api/v1/movies")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Thêm bộ phim mới thành công")))
                .andExpect(jsonPath("$.data.title", is("Lật Mặt 7: Một Điều Ước")))
                .andExpect(jsonPath("$.data.duration", is(138)))
                .andExpect(jsonPath("$.data.ageRating", is("C13")))
                .andExpect(jsonPath("$.data.director", is("Lý Hải")))
                .andExpect(jsonPath("$.data.genres", hasSize(2)));
    }

    @Test
    public void testCreateMovie_InvalidInput_MissingTitleAndGenres() throws Exception {
        MovieRequest request = MovieRequest.builder()
                .title("") // Trống
                .duration(0) // Duration <= 0
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .genreIds(Set.of()) // Trống
                .build();

        mockMvc.perform(post("/api/v1/movies")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    public void testCreateMovie_EndDateBeforeReleaseDate() throws Exception {
        MovieRequest request = MovieRequest.builder()
                .title("Phim Lỗi Ngày")
                .duration(120)
                .releaseDate(LocalDate.of(2026, 8, 10))
                .endDate(LocalDate.of(2026, 8, 1)) // Nhỏ hơn releaseDate
                .ageRating("P")
                .language("Tiếng Việt")
                .genreIds(Set.of(actionGenre.getId()))
                .build();

        mockMvc.perform(post("/api/v1/movies")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Ngày kết thúc phải lớn hơn hoặc bằng ngày khởi chiếu")));
    }

    @Test
    public void testCreateMovie_ForbiddenForCustomer() throws Exception {
        MovieRequest request = MovieRequest.builder()
                .title("Phim Của Customer")
                .duration(100)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .genreIds(Set.of(actionGenre.getId()))
                .build();

        mockMvc.perform(post("/api/v1/movies")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateMovie_Success() throws Exception {
        com.moviebooking.entity.Movie existingMovie = com.moviebooking.entity.Movie.builder()
                .title("Phim Ban Đầu")
                .description("Mô tả ban đầu")
                .duration(110)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .status("UPCOMING")
                .genres(new HashSet<>(Set.of(actionGenre)))
                .build();
        existingMovie = movieRepository.save(existingMovie);

        MovieRequest updateRequest = MovieRequest.builder()
                .title("Phim Đã Đổi Tên")
                .description("Mô tả đã cập nhật")
                .duration(125)
                .releaseDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(30))
                .ageRating("C16")
                .language("Tiếng Anh")
                .director("Christopher Nolan")
                .cast("Leonardo DiCaprio")
                .posterUrl("http://example.com/new_poster.jpg")
                .trailerUrl("http://example.com/new_trailer.mp4")
                .status("NOW_SHOWING")
                .genreIds(Set.of(actionGenre.getId(), comedyGenre.getId()))
                .build();

        mockMvc.perform(put("/api/v1/movies/" + existingMovie.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Cập nhật thông tin phim thành công")))
                .andExpect(jsonPath("$.data.title", is("Phim Đã Đổi Tên")))
                .andExpect(jsonPath("$.data.duration", is(125)))
                .andExpect(jsonPath("$.data.ageRating", is("C16")))
                .andExpect(jsonPath("$.data.director", is("Christopher Nolan")))
                .andExpect(jsonPath("$.data.status", is("NOW_SHOWING")));
    }

    @Test
    public void testUpdateMovie_NotFound() throws Exception {
        MovieRequest updateRequest = MovieRequest.builder()
                .title("Phim Không Tồn Tại")
                .duration(120)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .genreIds(Set.of(actionGenre.getId()))
                .build();

        mockMvc.perform(put("/api/v1/movies/999999")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Không tìm thấy bộ phim với ID: 999999")));
    }

    @Test
    public void testUpdateMovie_ForbiddenForCustomer() throws Exception {
        com.moviebooking.entity.Movie existingMovie = com.moviebooking.entity.Movie.builder()
                .title("Phim Thử Quyền")
                .duration(100)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .status("UPCOMING")
                .genres(new HashSet<>(Set.of(actionGenre)))
                .build();
        existingMovie = movieRepository.save(existingMovie);

        MovieRequest updateRequest = MovieRequest.builder()
                .title("Cố Tình Sửa Bởi Customer")
                .duration(100)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .genreIds(Set.of(actionGenre.getId()))
                .build();

        mockMvc.perform(put("/api/v1/movies/" + existingMovie.getId())
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteMovie_HardDeleteSuccess() throws Exception {
        com.moviebooking.entity.Movie movieToDelete = com.moviebooking.entity.Movie.builder()
                .title("Phim Chưa Có Suất Chiếu")
                .duration(90)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .status("UPCOMING")
                .genres(new HashSet<>(Set.of(actionGenre)))
                .build();
        movieToDelete = movieRepository.save(movieToDelete);

        mockMvc.perform(delete("/api/v1/movies/" + movieToDelete.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Xóa bộ phim khỏi hệ thống thành công")));

        assertFalse(movieRepository.existsById(movieToDelete.getId()));
    }

    @Test
    public void testDeleteMovie_SoftDeleteSuccess() throws Exception {
        com.moviebooking.entity.Movie movieWithPastShowtime = com.moviebooking.entity.Movie.builder()
                .title("Phim Đã Có Suất Chiếu Quá Khứ")
                .duration(90)
                .releaseDate(LocalDate.now().minusDays(10))
                .ageRating("P")
                .language("Tiếng Việt")
                .status("NOW_SHOWING")
                .genres(new HashSet<>(Set.of(actionGenre)))
                .build();
        movieWithPastShowtime = movieRepository.save(movieWithPastShowtime);

        Showtime pastShowtime = Showtime.builder()
                .movie(movieWithPastShowtime)
                .room(testRoom)
                .startTime(LocalDateTime.now().minusDays(2))
                .endTime(LocalDateTime.now().minusDays(2).plusMinutes(90))
                .basePrice(BigDecimal.valueOf(100000))
                .status("COMPLETED")
                .build();
        showtimeRepository.save(pastShowtime);

        mockMvc.perform(delete("/api/v1/movies/" + movieWithPastShowtime.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("INACTIVE")));

        com.moviebooking.entity.Movie updatedMovie = movieRepository.findById(movieWithPastShowtime.getId()).orElse(null);
        assertNotNull(updatedMovie);
        assertEquals("INACTIVE", updatedMovie.getStatus());
    }

    @Test
    public void testDeleteMovie_FutureShowtimeFailure() throws Exception {
        com.moviebooking.entity.Movie movieWithFutureShowtime = com.moviebooking.entity.Movie.builder()
                .title("Phim Đang Có Suất Chiếu Tương Lai")
                .duration(120)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .status("NOW_SHOWING")
                .genres(new HashSet<>(Set.of(actionGenre)))
                .build();
        movieWithFutureShowtime = movieRepository.save(movieWithFutureShowtime);

        Showtime futureShowtime = Showtime.builder()
                .movie(movieWithFutureShowtime)
                .room(testRoom)
                .startTime(LocalDateTime.now().plusDays(2))
                .endTime(LocalDateTime.now().plusDays(2).plusMinutes(120))
                .basePrice(BigDecimal.valueOf(120000))
                .status("SCHEDULED")
                .build();
        showtimeRepository.save(futureShowtime);

        mockMvc.perform(delete("/api/v1/movies/" + movieWithFutureShowtime.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Không thể xóa bộ phim đang có lịch chiếu trong tương lai")));
    }

    @Test
    public void testDeleteMovie_ForbiddenForCustomer() throws Exception {
        com.moviebooking.entity.Movie movie = com.moviebooking.entity.Movie.builder()
                .title("Phim Test Quyền")
                .duration(90)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .status("UPCOMING")
                .genres(new HashSet<>(Set.of(actionGenre)))
                .build();
        movie = movieRepository.save(movie);

        mockMvc.perform(delete("/api/v1/movies/" + movie.getId())
                        .header("Authorization", customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetMovies_PublicUser_ExcludesInactive() throws Exception {
        com.moviebooking.entity.Movie activeMovie = com.moviebooking.entity.Movie.builder()
                .title("Phim Active")
                .duration(100)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .status("NOW_SHOWING")
                .genres(new HashSet<>(Set.of(actionGenre)))
                .build();
        movieRepository.save(activeMovie);

        com.moviebooking.entity.Movie inactiveMovie = com.moviebooking.entity.Movie.builder()
                .title("Phim Inactive")
                .duration(100)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .status("INACTIVE")
                .genres(new HashSet<>(Set.of(actionGenre)))
                .build();
        movieRepository.save(inactiveMovie);

        mockMvc.perform(get("/api/v1/movies")
                        .header("Authorization", customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content[*].title", hasItem("Phim Active")))
                .andExpect(jsonPath("$.data.content[*].title", not(hasItem("Phim Inactive"))));
    }

    @Test
    public void testGetMovies_AdminUser_IncludesInactive() throws Exception {
        com.moviebooking.entity.Movie inactiveMovie = com.moviebooking.entity.Movie.builder()
                .title("Phim Admin Xem Inactive")
                .duration(100)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .status("INACTIVE")
                .genres(new HashSet<>(Set.of(actionGenre)))
                .build();
        movieRepository.save(inactiveMovie);

        mockMvc.perform(get("/api/v1/movies")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content[*].title", hasItem("Phim Admin Xem Inactive")));
    }

    @Test
    public void testSearchMovies_ByKeywordAndGenre() throws Exception {
        com.moviebooking.entity.Movie movie1 = com.moviebooking.entity.Movie.builder()
                .title("Kung Fu Panda 4")
                .duration(94)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Anh")
                .status("NOW_SHOWING")
                .genres(new HashSet<>(Set.of(comedyGenre)))
                .build();
        movieRepository.save(movie1);

        mockMvc.perform(get("/api/v1/movies")
                        .param("keyword", "panda")
                        .param("genreId", comedyGenre.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].title", is("Kung Fu Panda 4")));
    }

    @Test
    public void testSearchMovies_ByGenreNameAndPagination() throws Exception {
        com.moviebooking.entity.Movie movie = com.moviebooking.entity.Movie.builder()
                .title("Phim Hài Hước Đặc Sắc")
                .duration(105)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .status("NOW_SHOWING")
                .genres(new HashSet<>(Set.of(comedyGenre)))
                .build();
        movieRepository.save(movie);

        mockMvc.perform(get("/api/v1/movies")
                        .param("genreName", comedyGenre.getName())
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.pageable.pageSize", is(5)))
                .andExpect(jsonPath("$.data.content[*].title", hasItem("Phim Hài Hước Đặc Sắc")));
    }

    @Test
    public void testGetMovieDetail_Success() throws Exception {
        com.moviebooking.entity.Movie movie = com.moviebooking.entity.Movie.builder()
                .title("Dune: Phần Hai")
                .description("Hành trình trả thù và định mệnh của Paul Atreides.")
                .duration(166)
                .releaseDate(LocalDate.now())
                .ageRating("C16")
                .language("Tiếng Anh")
                .director("Denis Villeneuve")
                .status("NOW_SHOWING")
                .genres(new HashSet<>(Set.of(actionGenre)))
                .build();
        movie = movieRepository.save(movie);

        mockMvc.perform(get("/api/v1/movies/" + movie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Dune: Phần Hai")))
                .andExpect(jsonPath("$.data.director", is("Denis Villeneuve")));
    }

    @Test
    public void testGetMovieDetail_InactiveForbiddenForCustomer() throws Exception {
        com.moviebooking.entity.Movie inactiveMovie = com.moviebooking.entity.Movie.builder()
                .title("Phim Đã Khóa Chi Chiết")
                .duration(100)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .status("INACTIVE")
                .genres(new HashSet<>(Set.of(actionGenre)))
                .build();
        inactiveMovie = movieRepository.save(inactiveMovie);

        mockMvc.perform(get("/api/v1/movies/" + inactiveMovie.getId())
                        .header("Authorization", customerToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Phim không tồn tại hoặc đã dừng công chiếu")));
    }
}
