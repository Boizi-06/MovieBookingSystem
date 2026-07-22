package com.moviebooking.service;

import com.moviebooking.dto.CinemaRequest;
import com.moviebooking.dto.CinemaResponse;
import com.moviebooking.entity.Cinema;
import com.moviebooking.repository.CinemaRepository;
import com.moviebooking.repository.RoomRepository;
import com.moviebooking.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final RoomRepository roomRepository;
    private final ShowtimeRepository showtimeRepository;

    @Autowired
    public CinemaService(CinemaRepository cinemaRepository,
                         RoomRepository roomRepository,
                         ShowtimeRepository showtimeRepository) {
        this.cinemaRepository = cinemaRepository;
        this.roomRepository = roomRepository;
        this.showtimeRepository = showtimeRepository;
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<CinemaResponse> getAllCinemas(String keyword, String city, org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<Cinema> cinemaPage = cinemaRepository.findAll(pageable);
        return cinemaPage.map(CinemaResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public CinemaResponse getCinemaById(Long id) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy rạp chiếu phim với ID: " + id));
        return CinemaResponse.fromEntity(cinema);
    }

    @Transactional
    public CinemaResponse createCinema(CinemaRequest request) {
        String name = request.getName().trim();

        // BR-CINEMA-03: Tên rạp phải là duy nhất trong hệ thống
        if (cinemaRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tên rạp chiếu phim đã tồn tại trong hệ thống");
        }

        // BR-CINEMA-05: Trạng thái mặc định của rạp mới là ACTIVE
        String status = (request.getStatus() != null && !request.getStatus().trim().isEmpty())
                ? request.getStatus().trim()
                : "ACTIVE";

        Cinema cinema = Cinema.builder()
                .name(name)
                .address(request.getAddress().trim())
                .city(request.getCity().trim())
                .phone(request.getPhone() != null && !request.getPhone().trim().isEmpty() ? request.getPhone().trim() : null)
                .status(status)
                .build();

        Cinema savedCinema = cinemaRepository.save(cinema);
        return CinemaResponse.fromEntity(savedCinema);
    }

    @Transactional
    public CinemaResponse updateCinema(Long id, CinemaRequest request) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy rạp chiếu phim với ID: " + id));

        String name = request.getName().trim();

        // BR-CINEMA-09: Tên rạp không được trùng với rạp khác
        if (cinemaRepository.existsByNameAndIdNot(name, id)) {
            throw new IllegalArgumentException("Tên rạp chiếu phim đã tồn tại trong hệ thống");
        }

        String status = (request.getStatus() != null && !request.getStatus().trim().isEmpty())
                ? request.getStatus().trim()
                : cinema.getStatus();

        cinema.setName(name);
        cinema.setAddress(request.getAddress().trim());
        cinema.setCity(request.getCity().trim());
        cinema.setPhone(request.getPhone() != null && !request.getPhone().trim().isEmpty() ? request.getPhone().trim() : null);
        cinema.setStatus(status);

        Cinema updatedCinema = cinemaRepository.save(cinema);
        return CinemaResponse.fromEntity(updatedCinema);
    }

    @Transactional
    public String deleteCinema(Long id) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy rạp chiếu phim với ID: " + id));

        // BR-CINEMA-15: Không được xóa rạp có lịch chiếu trong tương lai
        boolean hasFutureShowtimes = showtimeRepository.existsByRoomCinemaIdAndStartTimeAfter(id, LocalDateTime.now());
        if (hasFutureShowtimes) {
            throw new IllegalArgumentException("Không thể xóa rạp đang có lịch chiếu trong tương lai");
        }

        // BR-CINEMA-16: Rạp đã có dữ liệu phòng chiếu/lịch chiếu chuyển sang INACTIVE thay vì xóa vật lý
        boolean hasRoomsOrShowtimes = roomRepository.existsByCinemaId(id) || showtimeRepository.existsByRoomCinemaId(id);
        if (hasRoomsOrShowtimes) {
            cinema.setStatus("INACTIVE");
            cinemaRepository.save(cinema);
            return "Rạp chiếu phim đã được chuyển sang trạng thái ngưng hoạt động (INACTIVE) do có dữ liệu liên quan";
        }

        cinemaRepository.delete(cinema);
        return "Xóa rạp chiếu phim thành công";
    }
}
