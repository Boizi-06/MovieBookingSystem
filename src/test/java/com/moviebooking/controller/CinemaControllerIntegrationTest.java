package com.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.dto.CinemaRequest;
import com.moviebooking.entity.Cinema;
import com.moviebooking.entity.User;
import com.moviebooking.repository.CinemaRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CinemaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private com.moviebooking.repository.RoomRepository roomRepository;

    @Autowired
    private com.moviebooking.repository.ShowtimeRepository showtimeRepository;

    @Autowired
    private com.moviebooking.repository.MovieRepository movieRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User customerUser;
    private String adminToken;
    private String customerToken;

    @BeforeEach
    public void setUp() {
        adminUser = User.builder()
                .fullname("Cinema Test Admin")
                .email("cinemaadmin@moviebooking.com")
                .password("Password123")
                .role("ADMIN")
                .status("ACTIVE")
                .build();
        adminUser = userRepository.save(adminUser);
        adminToken = "Bearer " + jwtTokenProvider.generateToken(adminUser);

        customerUser = User.builder()
                .fullname("Cinema Test Customer")
                .email("cinemacustomer@moviebooking.com")
                .password("Password123")
                .role("CUSTOMER")
                .status("ACTIVE")
                .build();
        customerUser = userRepository.save(customerUser);
        customerToken = "Bearer " + jwtTokenProvider.generateToken(customerUser);
    }

    @Test
    public void testCreateCinema_Success() throws Exception {
        CinemaRequest request = CinemaRequest.builder()
                .name("CGV Landmark 81")
                .address("720A Điện Biên Phủ, Phường 22, Bình Thạnh")
                .city("Hồ Chí Minh")
                .phone("02836220555")
                .status("ACTIVE")
                .build();

        mockMvc.perform(post("/api/v1/cinemas")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Thêm rạp chiếu phim mới thành công")))
                .andExpect(jsonPath("$.data.name", is("CGV Landmark 81")))
                .andExpect(jsonPath("$.data.city", is("Hồ Chí Minh")))
                .andExpect(jsonPath("$.data.status", is("ACTIVE")));
    }

    @Test
    public void testCreateCinema_DuplicateNameFailure() throws Exception {
        Cinema existingCinema = Cinema.builder()
                .name("CGV Vincom Đồng Khởi")
                .address("72 Lê Thánh Tôn")
                .city("Hồ Chí Minh")
                .status("ACTIVE")
                .build();
        cinemaRepository.save(existingCinema);

        CinemaRequest duplicateRequest = CinemaRequest.builder()
                .name("CGV Vincom Đồng Khởi")
                .address("Địa chỉ khác")
                .city("Hà Nội")
                .build();

        mockMvc.perform(post("/api/v1/cinemas")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Tên rạp chiếu phim đã tồn tại trong hệ thống")));
    }

    @Test
    public void testCreateCinema_MissingRequiredFields() throws Exception {
        CinemaRequest request = CinemaRequest.builder()
                .name("") // Bỏ trống tên
                .address("") // Bỏ trống địa chỉ
                .city("") // Bỏ trống thành phố
                .build();

        mockMvc.perform(post("/api/v1/cinemas")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    public void testCreateCinema_ForbiddenForCustomer() throws Exception {
        CinemaRequest request = CinemaRequest.builder()
                .name("Rạp Của Customer")
                .address("Địa chỉ")
                .city("Hồ Chí Minh")
                .build();

        mockMvc.perform(post("/api/v1/cinemas")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateCinema_Success() throws Exception {
        Cinema cinema = Cinema.builder()
                .name("CGV Hùng Vương")
                .address("126 Hùng Vương")
                .city("Hồ Chí Minh")
                .status("ACTIVE")
                .build();
        cinema = cinemaRepository.save(cinema);

        CinemaRequest updateRequest = CinemaRequest.builder()
                .name("CGV Hùng Vương Plaza")
                .address("126 Hồng Bàng, Phường 12, Quận 5")
                .city("Hồ Chí Minh")
                .phone("02838573888")
                .status("ACTIVE")
                .build();

        mockMvc.perform(put("/api/v1/cinemas/" + cinema.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Cập nhật thông tin rạp chiếu phim thành công")))
                .andExpect(jsonPath("$.data.name", is("CGV Hùng Vương Plaza")))
                .andExpect(jsonPath("$.data.phone", is("02838573888")));
    }

    @Test
    public void testUpdateCinema_NotFoundFailure() throws Exception {
        CinemaRequest updateRequest = CinemaRequest.builder()
                .name("CGV Không Tồn Tại")
                .address("Địa chỉ")
                .city("Hà Nội")
                .build();

        mockMvc.perform(put("/api/v1/cinemas/999999")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Không tìm thấy rạp chiếu phim với ID: 999999")));
    }

    @Test
    public void testUpdateCinema_DuplicateNameFailure() throws Exception {
        Cinema cinema1 = Cinema.builder().name("Rạp Thứ Nhất").address("Địa chỉ 1").city("Đà Nẵng").status("ACTIVE").build();
        cinema1 = cinemaRepository.save(cinema1);

        Cinema cinema2 = Cinema.builder().name("Rạp Thứ Hai").address("Địa chỉ 2").city("Đà Nẵng").status("ACTIVE").build();
        cinema2 = cinemaRepository.save(cinema2);

        // Đổi tên cinema2 thành tên của cinema1
        CinemaRequest duplicateRequest = CinemaRequest.builder()
                .name("Rạp Thứ Nhất")
                .address("Địa chỉ mới")
                .city("Đà Nẵng")
                .build();

        mockMvc.perform(put("/api/v1/cinemas/" + cinema2.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Tên rạp chiếu phim đã tồn tại trong hệ thống")));
    }

    @Test
    public void testUpdateCinema_ForbiddenForCustomer() throws Exception {
        Cinema cinema = Cinema.builder().name("Rạp Test Quyền").address("Địa chỉ").city("Hồ Chí Minh").status("ACTIVE").build();
        cinema = cinemaRepository.save(cinema);

        CinemaRequest updateRequest = CinemaRequest.builder()
                .name("Thử Sửa Bởi Customer")
                .address("Địa chỉ")
                .city("Hồ Chí Minh")
                .build();

        mockMvc.perform(put("/api/v1/cinemas/" + cinema.getId())
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteCinema_HardDeleteSuccess() throws Exception {
        Cinema unusedCinema = Cinema.builder()
                .name("Rạp Chưa Dùng")
                .address("Địa chỉ")
                .city("Cần Thơ")
                .status("ACTIVE")
                .build();
        unusedCinema = cinemaRepository.save(unusedCinema);

        mockMvc.perform(delete("/api/v1/cinemas/" + unusedCinema.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Xóa rạp chiếu phim thành công")));

        org.junit.jupiter.api.Assertions.assertFalse(cinemaRepository.existsById(unusedCinema.getId()));
    }

    @Test
    public void testDeleteCinema_SoftDeleteSuccess() throws Exception {
        Cinema cinemaWithRoom = Cinema.builder()
                .name("Rạp Đã Có Phòng")
                .address("Địa chỉ")
                .city("Hải Phòng")
                .status("ACTIVE")
                .build();
        cinemaWithRoom = cinemaRepository.save(cinemaWithRoom);

        com.moviebooking.entity.Room room = com.moviebooking.entity.Room.builder()
                .name("Phòng 01")
                .cinema(cinemaWithRoom)
                .status("ACTIVE")
                .build();
        roomRepository.save(room);

        mockMvc.perform(delete("/api/v1/cinemas/" + cinemaWithRoom.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("trạng thái ngưng hoạt động (INACTIVE)")));

        Cinema updatedCinema = cinemaRepository.findById(cinemaWithRoom.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("INACTIVE", updatedCinema.getStatus());
    }

    @Test
    public void testDeleteCinema_FutureShowtimeFailure() throws Exception {
        Cinema cinema = Cinema.builder()
                .name("Rạp Có Lịch Chiếu Tương Lai")
                .address("Địa chỉ")
                .city("Hà Nội")
                .status("ACTIVE")
                .build();
        cinema = cinemaRepository.save(cinema);

        com.moviebooking.entity.Room room = com.moviebooking.entity.Room.builder()
                .name("Phòng 02")
                .cinema(cinema)
                .status("ACTIVE")
                .build();
        room = roomRepository.save(room);

        com.moviebooking.entity.Movie movie = com.moviebooking.entity.Movie.builder()
                .title("Phim Chiếu Tương Lai")
                .duration(120)
                .releaseDate(java.time.LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .status("NOW_SHOWING")
                .build();
        movie = movieRepository.save(movie);

        com.moviebooking.entity.Showtime futureShowtime = com.moviebooking.entity.Showtime.builder()
                .movie(movie)
                .room(room)
                .startTime(java.time.LocalDateTime.now().plusDays(2))
                .endTime(java.time.LocalDateTime.now().plusDays(2).plusHours(2))
                .basePrice(new java.math.BigDecimal("90000"))
                .status("SCHEDULED")
                .build();
        showtimeRepository.save(futureShowtime);

        mockMvc.perform(delete("/api/v1/cinemas/" + cinema.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Không thể xóa rạp đang có lịch chiếu trong tương lai")));
    }

    @Test
    public void testDeleteCinema_ForbiddenForCustomer() throws Exception {
        Cinema cinema = Cinema.builder().name("Rạp Chặn Customer").address("Địa chỉ").city("Hà Nội").status("ACTIVE").build();
        cinema = cinemaRepository.save(cinema);

        mockMvc.perform(delete("/api/v1/cinemas/" + cinema.getId())
                        .header("Authorization", customerToken))
                .andExpect(status().isForbidden());
    }
}
