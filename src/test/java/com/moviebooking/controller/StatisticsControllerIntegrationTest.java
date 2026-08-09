package com.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.entity.*;
import com.moviebooking.repository.*;
import com.moviebooking.security.JwtTokenProvider;
import com.moviebooking.service.SeatService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StatisticsControllerIntegrationTest {

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
    private SeatRepository seatRepository;

    @Autowired
    private SeatService seatService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private User adminUser;
    private User customerUser;
    private Cinema cinema;
    private Room room;
    private Movie movie1;
    private Movie movie2;
    private Showtime showtime1;
    private Showtime showtime2;
    private List<Seat> generatedSeats;
    private String adminToken;
    private String customerToken;

    @BeforeEach
    public void setUp() {
        adminUser = User.builder()
                .fullname("Stats Admin")
                .email("statsadmin@moviebooking.com")
                .password("Password123")
                .role("ADMIN")
                .status("ACTIVE")
                .build();
        adminUser = userRepository.save(adminUser);
        adminToken = "Bearer " + jwtTokenProvider.generateToken(adminUser);

        customerUser = User.builder()
                .fullname("Stats Customer")
                .email("statscustomer@moviebooking.com")
                .password("Password123")
                .role("CUSTOMER")
                .status("ACTIVE")
                .build();
        customerUser = userRepository.save(customerUser);
        customerToken = "Bearer " + jwtTokenProvider.generateToken(customerUser);

        cinema = Cinema.builder()
                .name("CGV Stats Cinema")
                .address("123 Stats Street")
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

        generatedSeats = seatService.generateDefaultSeats(room);

        movie1 = Movie.builder()
                .title("Interstellar")
                .duration(169)
                .releaseDate(LocalDate.now().minusDays(5))
                .ageRating("P")
                .language("Tiếng Anh")
                .genres(new HashSet<>())
                .status("NOW_SHOWING")
                .build();
        movie1 = movieRepository.save(movie1);

        movie2 = Movie.builder()
                .title("Inception")
                .duration(148)
                .releaseDate(LocalDate.now().minusDays(5))
                .ageRating("P")
                .language("Tiếng Anh")
                .genres(new HashSet<>())
                .status("NOW_SHOWING")
                .build();
        movie2 = movieRepository.save(movie2);

        showtime1 = Showtime.builder()
                .movie(movie1)
                .room(room)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusMinutes(169))
                .basePrice(new BigDecimal("100000.00"))
                .status("SCHEDULED")
                .build();
        showtime1 = showtimeRepository.save(showtime1);

        showtime2 = Showtime.builder()
                .movie(movie2)
                .room(room)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusMinutes(148))
                .basePrice(new BigDecimal("80000.00"))
                .status("SCHEDULED")
                .build();
        showtime2 = showtimeRepository.save(showtime2);
    }

    @Test
    public void testCustomerAccessStatistics_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/statistics/revenue")
                        .header("Authorization", customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetRevenueAndTicketsStatistics_Success() throws Exception {
        // Tạo đơn hàng 1 (MB Bank, PAID, 1 ghế) cho showtime1
        Booking booking1 = Booking.builder()
                .bookingCode("BKG-S1")
                .user(customerUser)
                .showtime(showtime1)
                .seats(new HashSet<>(Set.of(generatedSeats.get(0))))
                .totalPrice(new BigDecimal("100000.00"))
                .status("PAID")
                .build();
        booking1 = bookingRepository.save(booking1);

        // Tạo đơn hàng 2 (MB Bank, PAID, 2 ghế) cho showtime2
        Booking booking2 = Booking.builder()
                .bookingCode("BKG-S2")
                .user(customerUser)
                .showtime(showtime2)
                .seats(new HashSet<>(Set.of(generatedSeats.get(1), generatedSeats.get(2))))
                .totalPrice(new BigDecimal("160000.00")) // 2 ghế * 80000
                .status("PAID")
                .build();
        booking2 = bookingRepository.save(booking2);

        // Đồng bộ lên database
        bookingRepository.flush();

        // Chỉnh sửa thời gian tạo booking2 về 2 ngày trước để test nhóm theo ngày
        entityManager.createNativeQuery("UPDATE bookings SET created_at = :createdAt WHERE id = :id")
                .setParameter("createdAt", LocalDateTime.now().minusDays(2))
                .setParameter("id", booking2.getId())
                .executeUpdate();

        entityManager.clear();

        // 1. Kiểm tra doanh thu
        mockMvc.perform(get("/api/v1/statistics/revenue")
                        .header("Authorization", adminToken)
                        .param("startDate", LocalDate.now().minusDays(5).toString())
                        .param("endDate", LocalDate.now().toString())
                        .param("groupBy", "DAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].revenue", is(160000.00))) // Ngày cũ hơn (2 ngày trước) xếp trước do TreeMap sort key
                .andExpect(jsonPath("$.data[1].revenue", is(100000.00))); // Ngày hôm nay xếp sau

        // 2. Kiểm tra số lượng vé
        mockMvc.perform(get("/api/v1/statistics/tickets")
                        .header("Authorization", adminToken)
                        .param("startDate", LocalDate.now().minusDays(5).toString())
                        .param("endDate", LocalDate.now().toString())
                        .param("groupBy", "DAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].ticketCount", is(2))) // 2 vé của booking2
                .andExpect(jsonPath("$.data[1].ticketCount", is(1))); // 1 vé của booking1
    }

    @Test
    public void testGetMovieStatistics_Success() throws Exception {
        // Phim Interstellar (booking1: 1 ghế, 100k)
        Booking booking1 = Booking.builder()
                .bookingCode("BKG-M1")
                .user(customerUser)
                .showtime(showtime1)
                .seats(new HashSet<>(Set.of(generatedSeats.get(0))))
                .totalPrice(new BigDecimal("100000.00"))
                .status("PAID")
                .build();
        bookingRepository.save(booking1);

        // Phim Inception (booking2: 2 ghế, 160k)
        Booking booking2 = Booking.builder()
                .bookingCode("BKG-M2")
                .user(customerUser)
                .showtime(showtime2)
                .seats(new HashSet<>(Set.of(generatedSeats.get(1), generatedSeats.get(2))))
                .totalPrice(new BigDecimal("160000.00"))
                .status("PAID")
                .build();
        bookingRepository.save(booking2);

        bookingRepository.flush();
        entityManager.clear();

        // Gọi API thống kê phim (Sắp xếp theo doanh thu giảm dần: Inception 160k trước, Interstellar 100k sau)
        mockMvc.perform(get("/api/v1/statistics/movies")
                        .header("Authorization", adminToken)
                        .param("startDate", LocalDate.now().minusDays(5).toString())
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].movieTitle", is("Inception")))
                .andExpect(jsonPath("$.data[0].ticketsSold", is(2)))
                .andExpect(jsonPath("$.data[0].revenue", is(160000.00)))
                .andExpect(jsonPath("$.data[1].movieTitle", is("Interstellar")))
                .andExpect(jsonPath("$.data[1].ticketsSold", is(1)))
                .andExpect(jsonPath("$.data[1].revenue", is(100000.00)));
    }
}
