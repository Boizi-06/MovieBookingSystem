package com.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.dto.BookingRequest;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookingControllerIntegrationTest {

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
    private TicketRepository ticketRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatService seatService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private User adminUser;
    private User customerUser;
    private User anotherCustomerUser;
    private Cinema cinema;
    private Room room;
    private Movie movie;
    private Showtime showtime;
    private List<Seat> generatedSeats;
    private String adminToken;
    private String customerToken;
    private String anotherCustomerToken;

    @BeforeEach
    public void setUp() {
        adminUser = User.builder()
                .fullname("Booking Admin")
                .email("bookingadmin@moviebooking.com")
                .password("Password123")
                .role("ADMIN")
                .status("ACTIVE")
                .build();
        adminUser = userRepository.save(adminUser);
        adminToken = "Bearer " + jwtTokenProvider.generateToken(adminUser);

        customerUser = User.builder()
                .fullname("Booking Customer")
                .email("bookingcustomer@moviebooking.com")
                .password("Password123")
                .role("CUSTOMER")
                .status("ACTIVE")
                .build();
        customerUser = userRepository.save(customerUser);
        customerToken = "Bearer " + jwtTokenProvider.generateToken(customerUser);

        anotherCustomerUser = User.builder()
                .fullname("Booking Another Customer")
                .email("bookinganother@moviebooking.com")
                .password("Password123")
                .role("CUSTOMER")
                .status("ACTIVE")
                .build();
        anotherCustomerUser = userRepository.save(anotherCustomerUser);
        anotherCustomerToken = "Bearer " + jwtTokenProvider.generateToken(anotherCustomerUser);

        cinema = Cinema.builder()
                .name("CGV Booking Cinema")
                .address("101 Booking Road")
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

        // Sinh 60 ghế cho phòng
        generatedSeats = seatService.generateDefaultSeats(room);

        movie = Movie.builder()
                .title("Dune: Part Three")
                .duration(150)
                .releaseDate(LocalDate.now().minusDays(2))
                .ageRating("C16")
                .language("Tiếng Anh")
                .genres(new HashSet<>())
                .status("NOW_SHOWING")
                .build();
        movie = movieRepository.save(movie);

        showtime = Showtime.builder()
                .movie(movie)
                .room(room)
                .startTime(LocalDateTime.now().plusDays(2))
                .endTime(LocalDateTime.now().plusDays(2).plusMinutes(150))
                .basePrice(new BigDecimal("100000.00"))
                .status("SCHEDULED")
                .build();
        showtime = showtimeRepository.save(showtime);
    }

    @Test
    public void testCreateBooking_Success() throws Exception {
        Seat seat1 = generatedSeats.get(0); // A01
        Seat seat2 = generatedSeats.get(1); // A02

        BookingRequest request = BookingRequest.builder()
                .showtimeId(showtime.getId())
                .seatIds(Set.of(seat1.getId(), seat2.getId()))
                .build();

        mockMvc.perform(post("/api/v1/bookings")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.bookingCode", startsWith("BKG-")))
                .andExpect(jsonPath("$.data.totalPrice", is(200000.00)))
                .andExpect(jsonPath("$.data.status", is("PENDING_PAYMENT")))
                .andExpect(jsonPath("$.data.expiresAt", notNullValue()));

        List<Booking> bookings = bookingRepository.findAll();
        assertEquals(1, bookings.size());
        Booking savedBooking = bookings.get(0);
        assertEquals("PENDING_PAYMENT", savedBooking.getStatus());
        assertEquals(2, savedBooking.getSeats().size());
    }

    @Test
    public void testCreateBooking_SeatAlreadyBookedFailure() throws Exception {
        Seat seat1 = generatedSeats.get(0); // A01

        // Đặt ghế trước bằng một đơn PAID
        Booking firstBooking = Booking.builder()
                .bookingCode("BKG-EXISTING")
                .user(anotherCustomerUser)
                .showtime(showtime)
                .seats(new HashSet<>(Set.of(seat1)))
                .totalPrice(new BigDecimal("100000.00"))
                .status("PAID")
                .build();
        bookingRepository.save(firstBooking);

        BookingRequest request = BookingRequest.builder()
                .showtimeId(showtime.getId())
                .seatIds(Set.of(seat1.getId()))
                .build();

        mockMvc.perform(post("/api/v1/bookings")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("đã được đặt hoặc đang được giữ bởi người khác")));
    }

    @Test
    public void testCreateBooking_SeatAlreadyHeldSuccessAfterExpiration() throws Exception {
        Seat seat1 = generatedSeats.get(0); // A01

        // Đơn giữ ghế hết hạn (tạo quá 5 phút trước)
        Booking expiredBooking = Booking.builder()
                .bookingCode("BKG-EXPIRED")
                .user(anotherCustomerUser)
                .showtime(showtime)
                .seats(new HashSet<>(Set.of(seat1)))
                .totalPrice(new BigDecimal("100000.00"))
                .status("PENDING_PAYMENT")
                .build();
        expiredBooking = bookingRepository.save(expiredBooking);

        bookingRepository.flush();
        entityManager.createNativeQuery("UPDATE bookings SET created_at = :createdAt WHERE id = :id")
                .setParameter("createdAt", LocalDateTime.now().minusMinutes(6))
                .setParameter("id", expiredBooking.getId())
                .executeUpdate();
        entityManager.clear();

        BookingRequest request = BookingRequest.builder()
                .showtimeId(showtime.getId())
                .seatIds(Set.of(seat1.getId()))
                .build();

        // Đặt lại ghế đó, hệ thống phải cho phép đặt vì đơn giữ cũ đã hết hạn
        mockMvc.perform(post("/api/v1/bookings")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("PENDING_PAYMENT")));
    }

    @Test
    public void testGetSeatsByShowtime_StatusMapping() throws Exception {
        Seat seat1 = generatedSeats.get(0); // A01 - Sẽ được mua (BOOKED)
        Seat seat2 = generatedSeats.get(1); // A02 - Sẽ được giữ (HOLD)
        Seat seat3 = generatedSeats.get(2); // A03 - Sẽ bị bảo trì (MAINTENANCE)

        // 1. Lưu đơn đã thanh toán cho seat1
        Booking paidBooking = Booking.builder()
                .bookingCode("BKG-PAID")
                .user(anotherCustomerUser)
                .showtime(showtime)
                .seats(new HashSet<>(Set.of(seat1)))
                .totalPrice(new BigDecimal("100000.00"))
                .status("PAID")
                .build();
        bookingRepository.save(paidBooking);

        // 2. Lưu đơn đang giữ cho seat2
        Booking holdBooking = Booking.builder()
                .bookingCode("BKG-HOLD")
                .user(anotherCustomerUser)
                .showtime(showtime)
                .seats(new HashSet<>(Set.of(seat2)))
                .totalPrice(new BigDecimal("100000.00"))
                .status("PENDING_PAYMENT")
                .build();
        bookingRepository.save(holdBooking);

        // 3. Khóa bảo trì cho seat3
        seat3.setStatus("MAINTENANCE");
        seatRepository.save(seat3);

        mockMvc.perform(get("/api/v1/seats/showtime/" + showtime.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[?(@.seatCode=='A01')].status", contains("BOOKED")))
                .andExpect(jsonPath("$.data[?(@.seatCode=='A02')].status", contains("HOLD")))
                .andExpect(jsonPath("$.data[?(@.seatCode=='A03')].status", contains("MAINTENANCE")))
                .andExpect(jsonPath("$.data[?(@.seatCode=='A04')].status", contains("AVAILABLE")));
    }

    @Test
    public void testGetPaymentDetails_Success() throws Exception {
        Seat seat1 = generatedSeats.get(0);

        Booking booking = Booking.builder()
                .bookingCode("BKG-PAY-TEST")
                .user(customerUser)
                .showtime(showtime)
                .seats(new HashSet<>(Set.of(seat1)))
                .totalPrice(new BigDecimal("100000.00"))
                .status("PENDING_PAYMENT")
                .build();
        booking = bookingRepository.save(booking);

        mockMvc.perform(get("/api/v1/bookings/" + booking.getId() + "/payment")
                        .header("Authorization", customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.bookingCode", is("BKG-PAY-TEST")))
                .andExpect(jsonPath("$.data.qrCodeUrl", containsString("MB-0999999999")));
    }

    @Test
    public void testProcessPaymentMockSuccess_Success() throws Exception {
        Seat seat1 = generatedSeats.get(0);
        Seat seat2 = generatedSeats.get(1);

        Booking booking = Booking.builder()
                .bookingCode("BKG-MOCK-PAY")
                .user(customerUser)
                .showtime(showtime)
                .seats(new HashSet<>(Set.of(seat1, seat2)))
                .totalPrice(new BigDecimal("200000.00"))
                .status("PENDING_PAYMENT")
                .build();
        booking = bookingRepository.save(booking);

        mockMvc.perform(post("/api/v1/payments/mock-success")
                        .param("bookingCode", "BKG-MOCK-PAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("PAID")))
                .andExpect(jsonPath("$.data.ticketCodes", hasSize(2)));

        List<Ticket> tickets = ticketRepository.findByBookingId(booking.getId());
        assertEquals(2, tickets.size());
        assertEquals("ACTIVE", tickets.get(0).getStatus());
    }

    @Test
    public void testGetBookingHistory_Success() throws Exception {
        Seat seat1 = generatedSeats.get(0);

        Booking booking = Booking.builder()
                .bookingCode("BKG-HISTORY-TEST")
                .user(customerUser)
                .showtime(showtime)
                .seats(new HashSet<>(Set.of(seat1)))
                .totalPrice(new BigDecimal("100000.00"))
                .status("PENDING_PAYMENT")
                .build();
        booking = bookingRepository.save(booking);

        // Giả lập thanh toán thành công
        mockMvc.perform(post("/api/v1/payments/mock-success")
                        .param("bookingCode", "BKG-HISTORY-TEST"))
                .andExpect(status().isOk());

        // Lấy lịch sử
        mockMvc.perform(get("/api/v1/bookings/my-history")
                        .header("Authorization", customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].bookingCode", is("BKG-HISTORY-TEST")))
                .andExpect(jsonPath("$.data[0].status", is("PAID")))
                .andExpect(jsonPath("$.data[0].ticketCodes", hasSize(1)));
    }
}
