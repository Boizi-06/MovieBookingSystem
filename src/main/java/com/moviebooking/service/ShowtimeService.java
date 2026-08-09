package com.moviebooking.service;

import com.moviebooking.dto.ShowtimeRequest;
import com.moviebooking.dto.ShowtimeResponse;
import com.moviebooking.entity.Movie;
import com.moviebooking.entity.Room;
import com.moviebooking.entity.Showtime;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.RoomRepository;
import com.moviebooking.repository.ShowtimeRepository;
import com.moviebooking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ShowtimeService(ShowtimeRepository showtimeRepository,
                            MovieRepository movieRepository,
                            RoomRepository roomRepository,
                            BookingRepository bookingRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ShowtimeResponse createShowtime(ShowtimeRequest request) {
        // BR-SHOWTIME-03: Kiểm tra sự tồn tại và trạng thái của Phim
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ phim với ID: " + request.getMovieId()));
        if ("INACTIVE".equalsIgnoreCase(movie.getStatus())) {
            throw new IllegalArgumentException("Bộ phim hiện không hoạt động hoặc đã ngừng chiếu");
        }

        // BR-SHOWTIME-03: Kiểm tra sự tồn tại và trạng thái của Phòng chiếu
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng chiếu với ID: " + request.getRoomId()));
        if (!"ACTIVE".equalsIgnoreCase(room.getStatus())) {
            throw new IllegalArgumentException("Phòng chiếu không hoạt động hoặc đang bảo trì");
        }

        // BR-SHOWTIME-03: Kiểm tra trạng thái của Rạp chiếu
        if (room.getCinema() == null || !"ACTIVE".equalsIgnoreCase(room.getCinema().getStatus())) {
            throw new IllegalArgumentException("Rạp chiếu phim liên kết hiện đang tạm ngưng hoạt động");
        }

        LocalDateTime startTime = request.getStartTime();
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Thời gian bắt đầu lịch chiếu phải ở tương lai");
        }

        // BR-SHOWTIME-04: Tính thời gian kết thúc
        LocalDateTime endTime = request.getEndTime();
        if (endTime == null) {
            endTime = startTime.plusMinutes(movie.getDuration());
        } else if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Thời gian kết thúc phải lớn hơn thời gian bắt đầu");
        }

        // BR-SHOWTIME-05: Kiểm tra trùng lịch chiếu (chồng lấn thời gian cùng phòng)
        boolean isOverlapping = showtimeRepository.existsOverlappingShowtime(
                room.getId(), startTime, endTime, null);
        if (isOverlapping) {
            throw new IllegalArgumentException("Phòng chiếu đã có lịch chiếu trong khoảng thời gian này");
        }

        String status = (request.getStatus() != null && !request.getStatus().trim().isEmpty())
                ? request.getStatus().trim().toUpperCase()
                : "SCHEDULED";

        Showtime showtime = Showtime.builder()
                .movie(movie)
                .room(room)
                .startTime(startTime)
                .endTime(endTime)
                .basePrice(request.getBasePrice())
                .status(status)
                .build();

        Showtime savedShowtime = showtimeRepository.save(showtime);
        return ShowtimeResponse.fromEntity(savedShowtime);
    }

    @Transactional(readOnly = true)
    public ShowtimeResponse getShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch chiếu với ID: " + id));
        return ShowtimeResponse.fromEntity(showtime);
    }

    @Transactional(readOnly = true)
    public List<ShowtimeResponse> searchShowtimes(Long movieId, Long cinemaId, String dateStr, String status, String userRole) {
        LocalDateTime dateStart = null;
        LocalDateTime dateEnd = null;
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr.trim());
            dateStart = date.atStartOfDay();
            dateEnd = date.plusDays(1).atStartOfDay();
        }

        // Khách hàng / Guest: chỉ được xem các lịch chiếu tương lai và hoạt động
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            LocalDateTime now = LocalDateTime.now();
            if (dateStart == null) {
                dateStart = now;
            } else if (dateStart.isBefore(now)) {
                dateStart = now;
            }
            if (dateEnd != null && dateEnd.isBefore(now)) {
                return Collections.emptyList();
            }
        }

        String filterStatus = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : null;

        List<Showtime> showtimes = showtimeRepository.searchShowtimes(movieId, cinemaId, filterStatus, dateStart, dateEnd);

        // Khách hàng / Guest: loại bỏ các trạng thái CANCELLED, CLOSED, COMPLETED
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            showtimes = showtimes.stream()
                    .filter(s -> !"CANCELLED".equalsIgnoreCase(s.getStatus()) &&
                                 !"CLOSED".equalsIgnoreCase(s.getStatus()) &&
                                 !"COMPLETED".equalsIgnoreCase(s.getStatus()))
                    .collect(Collectors.toList());
        }

        return showtimes.stream()
                .map(ShowtimeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShowtimeResponse updateShowtime(Long id, ShowtimeRequest request) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch chiếu với ID: " + id));

        // BR-SHOWTIME-11: Không được cập nhật lịch chiếu đã bắt đầu hoặc hoàn thành
        if (showtime.getStartTime().isBefore(LocalDateTime.now()) || "COMPLETED".equalsIgnoreCase(showtime.getStatus())) {
            throw new IllegalArgumentException("Không thể cập nhật lịch chiếu đã bắt đầu hoặc đã hoàn thành");
        }

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bộ phim với ID: " + request.getMovieId()));
        if ("INACTIVE".equalsIgnoreCase(movie.getStatus())) {
            throw new IllegalArgumentException("Bộ phim hiện không hoạt động hoặc đã ngừng chiếu");
        }

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng chiếu với ID: " + request.getRoomId()));
        if (!"ACTIVE".equalsIgnoreCase(room.getStatus())) {
            throw new IllegalArgumentException("Phòng chiếu không hoạt động hoặc đang bảo trì");
        }

        if (room.getCinema() == null || !"ACTIVE".equalsIgnoreCase(room.getCinema().getStatus())) {
            throw new IllegalArgumentException("Rạp chiếu phim liên kết hiện đang tạm ngưng hoạt động");
        }

        LocalDateTime startTime = request.getStartTime();
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Thời gian bắt đầu lịch chiếu phải ở tương lai");
        }

        LocalDateTime endTime = request.getEndTime();
        if (endTime == null) {
            endTime = startTime.plusMinutes(movie.getDuration());
        } else if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Thời gian kết thúc phải lớn hơn thời gian bắt đầu");
        }

        // BR-SHOWTIME-12: Kiểm tra trùng lịch chiếu (loại trừ chính nó)
        boolean isOverlapping = showtimeRepository.existsOverlappingShowtime(
                room.getId(), startTime, endTime, id);
        if (isOverlapping) {
            throw new IllegalArgumentException("Phòng chiếu đã có lịch chiếu trong khoảng thời gian này");
        }

        String status = (request.getStatus() != null && !request.getStatus().trim().isEmpty())
                ? request.getStatus().trim().toUpperCase()
                : showtime.getStatus();

        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(startTime);
        showtime.setEndTime(endTime);
        showtime.setBasePrice(request.getBasePrice());
        showtime.setStatus(status);

        Showtime updated = showtimeRepository.save(showtime);
        return ShowtimeResponse.fromEntity(updated);
    }

    @Transactional
    public String deleteShowtime(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch chiếu với ID: " + id));

        // BR-SHOWTIME-18: Không được xóa lịch chiếu đã bắt đầu hoặc hoàn thành
        if (showtime.getStartTime().isBefore(LocalDateTime.now()) || "COMPLETED".equalsIgnoreCase(showtime.getStatus())) {
            throw new IllegalArgumentException("Không thể xóa lịch chiếu đã bắt đầu hoặc đã hoàn thành");
        }

        // BR-SHOWTIME-19: Lịch chiếu đã có booking phải chuyển sang CANCELLED thay vì xóa vật lý
        if (bookingRepository.existsByShowtimeId(id)) {
            showtime.setStatus("CANCELLED");
            showtimeRepository.save(showtime);

            // Tìm và hủy các đơn hàng Active của lịch chiếu này
            LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
            List<com.moviebooking.entity.Booking> bookings = bookingRepository.findActiveBookingsByShowtime(id, threshold);
            for (com.moviebooking.entity.Booking booking : bookings) {
                booking.setStatus("CANCELLED");
                bookingRepository.save(booking);
            }

            return "Chuyển trạng thái lịch chiếu sang CANCELLED và hủy các đơn đặt vé thành công";
        } else {
            showtimeRepository.delete(showtime);
            return "Xóa lịch chiếu thành công";
        }
    }
}
