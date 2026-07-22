package com.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.dto.SeatStatusUpdateRequest;
import com.moviebooking.entity.Cinema;
import com.moviebooking.entity.Room;
import com.moviebooking.entity.Seat;
import com.moviebooking.entity.User;
import com.moviebooking.repository.CinemaRepository;
import com.moviebooking.repository.RoomRepository;
import com.moviebooking.repository.SeatRepository;
import com.moviebooking.repository.UserRepository;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SeatControllerIntegrationTest {

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
    private SeatService seatService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private Room room;
    private String adminToken;

    @BeforeEach
    public void setUp() {
        adminUser = User.builder()
                .fullname("Seat Test Admin")
                .email("seatadmin@moviebooking.com")
                .password("Password123")
                .role("ADMIN")
                .status("ACTIVE")
                .build();
        adminUser = userRepository.save(adminUser);
        adminToken = "Bearer " + jwtTokenProvider.generateToken(adminUser);

        Cinema cinema = Cinema.builder()
                .name("CGV Test Seat Cinema")
                .address("456 Test Address")
                .city("Hồ Chí Minh")
                .status("ACTIVE")
                .build();
        cinema = cinemaRepository.save(cinema);

        room = Room.builder()
                .cinema(cinema)
                .name("Phòng 01 Test Ghế")
                .roomType("STANDARD")
                .totalSeats(60)
                .status("ACTIVE")
                .build();
        room = roomRepository.save(room);

        // Sinh 60 ghế tự động cho phòng
        seatService.generateDefaultSeats(room);
    }

    @Test
    public void testGetSeatsByRoomId_Success() throws Exception {
        mockMvc.perform(get("/api/v1/seats/room/" + room.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(60)))
                .andExpect(jsonPath("$.data[0].seatCode", is("A01")))
                .andExpect(jsonPath("$.data[0].seatType", is("STANDARD")))
                .andExpect(jsonPath("$.data[59].seatCode", is("H04")))
                .andExpect(jsonPath("$.data[59].seatType", is("COUPLE")));
    }

    @Test
    public void testUpdateSeatStatus_Success() throws Exception {
        List<Seat> seats = seatRepository.findByRoomIdOrderBySeatRowAscSeatNumberAsc(room.getId());
        Seat firstSeat = seats.get(0);

        SeatStatusUpdateRequest request = SeatStatusUpdateRequest.builder()
                .status("MAINTENANCE")
                .build();

        mockMvc.perform(put("/api/v1/seats/" + firstSeat.getId() + "/status")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("MAINTENANCE")));

        Seat updatedSeat = seatRepository.findById(firstSeat.getId()).orElseThrow();
        assertEquals("MAINTENANCE", updatedSeat.getStatus());
    }
}
