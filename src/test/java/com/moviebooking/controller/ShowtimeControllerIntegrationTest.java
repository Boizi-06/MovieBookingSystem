package com.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.dto.ShowtimeRequest;
import com.moviebooking.entity.*;
import com.moviebooking.repository.*;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ShowtimeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User customerUser;
    private Cinema cinema;
    private Room room;
    private Movie movie;
    private String adminToken;
    private String customerToken;

    @BeforeEach
    public void setUp() {
        adminUser = User.builder()
                .fullname("Showtime Admin")
                .email("showtimeadmin@moviebooking.com")
                .password("Password123")
                .role("ADMIN")
                .status("ACTIVE")
                .build();
        adminUser = userRepository.save(adminUser);
        adminToken = "Bearer " + jwtTokenProvider.generateToken(adminUser);

        customerUser = User.builder()
                .fullname("Showtime Customer")
                .email("showtimecustomer@moviebooking.com")
                .password("Password123")
                .role("CUSTOMER")
                .status("ACTIVE")
                .build();
        customerUser = userRepository.save(customerUser);
        customerToken = "Bearer " + jwtTokenProvider.generateToken(customerUser);

        cinema = Cinema.builder()
                .name("CGV Showtime Cinema")
                .address("789 Test Rd")
                .city("Hà Nội")
                .status("ACTIVE")
                .build();
        cinema = cinemaRepository.save(cinema);

        room = Room.builder()
                .cinema(cinema)
                .name("Phòng 01")
                .roomType("STANDARD")
                .totalSeats(60)
                .status("ACTIVE")
                .build();
        room = roomRepository.save(room);

        movie = Movie.builder()
                .title("Dune: Part Two")
                .duration(166)
                .releaseDate(LocalDate.now().minusDays(5))
                .ageRating("C16")
                .language("Tiếng Anh")
                .status("NOW_SHOWING")
                .build();
        movie = movieRepository.save(movie);
    }

    @Test
    public void testCreateShowtime_Success() throws Exception {
        LocalDateTime startTime = LocalDateTime.now().plusDays(5);
        ShowtimeRequest request = ShowtimeRequest.builder()
                .movieId(movie.getId())
                .roomId(room.getId())
                .startTime(startTime)
                .endTime(startTime.plusMinutes(166))
                .basePrice(new BigDecimal("90000.00"))
                .status("SCHEDULED")
                .build();

        mockMvc.perform(post("/api/v1/showtimes")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.movieTitle", is("Dune: Part Two")))
                .andExpect(jsonPath("$.data.roomName", is("Phòng 01")))
                .andExpect(jsonPath("$.data.basePrice", is(90000.00)));
    }

    @Test
    public void testCreateShowtime_AutoCalculateEndTime() throws Exception {
        LocalDateTime startTime = LocalDateTime.now().plusDays(2);
        ShowtimeRequest request = ShowtimeRequest.builder()
                .movieId(movie.getId())
                .roomId(room.getId())
                .startTime(startTime)
                .endTime(null) // Để trống để kiểm thử tính năng tự động tính toán
                .basePrice(new BigDecimal("85000.00"))
                .status("SCHEDULED")
                .build();

        mockMvc.perform(post("/api/v1/showtimes")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.endTime", notNullValue()));

        Showtime createdShowtime = showtimeRepository.findAll().stream()
                .filter(s -> s.getBasePrice().compareTo(new BigDecimal("85000.00")) == 0)
                .findFirst().orElseThrow();
        assertEquals(startTime.plusMinutes(166), createdShowtime.getEndTime());
    }

    @Test
    public void testCreateShowtime_OverlappingTimeFailure() throws Exception {
        LocalDateTime baseTime = LocalDateTime.now().plusDays(3);
        
        // Tạo suất chiếu 1: 10:00 -> 12:46 (166 phút)
        Showtime showtime1 = Showtime.builder()
                .movie(movie)
                .room(room)
                .startTime(baseTime.withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(baseTime.withHour(10).withMinute(0).withSecond(0).withNano(0).plusMinutes(166))
                .basePrice(new BigDecimal("90000"))
                .status("SCHEDULED")
                .build();
        showtimeRepository.save(showtime1);

        // Thử tạo suất chiếu 2 chồng lấn: 11:00 -> 13:46 (trùng giờ với suất chiếu 1)
        ShowtimeRequest overlapRequest = ShowtimeRequest.builder()
                .movieId(movie.getId())
                .roomId(room.getId())
                .startTime(baseTime.withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(null)
                .basePrice(new BigDecimal("100000"))
                .build();

        mockMvc.perform(post("/api/v1/showtimes")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overlapRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Phòng chiếu đã có lịch chiếu trong khoảng thời gian này")));
    }

    @Test
    public void testCreateShowtime_InactiveRoomFailure() throws Exception {
        Room maintenanceRoom = Room.builder()
                .cinema(cinema)
                .name("Phòng bảo trì")
                .roomType("STANDARD")
                .totalSeats(60)
                .status("MAINTENANCE") // Phòng không hoạt động
                .build();
        maintenanceRoom = roomRepository.save(maintenanceRoom);

        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        ShowtimeRequest request = ShowtimeRequest.builder()
                .movieId(movie.getId())
                .roomId(maintenanceRoom.getId())
                .startTime(startTime)
                .basePrice(new BigDecimal("90000"))
                .build();

        mockMvc.perform(post("/api/v1/showtimes")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Phòng chiếu không hoạt động hoặc đang bảo trì")));
    }

    @Test
    public void testCreateShowtime_ForbiddenForCustomer() throws Exception {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        ShowtimeRequest request = ShowtimeRequest.builder()
                .movieId(movie.getId())
                .roomId(room.getId())
                .startTime(startTime)
                .basePrice(new BigDecimal("90000"))
                .build();

        mockMvc.perform(post("/api/v1/showtimes")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testSearchShowtimes_Success() throws Exception {
        Showtime showtime = Showtime.builder()
                .movie(movie)
                .room(room)
                .startTime(LocalDateTime.now().plusDays(4))
                .endTime(LocalDateTime.now().plusDays(4).plusMinutes(166))
                .basePrice(new BigDecimal("95000.00"))
                .status("OPEN")
                .build();
        showtimeRepository.save(showtime);

        // 1. Admin truy vấn
        mockMvc.perform(get("/api/v1/showtimes")
                        .header("Authorization", adminToken)
                        .param("movieId", movie.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));

        // 2. Customer truy vấn
        mockMvc.perform(get("/api/v1/showtimes")
                        .param("movieId", movie.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    public void testGetShowtimeById_Success() throws Exception {
        Showtime showtime = Showtime.builder()
                .movie(movie)
                .room(room)
                .startTime(LocalDateTime.now().plusDays(4))
                .endTime(LocalDateTime.now().plusDays(4).plusMinutes(166))
                .basePrice(new BigDecimal("95000.00"))
                .status("OPEN")
                .build();
        showtime = showtimeRepository.save(showtime);

        mockMvc.perform(get("/api/v1/showtimes/" + showtime.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(showtime.getId().intValue())))
                .andExpect(jsonPath("$.data.status", is("OPEN")));
    }

    @Test
    public void testUpdateShowtime_Success() throws Exception {
        Showtime showtime = Showtime.builder()
                .movie(movie)
                .room(room)
                .startTime(LocalDateTime.now().plusDays(4))
                .endTime(LocalDateTime.now().plusDays(4).plusMinutes(166))
                .basePrice(new BigDecimal("95000.00"))
                .status("OPEN")
                .build();
        showtime = showtimeRepository.save(showtime);

        LocalDateTime newStart = LocalDateTime.now().plusDays(6);
        ShowtimeRequest updateRequest = ShowtimeRequest.builder()
                .movieId(movie.getId())
                .roomId(room.getId())
                .startTime(newStart)
                .endTime(newStart.plusMinutes(166))
                .basePrice(new BigDecimal("110000.00"))
                .status("SCHEDULED")
                .build();

        mockMvc.perform(put("/api/v1/showtimes/" + showtime.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.basePrice", is(110000.00)))
                .andExpect(jsonPath("$.data.status", is("SCHEDULED")));
    }

    @Test
    public void testDeleteShowtime_SuccessNoBookings() throws Exception {
        Showtime showtime = Showtime.builder()
                .movie(movie)
                .room(room)
                .startTime(LocalDateTime.now().plusDays(4))
                .endTime(LocalDateTime.now().plusDays(4).plusMinutes(166))
                .basePrice(new BigDecimal("95000.00"))
                .status("OPEN")
                .build();
        showtime = showtimeRepository.save(showtime);

        mockMvc.perform(delete("/api/v1/showtimes/" + showtime.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Xóa lịch chiếu thành công")));
    }

    @Test
    public void testDeleteShowtime_WithBookingsSoftDelete() throws Exception {
        Showtime showtime = Showtime.builder()
                .movie(movie)
                .room(room)
                .startTime(LocalDateTime.now().plusDays(4))
                .endTime(LocalDateTime.now().plusDays(4).plusMinutes(166))
                .basePrice(new BigDecimal("95000.00"))
                .status("OPEN")
                .build();
        showtime = showtimeRepository.save(showtime);

        Booking booking = Booking.builder()
                .bookingCode("BKG-TEST-1234567")
                .user(customerUser)
                .showtime(showtime)
                .totalPrice(new BigDecimal("95000"))
                .status("PAID")
                .build();
        bookingRepository.save(booking);

        mockMvc.perform(delete("/api/v1/showtimes/" + showtime.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Chuyển trạng thái lịch chiếu sang CANCELLED")));

        Showtime deletedShowtime = showtimeRepository.findById(showtime.getId()).orElseThrow();
        assertEquals("CANCELLED", deletedShowtime.getStatus());
    }
}
