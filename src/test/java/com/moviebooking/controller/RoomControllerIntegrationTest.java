package com.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.dto.RoomRequest;
import com.moviebooking.entity.Cinema;
import com.moviebooking.entity.Room;
import com.moviebooking.entity.User;
import com.moviebooking.repository.CinemaRepository;
import com.moviebooking.repository.RoomRepository;
import com.moviebooking.repository.SeatRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RoomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User customerUser;
    private Cinema cinema;
    private String adminToken;
    private String customerToken;

    @BeforeEach
    public void setUp() {
        adminUser = User.builder()
                .fullname("Room Test Admin")
                .email("roomadmin@moviebooking.com")
                .password("Password123")
                .role("ADMIN")
                .status("ACTIVE")
                .build();
        adminUser = userRepository.save(adminUser);
        adminToken = "Bearer " + jwtTokenProvider.generateToken(adminUser);

        customerUser = User.builder()
                .fullname("Room Test Customer")
                .email("roomcustomer@moviebooking.com")
                .password("Password123")
                .role("CUSTOMER")
                .status("ACTIVE")
                .build();
        customerUser = userRepository.save(customerUser);
        customerToken = "Bearer " + jwtTokenProvider.generateToken(customerUser);

        cinema = Cinema.builder()
                .name("CGV Test Room Cinema")
                .address("123 Test Address")
                .city("Hà Nội")
                .status("ACTIVE")
                .build();
        cinema = cinemaRepository.save(cinema);
    }

    @Test
    public void testCreateRoom_AutoGenerates60Seats() throws Exception {
        RoomRequest request = RoomRequest.builder()
                .cinemaId(cinema.getId())
                .name("Phòng IMAX 01")
                .roomType("IMAX")
                .status("ACTIVE")
                .build();

        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Phòng IMAX 01")))
                .andExpect(jsonPath("$.data.roomType", is("IMAX")))
                .andExpect(jsonPath("$.data.totalSeats", is(60)));

        // Kiểm tra xem số lượng ghế sinh trong DB có đúng 60 ghế không
        Room createdRoom = roomRepository.findByCinemaId(cinema.getId()).get(0);
        int seatCount = seatRepository.countByRoomId(createdRoom.getId());
        assertEquals(60, seatCount);
    }

    @Test
    public void testCreateRoom_DuplicateNameInSameCinema() throws Exception {
        Room room = Room.builder()
                .cinema(cinema)
                .name("Phòng 01 Trùng Tên")
                .roomType("STANDARD")
                .totalSeats(60)
                .status("ACTIVE")
                .build();
        roomRepository.save(room);

        RoomRequest duplicateRequest = RoomRequest.builder()
                .cinemaId(cinema.getId())
                .name("Phòng 01 Trùng Tên")
                .roomType("VIP")
                .build();

        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Tên phòng chiếu đã tồn tại trong rạp chiếu phim này")));
    }

    @Test
    public void testGetRoomsByCinema_Success() throws Exception {
        Room room1 = Room.builder().cinema(cinema).name("Phòng Alpha").roomType("STANDARD").totalSeats(60).status("ACTIVE").build();
        roomRepository.save(room1);

        mockMvc.perform(get("/api/v1/rooms")
                        .param("cinemaId", cinema.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content[*].name", hasItem("Phòng Alpha")));
    }
}
