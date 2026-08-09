package com.moviebooking.service;

import com.moviebooking.dto.RoomRequest;
import com.moviebooking.dto.RoomResponse;
import com.moviebooking.entity.Cinema;
import com.moviebooking.entity.Room;
import com.moviebooking.entity.Seat;
import com.moviebooking.repository.CinemaRepository;
import com.moviebooking.repository.RoomRepository;
import com.moviebooking.repository.SeatRepository;
import com.moviebooking.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final CinemaRepository cinemaRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final SeatService seatService;

    @Autowired
    public RoomService(RoomRepository roomRepository,
                       CinemaRepository cinemaRepository,
                       ShowtimeRepository showtimeRepository,
                       SeatRepository seatRepository,
                       SeatService seatService) {
        this.roomRepository = roomRepository;
        this.cinemaRepository = cinemaRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.seatService = seatService;
    }

    @Transactional(readOnly = true)
    public Page<RoomResponse> getRoomsByCinema(Long cinemaId, Pageable pageable) {
        Page<Room> roomPage = roomRepository.findByCinemaId(cinemaId, pageable);
        return roomPage.map(RoomResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng chiếu với ID: " + id));
        return RoomResponse.fromEntity(room);
    }

    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        Cinema cinema = cinemaRepository.findById(request.getCinemaId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy rạp chiếu phim với ID: " + request.getCinemaId()));

        String name = request.getName().trim();

        // BR-CINEMA-19: Tên phòng phải là duy nhất trong cùng một rạp
        if (roomRepository.existsByNameAndCinemaId(name, request.getCinemaId())) {
            throw new IllegalArgumentException("Tên phòng chiếu đã tồn tại trong rạp chiếu phim này");
        }

        String roomType = (request.getRoomType() != null && !request.getRoomType().trim().isEmpty())
                ? request.getRoomType().trim().toUpperCase()
                : "STANDARD";

        String status = (request.getStatus() != null && !request.getStatus().trim().isEmpty())
                ? request.getStatus().trim().toUpperCase()
                : "ACTIVE";

        Room room = Room.builder()
                .cinema(cinema)
                .name(name)
                .roomType(roomType)
                .totalSeats(0)
                .status(status)
                .build();

        Room savedRoom = roomRepository.save(room);

        // Khởi tạo tự động sơ đồ ghế mặc định cho phòng chiếu mới
        List<Seat> generatedSeats = seatService.generateDefaultSeats(savedRoom);
        savedRoom.setTotalSeats(generatedSeats.size());
        savedRoom = roomRepository.save(savedRoom);

        return RoomResponse.fromEntity(savedRoom);
    }

    @Transactional
    public RoomResponse resetSeatsForRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng chiếu với ID: " + roomId));

        List<Seat> newSeats = seatService.resetAndRegenerateSeatsForRoom(room);
        room.setTotalSeats(newSeats.size());
        Room savedRoom = roomRepository.save(room);

        return RoomResponse.fromEntity(savedRoom);
    }

    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng chiếu với ID: " + id));

        String name = request.getName().trim();

        // BR-CINEMA-19: Tên phòng phải là duy nhất trong cùng một rạp
        if (roomRepository.existsByNameAndCinemaIdAndIdNot(name, room.getCinema().getId(), id)) {
            throw new IllegalArgumentException("Tên phòng chiếu đã tồn tại trong rạp chiếu phim này");
        }

        String roomType = (request.getRoomType() != null && !request.getRoomType().trim().isEmpty())
                ? request.getRoomType().trim().toUpperCase()
                : room.getRoomType();

        String status = (request.getStatus() != null && !request.getStatus().trim().isEmpty())
                ? request.getStatus().trim().toUpperCase()
                : room.getStatus();

        room.setName(name);
        room.setRoomType(roomType);
        room.setStatus(status);

        Room updatedRoom = roomRepository.save(room);
        return RoomResponse.fromEntity(updatedRoom);
    }

    @Transactional
    public String deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng chiếu với ID: " + id));

        // BR-CINEMA-21: Không được xóa phòng có lịch chiếu trong tương lai
        boolean hasFutureShowtimes = showtimeRepository.existsByRoomIdAndStartTimeAfter(id, LocalDateTime.now());
        if (hasFutureShowtimes) {
            throw new IllegalArgumentException("Không thể xóa phòng chiếu đang có lịch chiếu trong tương lai");
        }

        boolean hasShowtimes = showtimeRepository.existsByRoomId(id);
        if (hasShowtimes) {
            room.setStatus("INACTIVE");
            roomRepository.save(room);
            return "Phòng chiếu đã được chuyển sang trạng thái ngưng hoạt động (INACTIVE) do có dữ liệu lịch chiếu quá khứ";
        }

        // Nếu chưa có lịch chiếu nào, xóa ghế và xóa phòng khỏi DB
        seatRepository.deleteByRoomId(id);
        roomRepository.delete(room);
        return "Xóa phòng chiếu thành công";
    }
}
