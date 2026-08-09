package com.moviebooking.service.impl;

import com.moviebooking.dto.BookingRequest;
import com.moviebooking.dto.BookingResponse;
import com.moviebooking.dto.ShowtimeSeatResponse;
import com.moviebooking.entity.Booking;
import com.moviebooking.entity.Seat;
import com.moviebooking.entity.Showtime;
import com.moviebooking.entity.User;
import com.moviebooking.repository.BookingRepository;
import com.moviebooking.repository.SeatRepository;
import com.moviebooking.repository.ShowtimeRepository;
import com.moviebooking.repository.UserRepository;
import com.moviebooking.repository.PaymentRepository;
import com.moviebooking.repository.TicketRepository;
import com.moviebooking.entity.Payment;
import com.moviebooking.entity.Ticket;
import com.moviebooking.dto.PaymentResponse;
import com.moviebooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final com.moviebooking.repository.FoodComboRepository foodComboRepository;
    private final com.moviebooking.repository.BookingComboRepository bookingComboRepository;
    private final com.moviebooking.service.EmailService emailService;
    private final vn.payos.PayOS payOS;

    @org.springframework.beans.factory.annotation.Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ShowtimeRepository showtimeRepository,
                              SeatRepository seatRepository,
                              UserRepository userRepository,
                              PaymentRepository paymentRepository,
                              TicketRepository ticketRepository,
                              com.moviebooking.repository.FoodComboRepository foodComboRepository,
                              com.moviebooking.repository.BookingComboRepository bookingComboRepository,
                              com.moviebooking.service.EmailService emailService,
                              vn.payos.PayOS payOS) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.ticketRepository = ticketRepository;
        this.foodComboRepository = foodComboRepository;
        this.bookingComboRepository = bookingComboRepository;
        this.emailService = emailService;
        this.payOS = payOS;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeSeatResponse> getSeatsByShowtimeId(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch chiếu với ID: " + showtimeId));

        List<Seat> seats = seatRepository.findByRoomIdOrderBySeatRowAscSeatNumberAsc(showtime.getRoom().getId());

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        List<Booking> activeBookings = bookingRepository.findActiveBookingsByShowtime(showtimeId, threshold);

        Set<Long> bookedSeatIds = new HashSet<>();
        Set<Long> heldSeatIds = new HashSet<>();

        for (Booking booking : activeBookings) {
            if ("PENDING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
                heldSeatIds.addAll(booking.getSeats().stream().map(Seat::getId).collect(Collectors.toSet()));
            } else {
                bookedSeatIds.addAll(booking.getSeats().stream().map(Seat::getId).collect(Collectors.toSet()));
            }
        }

        List<ShowtimeSeatResponse> responseList = new ArrayList<>();
        for (Seat seat : seats) {
            String dynamicStatus = "AVAILABLE";
            if (!"ACTIVE".equalsIgnoreCase(seat.getStatus())) {
                dynamicStatus = "MAINTENANCE";
            } else if (bookedSeatIds.contains(seat.getId())) {
                dynamicStatus = "BOOKED";
            } else if (heldSeatIds.contains(seat.getId())) {
                dynamicStatus = "HOLD";
            }

            BigDecimal price = showtime.getBasePrice().multiply(seat.getPriceMultiplier());

            responseList.add(ShowtimeSeatResponse.builder()
                    .seatId(seat.getId())
                    .seatCode(seat.getSeatCode())
                    .seatNumber(seat.getSeatNumber())
                    .seatRow(seat.getSeatRow())
                    .seatType(seat.getSeatType())
                    .price(price)
                    .status(dynamicStatus)
                    .build());
        }

        return responseList;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với email: " + userEmail));

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch chiếu với ID: " + request.getShowtimeId()));

        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Không thể đặt vé cho suất chiếu đã diễn ra");
        }

        List<Seat> seats = seatRepository.findAllById(request.getSeatIds());
        if (seats.size() != request.getSeatIds().size()) {
            throw new IllegalArgumentException("Một hoặc nhiều ghế chọn không tồn tại");
        }

        for (Seat seat : seats) {
            if (!seat.getRoom().getId().equals(showtime.getRoom().getId())) {
                throw new IllegalArgumentException("Ghế " + seat.getSeatCode() + " không thuộc phòng chiếu này");
            }
            if (!"ACTIVE".equalsIgnoreCase(seat.getStatus())) {
                throw new IllegalArgumentException("Ghế " + seat.getSeatCode() + " hiện đang bảo trì hoặc ngừng hoạt động");
            }
        }

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        List<Booking> activeBookings = bookingRepository.findActiveBookingsByShowtime(request.getShowtimeId(), threshold);

        for (Booking booking : activeBookings) {
            for (Seat s : booking.getSeats()) {
                if (request.getSeatIds().contains(s.getId())) {
                    throw new IllegalArgumentException("Ghế " + s.getSeatCode() + " đã được đặt hoặc đang được giữ bởi người khác");
                }
            }
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Seat seat : seats) {
            BigDecimal seatPrice = showtime.getBasePrice().multiply(seat.getPriceMultiplier());
            totalPrice = totalPrice.add(seatPrice);
        }

        List<com.moviebooking.entity.BookingCombo> bookingCombosToSave = new ArrayList<>();
        List<BookingResponse.BookingComboResponse> comboResponses = new ArrayList<>();

        if (request.getCombos() != null && !request.getCombos().isEmpty()) {
            for (BookingRequest.ComboItemRequest item : request.getCombos()) {
                if (item.getComboId() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                    com.moviebooking.entity.FoodCombo combo = foodComboRepository.findById(item.getComboId())
                            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Combo bỏng nước với ID: " + item.getComboId()));
                    BigDecimal comboSubtotal = combo.getPrice().multiply(new BigDecimal(item.getQuantity()));
                    totalPrice = totalPrice.add(comboSubtotal);

                    comboResponses.add(BookingResponse.BookingComboResponse.builder()
                            .comboId(combo.getId())
                            .comboName(combo.getName())
                            .quantity(item.getQuantity())
                            .price(combo.getPrice())
                            .build());
                }
            }
        }

        String bookingCode = "BKG" + System.currentTimeMillis();

        Booking booking = Booking.builder()
                .bookingCode(bookingCode)
                .user(user)
                .showtime(showtime)
                .seats(new HashSet<>(seats))
                .totalPrice(totalPrice)
                .status("PENDING_PAYMENT")
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        if (request.getCombos() != null && !request.getCombos().isEmpty()) {
            for (BookingRequest.ComboItemRequest item : request.getCombos()) {
                if (item.getComboId() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                    com.moviebooking.entity.FoodCombo combo = foodComboRepository.findById(item.getComboId()).orElse(null);
                    if (combo != null) {
                        com.moviebooking.entity.BookingComboId bcId = com.moviebooking.entity.BookingComboId.builder()
                                .bookingId(savedBooking.getId())
                                .comboId(combo.getId())
                                .build();
                        com.moviebooking.entity.BookingCombo bc = com.moviebooking.entity.BookingCombo.builder()
                                .id(bcId)
                                .booking(savedBooking)
                                .combo(combo)
                                .quantity(item.getQuantity())
                                .price(combo.getPrice())
                                .build();
                        bookingComboRepository.save(bc);
                    }
                }
            }
        }

        List<String> seatCodes = seats.stream().map(Seat::getSeatCode).collect(Collectors.toList());

        return BookingResponse.builder()
                .id(savedBooking.getId())
                .bookingCode(savedBooking.getBookingCode())
                .showtimeId(showtime.getId())
                .movieTitle(showtime.getMovie().getTitle())
                .cinemaName(showtime.getRoom().getCinema().getName())
                .roomName(showtime.getRoom().getName())
                .startTime(showtime.getStartTime())
                .seatCodes(seatCodes)
                .totalPrice(savedBooking.getTotalPrice())
                .status(savedBooking.getStatus())
                .createdAt(savedBooking.getCreatedAt())
                .expiresAt(savedBooking.getCreatedAt().plusMinutes(5))
                .comboItems(comboResponses)
                .build();
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 60000)
    public void cancelExpiredBookings() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        List<Booking> expiredBookings = bookingRepository.findByStatusAndCreatedAtBefore("PENDING_PAYMENT", threshold);
        for (Booking booking : expiredBookings) {
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingHistory(String userEmail) {
        List<Booking> bookings = bookingRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
        List<BookingResponse> responseList = new ArrayList<>();
        for (Booking booking : bookings) {
            List<String> seatCodes = booking.getSeats().stream()
                    .map(Seat::getSeatCode)
                    .collect(Collectors.toList());

            List<Ticket> tickets = ticketRepository.findByBookingId(booking.getId());
            List<String> ticketCodes = tickets.stream()
                    .map(Ticket::getTicketCode)
                    .collect(Collectors.toList());

            List<com.moviebooking.entity.BookingCombo> bookingCombos = bookingComboRepository.findByBookingId(booking.getId());
            List<BookingResponse.BookingComboResponse> comboItems = bookingCombos.stream()
                    .map(bc -> BookingResponse.BookingComboResponse.builder()
                            .comboId(bc.getCombo().getId())
                            .comboName(bc.getCombo().getName())
                            .quantity(bc.getQuantity())
                            .price(bc.getPrice())
                            .build())
                    .collect(Collectors.toList());

            responseList.add(BookingResponse.builder()
                    .id(booking.getId())
                    .bookingCode(booking.getBookingCode())
                    .showtimeId(booking.getShowtime().getId())
                    .movieTitle(booking.getShowtime().getMovie().getTitle())
                    .cinemaName(booking.getShowtime().getRoom().getCinema().getName())
                    .roomName(booking.getShowtime().getRoom().getName())
                    .startTime(booking.getShowtime().getStartTime())
                    .seatCodes(seatCodes)
                    .totalPrice(booking.getTotalPrice())
                    .status(booking.getStatus())
                    .createdAt(booking.getCreatedAt())
                    .expiresAt(booking.getCreatedAt().plusMinutes(5))
                    .ticketCodes(ticketCodes)
                    .comboItems(comboItems)
                    .build());
        }
        return responseList;
    }

    @org.springframework.beans.factory.annotation.Value("${vietqr.bankId:MB}")
    private String vietqrBankId;

    @org.springframework.beans.factory.annotation.Value("${vietqr.accountNo:0999999999}")
    private String vietqrAccountNo;

    @org.springframework.beans.factory.annotation.Value("${vietqr.accountName:CHỦ TÀI KHOẢN}")
    private String vietqrAccountName;

    @Override
    @Transactional(readOnly = true)
    public com.moviebooking.dto.PaymentResponse getPaymentDetails(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt vé"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("Đơn đặt vé này không thuộc về bạn");
        }

        if (!"PENDING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Đơn hàng này không ở trạng thái chờ thanh toán");
        }

        if (booking.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalArgumentException("Đơn hàng này đã hết hạn thanh toán");
        }

        String encodedAccountName = "";
        String encodedMemo = "";
        try {
            encodedAccountName = java.net.URLEncoder.encode(vietqrAccountName, java.nio.charset.StandardCharsets.UTF_8.toString());
            encodedMemo = java.net.URLEncoder.encode(booking.getBookingCode(), java.nio.charset.StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            encodedAccountName = vietqrAccountName;
            encodedMemo = booking.getBookingCode();
        }

        String qrCodeUrl = String.format("https://img.vietqr.io/image/%s-%s-compact2.png?amount=%s&addInfo=%s&accountName=%s",
                vietqrBankId.trim(), vietqrAccountNo.trim(), booking.getTotalPrice().toPlainString(), encodedMemo, encodedAccountName);

        String payosCheckoutUrl = null;
        Long payosOrderCode = booking.getId();
        try {
            long amountLong = booking.getTotalPrice().longValue();
            String desc = "BKG" + booking.getId();
            if (desc.length() > 25) desc = desc.substring(0, 25);

            vn.payos.model.v2.paymentRequests.PaymentLinkItem item = vn.payos.model.v2.paymentRequests.PaymentLinkItem.builder()
                    .name("Ve phim " + booking.getShowtime().getMovie().getTitle())
                    .quantity(booking.getSeats().size())
                    .price(amountLong)
                    .build();

            vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest paymentData = vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest.builder()
                    .orderCode(payosOrderCode)
                    .amount(amountLong)
                    .description(desc)
                    .returnUrl(frontendUrl + "/booking-success?bookingId=" + booking.getId())
                    .cancelUrl(frontendUrl + "/checkout?bookingId=" + booking.getId())
                    .items(java.util.Collections.singletonList(item))
                    .build();

            vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse checkoutData = payOS.paymentRequests().create(paymentData);
            if (checkoutData != null) {
                payosCheckoutUrl = checkoutData.getCheckoutUrl();
                if (checkoutData.getQrCode() != null && !checkoutData.getQrCode().isEmpty()) {
                    qrCodeUrl = checkoutData.getQrCode();
                }
            }
        } catch (Exception e) {
            System.out.println("[PAYOS NOTICE] PayOS chưa cấu hình hoặc chạy fallback VietQR: " + e.getMessage());
        }

        LocalDateTime expiresAt = booking.getCreatedAt().plusMinutes(5);
        long remainingSeconds = java.time.Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
        if (remainingSeconds < 0) remainingSeconds = 0;

        List<com.moviebooking.entity.BookingCombo> bookingCombos = bookingComboRepository.findByBookingId(booking.getId());
        List<BookingResponse.BookingComboResponse> comboItems = bookingCombos.stream()
                .map(bc -> BookingResponse.BookingComboResponse.builder()
                        .comboId(bc.getCombo().getId())
                        .comboName(bc.getCombo().getName())
                        .quantity(bc.getQuantity())
                        .price(bc.getPrice())
                        .build())
                .collect(Collectors.toList());

        return com.moviebooking.dto.PaymentResponse.builder()
                .bookingCode(booking.getBookingCode())
                .showtimeId(booking.getShowtime().getId())
                .amount(booking.getTotalPrice())
                .bankId(vietqrBankId)
                .accountNo(vietqrAccountNo)
                .accountName(vietqrAccountName)
                .transferMemo(booking.getBookingCode())
                .qrCodeUrl(qrCodeUrl)
                .checkoutUrl(payosCheckoutUrl)
                .orderCode(payosOrderCode)
                .createdAt(booking.getCreatedAt())
                .expiresAt(expiresAt)
                .remainingSeconds(remainingSeconds)
                .comboItems(comboItems)
                .build();
    }

    @Override
    @Transactional
    public boolean processPayOSWebhook(String rawJson) {
        if (rawJson == null || rawJson.trim().isEmpty()) return false;

        System.out.println("=================================================");
        System.out.println("[PAYOS WEBHOOK RAW RECEIVED] " + rawJson);
        System.out.println("=================================================");

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            vn.payos.model.webhooks.Webhook webhook = mapper.readValue(rawJson, vn.payos.model.webhooks.Webhook.class);

            vn.payos.model.webhooks.WebhookData verifiedData = null;
            try {
                verifiedData = payOS.webhooks().verify(webhook);
            } catch (Exception ve) {
                System.out.println("[PAYOS WEBHOOK WARN] Signature verification note: " + ve.getMessage());
                if (webhook != null) verifiedData = webhook.getData();
            }

            if (verifiedData != null && verifiedData.getOrderCode() != null) {
                Long orderCode = verifiedData.getOrderCode();
                System.out.println("[PAYOS WEBHOOK SUCCESS] Received orderCode: " + orderCode + ", amount: " + verifiedData.getAmount());

                java.util.Optional<Booking> optionalBooking = bookingRepository.findById(orderCode);
                if (optionalBooking.isPresent()) {
                    Booking booking = optionalBooking.get();
                    if ("PENDING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
                        fulfillPaymentForBooking(booking, "PAYOS-" + (verifiedData.getPaymentLinkId() != null ? verifiedData.getPaymentLinkId() : System.currentTimeMillis()));
                        System.out.println("[PAYOS WEBHOOK] ✅ ĐÃ TỰ ĐỘNG XÁC NHẬN THANH TOÁN VÀ XUẤT VÉ CHO ĐƠN: " + booking.getBookingCode());
                        return true;
                    } else {
                        return true; // Already paid
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[PAYOS WEBHOOK PARSE ERROR] " + e.getMessage());
        }
        return false;
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt vé với ID: " + bookingId));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("Đơn đặt vé này không thuộc về bạn");
        }

        if (!"PENDING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Chỉ có thể hủy đơn đặt vé ở trạng thái chờ thanh toán");
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public String getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt vé"));
        return booking.getStatus();
    }

    @Override
    @Transactional
    public boolean processSepayWebhookRaw(String rawJson) {
        if (rawJson == null || rawJson.trim().isEmpty()) return false;

        System.out.println("=================================================");
        System.out.println("[SEPAY PROCESS RAW] Received Raw JSON: " + rawJson);
        System.out.println("=================================================");

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("BKG-?[A-Z0-9]+", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(rawJson);

        if (!matcher.find()) {
            System.out.println("[SEPAY PROCESS RAW] ❌ Không tìm thấy chuỗi BKG trong raw JSON.");
            return false;
        }

        String rawMatched = matcher.group().trim();
        String cleanMatched = rawMatched.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();

        System.out.println("[SEPAY PROCESS RAW] 🔍 Tìm thấy: " + rawMatched + " -> Mã chuẩn hóa: " + cleanMatched);
        java.util.Optional<Booking> optionalBooking = bookingRepository.findByNormalizedBookingCode(cleanMatched);
        if (optionalBooking.isEmpty()) {
            System.out.println("[SEPAY PROCESS RAW] ❌ Mã đơn hàng không có trong Database: " + cleanMatched);
            return false;
        }

        Booking booking = optionalBooking.get();
        if (!"PENDING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            System.out.println("[SEPAY PROCESS RAW] ℹ️ Đơn hàng không ở trạng thái PENDING_PAYMENT. Trạng thái hiện tại: " + booking.getStatus());
            return true; // Đã xử lý thanh toán trước đó
        }

        fulfillPaymentForBooking(booking, "SEPAY-" + System.currentTimeMillis());
        System.out.println("=================================================");
        System.out.println("[SEPAY PROCESS RAW] ✅ >>> ĐÃ TỰ ĐỘNG XÁC NHẬN THANH TOÁN VÀ XUẤT VÉ THÀNH CÔNG CHO ĐƠN: " + booking.getBookingCode() + " <<<");
        System.out.println("=================================================");
        return true;
    }

    @Override
    @Transactional
    public boolean processSepayWebhook(com.moviebooking.dto.SepayWebhookPayload payload) {
        if (payload == null) return false;

        String content = "";
        if (payload.getTransactionContent() != null) {
            content += payload.getTransactionContent() + " ";
        }
        if (payload.getBody() != null) {
            content += payload.getBody() + " ";
        }
        if (payload.getCode() != null) {
            content += payload.getCode();
        }

        System.out.println("[SEPAY PROCESS] Chuỗi nội dung tìm kiếm: " + content);

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("BKG-?[A-Z0-9]+", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(content);

        if (!matcher.find()) {
            System.out.println("[SEPAY PROCESS] Không tìm thấy chuỗi BKG trong nội dung chuyển khoản.");
            return false;
        }

        String matchedCode = matcher.group().toUpperCase();
        if (!matchedCode.contains("-") && matchedCode.length() > 3) {
            matchedCode = "BKG-" + matchedCode.substring(3);
        }

        System.out.println("[SEPAY PROCESS] Tìm thấy mã đơn hàng: " + matchedCode);
        java.util.Optional<Booking> optionalBooking = bookingRepository.findByBookingCode(matchedCode);
        if (optionalBooking.isEmpty()) {
            System.out.println("[SEPAY PROCESS] Mã đơn hàng không có trong Database: " + matchedCode);
            return false;
        }

        Booking booking = optionalBooking.get();
        if (!"PENDING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            System.out.println("[SEPAY PROCESS] Đơn hàng không ở trạng thái PENDING_PAYMENT. Trạng thái hiện tại: " + booking.getStatus());
            return true; // Đã xử lý thanh toán trước đó
        }

        BigDecimal amountIn = payload.getAmountIn() != null ? payload.getAmountIn() : BigDecimal.ZERO;
        if (amountIn.compareTo(booking.getTotalPrice()) < 0) {
            System.out.println("[SEPAY PROCESS] Số tiền chuyển (" + amountIn + ") nhỏ hơn giá vé (" + booking.getTotalPrice() + ")");
            return false;
        }

        fulfillPaymentForBooking(booking, "SEPAY-" + (payload.getReferenceNumber() != null ? payload.getReferenceNumber() : System.currentTimeMillis()));
        System.out.println("[SEPAY PROCESS] >>> ĐÃ TỰ ĐỘNG XÁC NHẬN THANH TOÁN VÀ XUẤT VÉ THÀNH CÔNG CHO ĐƠN: " + matchedCode + " <<<");
        return true;
    }

    @Override
    @Transactional
    public BookingResponse processPaymentMockSuccess(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt vé với mã: " + bookingCode));

        if (!"PENDING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Đơn hàng đã được xử lý thanh toán hoặc đã hủy");
        }

        if (booking.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalArgumentException("Đơn hàng đã hết hạn thanh toán và không thể xử lý");
        }

        return fulfillPaymentForBooking(booking, "TXN-" + System.currentTimeMillis());
    }

    private BookingResponse fulfillPaymentForBooking(Booking booking, String txnId) {
        Payment payment = Payment.builder()
                .booking(booking)
                .transactionId(txnId)
                .paymentMethod("BANK_TRANSFER")
                .amount(booking.getTotalPrice())
                .status("PAID")
                .build();
        paymentRepository.save(payment);

        booking.setStatus("PAID");
        Booking savedBooking = bookingRepository.save(booking);

        List<String> ticketCodes = new ArrayList<>();
        for (Seat seat : booking.getSeats()) {
            String ticketCode = "TKT-" + UUID.randomUUID().toString().toUpperCase().replaceAll("-", "").substring(0, 12);
            BigDecimal price = booking.getShowtime().getBasePrice().multiply(seat.getPriceMultiplier());
            Ticket ticket = Ticket.builder()
                    .ticketCode(ticketCode)
                    .booking(booking)
                    .seat(seat)
                    .price(price)
                    .status("ACTIVE")
                    .build();
            ticketRepository.save(ticket);
            ticketCodes.add(ticketCode);
        }

        List<String> seatCodes = booking.getSeats().stream()
                .map(Seat::getSeatCode)
                .collect(Collectors.toList());

        // Tự động gửi Email Cuống Vé HTML về Gmail khách hàng
        try {
            new Thread(() -> emailService.sendTicketEmail(savedBooking)).start();
        } catch (Exception e) {
            System.err.println("[EMAIL ERROR] Lỗi khởi chạy luồng gửi mail cuống vé: " + e.getMessage());
        }

        return BookingResponse.builder()
                .id(savedBooking.getId())
                .bookingCode(savedBooking.getBookingCode())
                .showtimeId(booking.getShowtime().getId())
                .movieTitle(booking.getShowtime().getMovie().getTitle())
                .cinemaName(booking.getShowtime().getRoom().getCinema().getName())
                .roomName(booking.getShowtime().getRoom().getName())
                .startTime(booking.getShowtime().getStartTime())
                .seatCodes(seatCodes)
                .totalPrice(savedBooking.getTotalPrice())
                .status(savedBooking.getStatus())
                .createdAt(savedBooking.getCreatedAt())
                .expiresAt(savedBooking.getCreatedAt().plusMinutes(5))
                .ticketCodes(ticketCodes)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<BookingResponse> getAllBookingsAdmin(
            String search,
            String status,
            Long movieId,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate,
            org.springframework.data.domain.Pageable pageable) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String cleanStatus = (status != null && !status.trim().isEmpty()) ? status.trim() : null;

        java.time.LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        java.time.LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

        org.springframework.data.domain.Page<Booking> pageResult = bookingRepository.findAllAdminBookings(
                cleanSearch, cleanStatus, movieId, startDateTime, endDateTime, pageable);

        return pageResult.map(booking -> {
            List<String> seatCodes = booking.getSeats().stream()
                    .map(Seat::getSeatCode)
                    .collect(Collectors.toList());

            List<Ticket> tickets = ticketRepository.findByBookingId(booking.getId());
            List<String> ticketCodes = tickets.stream()
                    .map(Ticket::getTicketCode)
                    .collect(Collectors.toList());

            return BookingResponse.builder()
                    .id(booking.getId())
                    .bookingCode(booking.getBookingCode())
                    .showtimeId(booking.getShowtime().getId())
                    .movieTitle(booking.getShowtime().getMovie().getTitle())
                    .cinemaName(booking.getShowtime().getRoom().getCinema().getName())
                    .roomName(booking.getShowtime().getRoom().getName())
                    .startTime(booking.getShowtime().getStartTime())
                    .seatCodes(seatCodes)
                    .totalPrice(booking.getTotalPrice())
                    .status(booking.getStatus())
                    .createdAt(booking.getCreatedAt())
                    .expiresAt(booking.getCreatedAt().plusMinutes(5))
                    .ticketCodes(ticketCodes)
                    .build();
        });
    }
}
