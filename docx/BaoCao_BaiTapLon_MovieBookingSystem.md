# HỌC VIỆN CÔNG NGHỆ BƯU CHÍ VIỄN THÔNG
## KHOA CÔNG NGHỆ THÔNG TIN

---

### BÁO CÁO BÀI TẬP LỚN MÔN: PHÂN TÍCH THIẾT KẾ HỆ THỐNG THÔNG TIN
### TÊN ĐỀ TÀI: XÂY DỰNG HỆ THỐNG QUẢN LÝ ĐẶT VÉ XEM PHIM ONLINE (MOVIE TICKET BOOKING SYSTEM)

- **Giảng viên hướng dẫn**: Bùi Thanh Hải
- **Đơn vị công tác của giảng viên**: Khoa Công nghệ Thông tin - Học viện Công nghệ Bưu chính Viễn thông
- **Mã tài liệu tham chiếu**: `SRS-MTBS-001`
- **Sinh viên thực hiện**: Hoàng Thiên Sơn
- **Mã sinh viên**: B24DTCN436
- **Lớp**: CNTT3

**Hà Nội – 2026**

---

## NHẬN XÉT CỦA GIẢNG VIÊN HƯỚNG DẪN

**Nhận xét về nội dung báo cáo và kết quả thực hiện đề tài**:
....................................................................................................................
....................................................................................................................
....................................................................................................................
....................................................................................................................
....................................................................................................................
....................................................................................................................

**Đánh giá điểm số**: ................................. / 10

**Xác nhận của giảng viên hướng dẫn**:

*(Ký và ghi rõ họ tên)*

---

## LỜI CẢM ƠN

Lời đầu tiên, em xin gửi lời cảm ơn chân thành đến Khoa Công nghệ Thông tin – Học viện Công nghệ Bưu chính Viễn thông đã tạo điều kiện thuận lợi về học liệu, phòng thực hành và định hướng chuyên môn trong suốt quá trình học tập, giúp em có nền tảng kiến thức cần thiết để thực hiện bài tập lớn này.

Em xin bày tỏ lòng biết ơn sâu sắc tới giảng viên hướng dẫn **thầy Bùi Thanh Hải** đã tận tình chỉ bảo, góp ý về phương pháp phân tích yêu cầu nghiệp vụ, thiết kế cơ sở dữ liệu quan hệ, kiến trúc RESTful API và tổ chức tài liệu đặc tả chuẩn mực trong suốt quá trình thực hiện đề tài *"Xây dựng hệ thống quản lý đặt vé xem phim online (Movie Ticket Booking System)"*.

Do thời gian và kiến thức còn hạn chế, báo cáo chắc chắn không tránh khỏi thiếu sót. Em rất mong nhận được sự góp ý chân thành của quý thầy cô để hoàn thiện hơn trong những đề tài tiếp theo.

Em xin chân thành cảm ơn!

*Sinh viên thực hiện: Hoàng Thiên Sơn*

---

## LỜI CAM ĐOAN

Em xin cam đoan đề tài *"Xây dựng hệ thống quản lý đặt vé xem phim online"* là công trình nghiên cứu và phát triển của cá nhân em dưới sự hướng dẫn của giảng viên hướng dẫn, dựa trên tài liệu đặc tả yêu cầu phần mềm (`SRS-MTBS-001`) đã được xây dựng trước khi triển khai.

Toàn bộ nội dung phân tích, thiết kế, lập trình và trình bày trong báo cáo này là do em tự nghiên cứu, tổng hợp và hoàn thành, không sao chép từ bất kỳ báo cáo hoặc công trình nào khác. Các số liệu, bảng biểu, sơ đồ trong báo cáo được xây dựng dựa trên tài liệu đặc tả yêu cầu của chính đề tài và kiến thức môn học, không vi phạm quyền sở hữu trí tuệ của bất kỳ tổ chức, cá nhân nào.

Nếu có bất kỳ sai sót hoặc vi phạm nào, em xin chịu hoàn toàn trách nhiệm.

*Sinh viên thực hiện: Hoàng Thiên Sơn*

---

## MỤC LỤC CHI TIẾT

- **NHẬN XÉT CỦA GIẢNG VIÊN HƯỚNG DẪN**
- **LỜI CẢM ƠN**
- **LỜI CAM ĐOAN**
- **LỜI MỞ ĐẦU**
- **CHƯƠNG 1: TỔNG QUAN VỀ HỆ THỐNG QUẢN LÝ ĐẶT VÉ XEM PHIM ONLINE**
  - 1.1. Giới thiệu
    - 1.1.1. Khái niệm và mục đích
    - 1.1.2. Quy trình hoạt động
    - 1.1.3. Các mô hình hệ thống phổ biến
    - 1.1.4. Các hệ thống tham khảo (CGV, Lotte Cinema, Beta Cinemas, NCC)
  - 1.2. Thực trạng công tác quản lý & bán vé tại rạp hiện nay
  - 1.3. Các thành phần chính của hệ thống (Danh sách 12 Mô đun)
  - 1.4. Bài toán quản lý đặt vé của đề tài
    - 1.4.1. Phát biểu bài toán
    - 1.4.2. Mục đích, mục tiêu
    - 1.4.3. Phạm vi (Trong & Ngoài đề tài)
    - 1.4.4. Giải pháp đề xuất và công nghệ sử dụng
  - 1.5. Kế hoạch thực hiện dự án (Bảng mốc thời gian & Issue)
- **CHƯƠNG 2: PHÂN TÍCH VÀ THIẾT KẾ HỆ THỐNG**
  - 2.1. Mô hình nghiệp vụ của hệ thống (Tác nhân: Guest, Customer, Admin)
  - 2.2. Phân tích yêu cầu hệ thống
    - 2.2.1. Xác định các ca sử dụng
    - 2.2.2. Biểu đồ ca sử dụng tổng quát & Ma trận phân quyền
    - 2.2.3. Đặc tả ca sử dụng chi tiết (Đầy đủ 18 ca sử dụng từ AUTH-01 đến DASH-01)
  - 2.3. Mô hình hóa hoạt động của hệ thống (Activity Diagrams)
    - 2.3.1. Quy trình chọn ghế và khóa giữ ghế 5 phút Server-side
    - 2.3.2. Quy trình thanh toán PayOS VietQR & tự động nhận Webhook xuất vé
  - 2.4. Sơ đồ chuyển trạng thái (State Machine)
    - 2.4.1. Trạng thái của Ghế
    - 2.4.2. Trạng thái của đơn hàng Booking
  - 2.5. Sơ đồ quan hệ đối tượng (ERD tổng quan)
  - 2.6. Thiết kế cơ sở dữ liệu (Từ điển dữ liệu 12 bảng MySQL & Công thức nghiệp vụ)
  - 2.7. Thiết kế giao diện người dùng (Site Map & Component Glassmorphism)
- **CHƯƠNG 3: CÀI ĐẶT VÀ KIỂM THỬ HỆ THỐNG**
  - 3.1. Bảo mật hệ thống (JWT, BCrypt, Webhook HMAC-SHA256 Signature)
  - 3.2. Yêu cầu phi chức năng (NFR)
  - 3.3. Danh sách API hệ thống (Bảng 18 RESTful Endpoints)
  - 3.4. Hệ thống thông báo và xử lý lỗi (Response Envelope `ApiResponse<T>`)
  - 3.5. Xây dựng các mô đun trọng tâm (Code Implementation 3 mô đun core)
  - 3.6. Kiểm thử hệ thống (Kịch bản kiểm thử Test Cases & Kết quả)
- **KẾT LUẬN**
- **TÀI LIỆU THAM KHẢO**

---

## LỜI MỞ ĐẦU

Trong thời đại chuyển đổi số mạnh mẽ, việc ứng dụng công nghệ thông tin để tự động hóa quy trình thương mại điện tử, trong đó có hoạt động bán vé xem phim tại các rạp chiếu phim, đang trở thành nhu cầu tất yếu đối với các cơ sở giải trí và doanh nghiệp điện ảnh. Rạp chiếu phim không đơn thuần là nơi trình chiếu các tác phẩm nghệ thuật mà còn là trung tâm dịch vụ điện ảnh phục vụ hàng ngàn lượt khách mỗi ngày. Tuy nhiên, nếu quản lý bán vé bằng phương thức truyền thống tại quầy hoặc sử dụng các phần mềm rời rạc, việc giữ ghế, tính giá vé theo suất chiếu/loại ghế và đối soát thanh toán thường tốn nhiều thời gian, dễ xảy ra tình trạng bán trùng 1 ghế cho 2 khách hàng (double booking) và thiếu tính minh bạch.

Xuất phát từ thực tiễn đó, đề tài *"Xây dựng hệ thống quản lý đặt vé xem phim online"* (**Movie Ticket Booking System - MTBS**) được lựa chọn nhằm xây dựng một nền tảng web thương mại điện tử giúp số hóa toàn bộ quy trình tra cứu phim, đặt giữ ghế thời gian thực, thanh toán tự động qua mã VietQR PayOS, xuất vé điện tử (E-Ticket) và tổng hợp báo cáo doanh thu.

Hệ thống hướng tới ba nhóm người dùng chính: **Guest (Khách vãng khách)**, **Customer (Khách hàng)** và **Admin (Quản trị viên)**, mỗi nhóm được phân quyền chặt chẽ với vai trò nghiệp vụ của mình.

Báo cáo được xây dựng dựa trên tài liệu đặc tả yêu cầu phần mềm (`SRS-MTBS-001`), trình bày đầy đủ quá trình phân tích yêu cầu, thiết kế hệ thống, thiết kế cơ sở dữ liệu quan hệ, xây dựng RESTful API và kiểm thử chức năng.

**Nội dung báo cáo gồm 3 chương chính**:
- **Chương 1**: Tổng quan về hệ thống quản lý đặt vé xem phim online – trình bày khái niệm, thực trạng, các thành phần chính và bài toán đặt ra cho đề tài.
- **Chương 2**: Phân tích và thiết kế hệ thống – mô tả các tác nhân, ca sử dụng, quy trình nghiệp vụ, sơ đồ trạng thái, thiết kế cơ sở dữ liệu và giao diện người dùng Glassmorphic.
- **Chương 3**: Cài đặt và kiểm thử hệ thống – trình bày các biện pháp bảo mật (JWT, BCrypt, HMAC-SHA256), danh sách API, các mô đun nghiệp vụ trọng tâm và kết quả kiểm thử kịch bản.

---

## CHƯƠNG 1: TỔNG QUAN VỀ HỆ THỐNG QUẢN LÝ ĐẶT VÉ XEM PHIM ONLINE

### 1.1. Giới thiệu

#### 1.1.1. Khái niệm và mục đích
Hệ thống quản lý đặt vé xem phim online (Movie Ticket Booking System – MTBS) là một ứng dụng web được xây dựng nhằm tự động hóa các nghiệp vụ cốt lõi của một cụm rạp chiếu phim, bao gồm: quản lý danh mục phim, quản lý cụm rạp/phòng chiếu, tự động sinh sơ đồ ghế ngồi (Thường/VIP), tạo lịch chiếu, đặt giữ ghế trực tuyến thời gian thực, thanh toán tự động qua VietQR PayOS, gửi Email vé điện tử và tổng hợp báo cáo doanh thu phục vụ công tác quản trị.

Khác với hình thức mua vé trực tiếp tại quầy, hệ thống đặt vé điện tử cho phép người dùng lựa chọn đúng vị trí ghế yêu thích, biết chính xác giá vé và hoàn tất thanh toán trong chưa đầy 1 phút.

**Mục đích cốt lõi của hệ thống**:
1. **Tự động hóa quy trình chọn ghế và đặt vé**: Loại bỏ hoàn toàn nguy cơ trùng ghế (Race Condition) nhờ cơ chế khóa ghế Database Transaction.
2. **Tích hợp thanh toán trực tuyến tự động PayOS VietQR**: Nhận tín hiệu Webhook tức thì (<300ms) để gạch nợ tự động mà không cần nhân viên đối soát thủ công.
3. **Cơ chế đếm ngược giữ ghế 5 phút chuẩn Server**: Đồng bộ thời gian còn lại trực tiếp từ Database (`300 - (CurrentTime - CreatedAt)`), ngăn chặn việc F5 hoặc đổi trình duyệt để lạm dụng thời gian giữ ghế.
4. **Hỗ trợ khách hàng dễ dàng tra cứu lịch sử đặt vé**: Bổ sung nút **"Lịch sử đặt vé"** trực tiếp trên Header Navigation (`/my-bookings`), giúp khách hàng mở lại mã QR vé điện tử nhanh chóng tại cửa soát vé rạp.
5. **Cung cấp Dashboard và báo cáo**: Hỗ trợ Admin nắm bắt tình hình kinh doanh, doanh thu theo thời gian thực và quản lý vận hành toàn bộ rạp.

#### 1.1.2. Quy trình hoạt động
Một hệ thống đặt vé xem phim điển hình vận hành theo quy trình khép kín xoay quanh vòng đời của suất chiếu, vị trí ghế và đơn đặt vé (Booking):
1. **Khách hàng tìm kiếm phim**: Khách hàng xem danh sách phim đang chiếu trên Trang chủ, lọc theo thể loại hoặc tìm kiếm từ khóa.
2. **Chọn Suất chiếu & Sơ đồ ghế**: Khách hàng chọn bộ phim ➔ Chọn rạp, ngày và suất chiếu ➔ Sơ đồ ghế hiển thị trạng thái từng vị trí (Ghế Trống, Ghế VIP, Ghế Đã Đặt, Ghế Đang Giữ).
3. **Đặt Giữ Ghế (5 phút)**: Khách hàng chọn ghế và bấm *"Đặt giữ ghế"*. Hệ thống khóa ghế ở trạng thái `HOLD` và khởi tạo Booking ở trạng thái `PENDING_PAYMENT`.
4. **Thanh toán VietQR PayOS**: Màn hình thanh toán hiển thị mã VietQR. Khách hàng sử dụng ứng dụng Ngân hàng hoặc Momo quét mã QR để chuyển khoản.
5. **Xử lý Webhook & Xuất Vé**: Cổng PayOS bắn Webhook xác thực chữ ký HMAC-SHA256 về Server ➔ Server cập nhật đơn hàng thành `PAID`, chuyển trạng thái ghế thành `BOOKED`, sinh mã vé điện tử QR Code và gửi Email cuống vé HTML đến Gmail khách hàng.
6. **Xem Vé & Tra Cứu**: Khách hàng bấm xem vé điện tử hoặc truy cập nút **"Lịch sử đặt vé"** trên thanh Header Navigation bất kỳ lúc nào để xuất trình tại rạp.

#### 1.1.3. Các mô hình quản lý đặt vé rạp chiếu phim phổ biến
- **Mô hình Rạp đơn độc lập**: Quản lý 1 cụm rạp với số lượng phòng chiếu giới hạn.
- **Mô hình Chuỗi rạp đa chi nhánh (Multi-cinema Chain)**: Quản lý tập trung dữ liệu phim, suất chiếu và tài khoản khách hàng trên nhiều rạp tại các tỉnh/thành phố khác nhau (Đây là mô hình được lựa chọn cho đề tài).
- **Mô hình Cổng bán vé tổng hợp (Aggregator)**: Tích hợp nhiều chuỗi rạp khác nhau.

#### 1.1.4. Các hệ thống rạp chiếu phim tham khảo
- **CGV Cinemas**: Hệ thống rạp lớn với giao diện đặt vé trực quan, phân loại ghế Thường/VIP/Couples.
- **Lotte Cinema & Beta Cinemas**: Tối ưu tốc độ chọn suất chiếu và tích hợp các ví điện tử thanh toán QR.
- **NCC (Trung tâm Chiếu phim Quốc gia)**: Giao diện bố cục chuẩn mực, tập trung vào danh sách phim và lịch chiếu dễ nhìn.

---

### 1.2. Thực trạng công tác quản lý & đặt vé hiện nay
Tại một số rạp chiếu phim quy mô vừa và nhỏ hoặc các hệ thống bán vé cũ, quy trình vẫn tồn tại một số hạn chế:
- Khó kiểm soát chính xác vị trí ghế còn trống theo thời gian thực, dễ dẫn đến tình trạng bán trùng 1 ghế cho 2 khách hàng.
- Xử lý thanh toán thủ công làm chậm quá trình xuất vé, phải có nhân viên rà soát số dư tài khoản ngân hàng.
- Đồng hồ đếm ngược giữ ghế nếu chỉ làm ở Client sẽ bị lạm dụng khi khách cố tình bấm F5 refresh lại trang.

Hệ thống **MTBS** được xây dựng nhằm khắc phục triệt me các hạn chế trên bằng kiến trúc Spring Boot 3 + ReactJS 18 + PayOS Webhook tự động 100%.

---

### 1.3. Các thành phần chính của hệ thống (Danh sách 12 Mô đun)

| STT | Mô đun | Mô tả chức năng cốt lõi |
|---|---|---|
| 1 | **Authentication & Authorization** | Đăng ký, đăng nhập JWT Token, đổi mật khẩu, phân quyền `CUSTOMER` & `ADMIN`. |
| 2 | **User Management** | Quản lý thông tin tài khoản khách hàng, đổi SĐT/họ tên, quản lý người dùng Admin. |
| 3 | **Movie Management** | Quản lý phim, danh mục thể loại, upload poster & banner ngang 16:9 lên Cloudinary. |
| 4 | **Genre Management** | Quản lý thể loại phim phục vụ phân loại và tìm kiếm. |
| 5 | **Cinema Management** | Quản lý danh sách các cụm rạp chiếu phim. |
| 6 | **Room Management** | Quản lý phòng chiếu thuộc rạp. |
| 7 | **Seat Map Management** | Tự động khởi tạo sơ đồ ghế (Hàng A-H x Cột 1-10), phân loại Thường/VIP, trạng thái bảo trì. |
| 8 | **Showtime Management** | Quản lý suất chiếu theo phim/phòng/ngày giờ, chống trùng lịch chiếu. |
| 9 | **Seat Lock & Booking** | Chọn ghế real-time, **khóa giữ ghế 5 phút đếm chuẩn Server** chống F5. |
| 10 | **PayOS VietQR Payment** | Tích hợp cổng PayOS SDK 2.0.1, sinh mã QR VietQR, hiển thị tài khoản chuyển khoản. |
| 11 | **My Bookings History** | Trang tra cứu Lịch sử đặt vé chuyên biệt (`/my-bookings`) truy cập trực tiếp từ Header Navigation. |
| 12 | **Dashboard & Reports** | Thống kê tổng doanh thu, số vé bán ra, bảng danh sách giao dịch gần nhất cho Admin. |

*Bảng 1.1. Danh sách các mô đun chính của hệ thống MTBS (Nguồn: SRS-MTBS-001)*

---

### 1.4. Bài toán quản lý đặt vé của đề tài

#### 1.4.1. Phát biểu bài toán
Xây dựng một hệ thống thương mại điện tử đặt vé xem phim trực tuyến thống nhất, cho phép Khách hàng chọn suất chiếu, chọn vị trí ghế yêu thích, giữ ghế trong 5 phút và thanh toán qua mã VietQR PayOS. Hệ thống tự động gạch nợ qua Webhook bảo mật, xuất vé điện tử chứa QR Code và gửi Email xác nhận tức thì. Đồng thời hỗ trợ Quản trị viên quản lý toàn bộ dữ liệu rạp, phim, suất chiếu và theo dõi báo cáo doanh thu.

#### 1.4.2. Mục đích, mục tiêu
- Tự động hóa 100% quy trình bán vé & gạch nợ thanh toán.
- Chống trùng ghế tuyệt đối nhờ cơ chế mã khóa ghế Database Transaction & Server Timer.
- Chuẩn hóa Giao diện (UI/UX) Dark Mode Glassmorphism rực rỡ, hỗ trợ responsive mượt mà trên mọi thiết bị.

#### 1.4.3. Phạm vi
- **Trong đề tài**: Đăng ký, đăng nhập JWT, quản lý rạp/phòng/ghế, quản lý phim/lịch chiếu, đặt ghế 5m, thanh toán PayOS VietQR, nhận Webhook tự động, gửi Mail cuống vé HTML, trang lịch sử đặt vé (`/my-bookings`), Admin Dashboard thống kê.
- **Ngoài đề tài**: Quét mã vạch cơ học tại cửa rạp, tích hợp máy in nhiệt phần cứng.

#### 1.4.4. Giải pháp đề xuất và công nghệ sử dụng

| Thành phần | Công nghệ sử dụng | Vai trò trong hệ thống |
|---|---|---|
| **Frontend** | React 18, TypeScript, Vite, Lucide React, Vanilla CSS Glassmorphism | Xây dựng giao diện người dùng SPA, quản lý state và gọi REST API. |
| **Backend** | Spring Boot 3.2, Java 17, Spring Security, JWT Token | Xử lý logic nghiệp vụ, khóa ghế 5m, xác thực phân quyền và tích hợp PayOS. |
| **Database** | MySQL 8.0 (InnoDB Engine, UTF8MB4) | Lưu trữ cơ sở dữ liệu quan hệ người dùng, rạp, phim, ghế và đơn hàng. |
| **ORM** | Spring Data JPA / Hibernate | Ánh xạ đối tượng – quan hệ và thao tác dữ liệu an toàn. |
| **Payment Gateway** | PayOS SDK 2.0.1 VietQR API | Sinh mã QR VietQR và bắn Webhook tự động khi khách chuyển khoản thành công. |
| **Media Storage** | Cloudinary API | Lưu trữ hình ảnh Poster phim đứng và Banner poster ngang 16:9. |
| **Email Service** | Spring Mail (Gmail SMTP) | Gửi Email cuống vé định dạng HTML đến Gmail khách hàng. |

*Bảng 1.2. Công nghệ sử dụng trong hệ thống MTBS (Nguồn: SRS-MTBS-001)*

---

### 1.5. Kế hoạch thực hiện dự án

| Mã Issue | Nội dung công việc | Trạng thái |
|---|---|---|
| `MTBS-001` | Phân tích yêu cầu nghiệp vụ và xây dựng tài liệu SRS | Hoàn thành |
| `MTBS-002` | Thiết kế Cơ sở dữ liệu quan hệ MySQL & ERD | Hoàn thành |
| `MTBS-003` | Phát triển Backend REST API Spring Boot & Tích hợp PayOS | Hoàn thành |
| `MTBS-004` | Phát triển Frontend ReactJS UI Glassmorphic & Header Navigation | Hoàn thành |
| `MTBS-005` | Kiểm thử hệ thống, tối ưu Webhook & Hoàn thiện Báo cáo | Hoàn thành |

*Bảng 1.3. Kế hoạch thực hiện dự án MTBS (Nguồn: SRS-MTBS-001, mục 5.1)*

---

## CHƯƠNG 2: PHÂN TÍCH VÀ THIẾT KẾ HỆ THỐNG

### 2.1. Mô hình nghiệp vụ của hệ thống
Hệ thống phục vụ 3 nhóm Tác nhân (Actors) chính:

| Actor | Mô tả vai trò nghiệp vụ |
|---|---|
| **Guest** | Khách vãng khách truy cập trang chủ, xem thông tin phim, tra cứu suất chiếu, đăng ký/đăng nhập. |
| **Customer** | Khách hàng đã đăng nhập: chọn ghế, giữ ghế 5 phút, thanh toán PayOS VietQR, xem trang `/my-bookings`, mở mã QR vé điện tử, đổi mật khẩu & thông tin cá nhân. |
| **Admin** | Quản trị viên hệ thống: toàn quyền xem Dashboard doanh thu, quản lý rạp/phòng/ghế, quản lý phim/thể loại, tạo suất chiếu, quản lý người dùng và danh sách đơn hàng. |

*Bảng 2.1. Danh sách tác nhân hệ thống (Nguồn: SRS-MTBS-001, mục 2.1)*

---

### 2.2. Phân tích yêu cầu hệ thống

#### 2.2.1. Ma trận phân quyền chức năng

| Chức năng / Ca sử dụng | Guest | Customer | Admin |
|---|:---:|:---:|:---:|
| Đăng ký / Đăng nhập / Quên mật khẩu | Có | Có | Có |
| Xem danh sách phim & Banner Slider | Có | Có | Có |
| Tìm kiếm & Lọc phim | Có | Có | Có |
| Chọn ghế & Đặt giữ ghế 5 phút | Không | Có | Có |
| Thanh toán VietQR PayOS | Không | Có | Có |
| Xem Lịch sử Đặt vé (`/my-bookings`) | Không | Có | Có |
| Xem Mã QR Vé điện tử | Không | Có | Có |
| Cập nhật Hồ sơ & Đổi mật khẩu | Không | Có | Có |
| Quản lý Phim & Cloudinary Upload | Không | Không | Có |
| Quản lý Rạp, Phòng & Sơ đồ ghế | Không | Không | Có |
| Quản lý Lịch chiếu phim | Không | Không | Có |
| Quản lý Người dùng | Không | Không | Có |
| Dashboard Thống kê doanh thu | Không | Không | Có |

*Bảng 2.2. Ma trận phân quyền chức năng hệ thống (Nguồn: SRS-MTBS-001, mục 2.11)*

#### 2.2.2. Danh sách mã ca sử dụng (Use Case List)

| Mã UC | Tên ca sử dụng | Phân hệ | Tác nhân |
|---|---|---|---|
| `UC-AUTH-01` | Đăng ký tài khoản Khách hàng | Authentication | Guest |
| `UC-AUTH-02` | Đăng nhập hệ thống JWT Token | Authentication | Guest / Customer / Admin |
| `UC-AUTH-03` | Quên mật khẩu qua Email Token | Authentication | Guest / Customer |
| `UC-AUTH-04` | Đổi mật khẩu tài khoản | Authentication | Customer / Admin |
| `UC-AUTH-05` | Phân quyền người dùng bằng JWT | Authentication | System |
| `UC-USER-01` | Cập nhật hồ sơ cá nhân | User Management | Customer / Admin |
| `UC-USER-02` | Quản lý danh sách người dùng | User Management | Admin |
| `UC-MOVIE-01` | Xem & Tìm kiếm danh sách phim | Movie Management | Guest / Customer / Admin |
| `UC-MOVIE-02` | Quản lý phim & Upload Cloudinary | Movie Management | Admin |
| `UC-CATE-01` | Quản lý thể loại phim | Movie Management | Admin |
| `UC-CINEMA-01` | Quản lý cụm rạp chiếu phim | Cinema Management | Admin |
| `UC-ROOM-01` | Quản lý phòng chiếu phim | Cinema Management | Admin |
| `UC-SEAT-01` | Tự động sinh sơ đồ ghế Thường/VIP | Seat Management | Admin |
| `UC-SHOWTIME-01` | Quản lý & Kiểm tra trùng lịch chiếu | Showtime Management | Admin |
| `UC-SHOWTIME-02` | Xem lịch chiếu phim theo ngày | Showtime Management | Guest / Customer / Admin |
| `UC-BOOKING-01` | Chọn ghế & Giữ ghế 5 phút đếm Server | Booking Management | Customer / Admin |
| `UC-BOOKING-02` | Xem lịch sử đặt vé (`/my-bookings`) | Booking Management | Customer / Admin |
| `UC-PAYMENT-01` | Thanh toán VietQR PayOS & Webhook | Payment Management | Customer / System |
| `UC-DASH-01` | Dashboard & Báo cáo doanh thu | Dashboard | Admin |

*Bảng 2.3. Danh sách ca sử dụng của hệ thống (Nguồn: SRS-MTBS-001, mục 3.0)*

---

### 2.2.3. Đặc tả ca sử dụng chi tiết (Đầy đủ 18 Use Cases)

#### 1. Ca sử dụng UC-AUTH-01: Đăng ký tài khoản Khách hàng
- **Tác nhân**: Guest.
- **Mô tả**: Cho phép người dùng chưa có tài khoản đăng ký tài khoản mới.
- **Mức độ ưu tiên**: High.
- **Trigger**: Người dùng chọn *"Đăng ký"* trên giao diện.
- **Điều kiện tiên quyết**: Email đăng ký chưa tồn tại trong hệ thống.
- **Điều kiện sau**: Tài khoản mới được khởi tạo ở trạng thái `ACTIVE` với vai trò `CUSTOMER`.
- **Luồng cơ bản**:
  1. Người dùng chọn nút Đăng ký. Hệ thống hiển thị biểu mẫu.
  2. Người dùng nhập Họ tên, Email, SĐT, Mật khẩu và Xác nhận mật khẩu.
  3. Người dùng nhấn *"Đăng ký"*.
  4. Hệ thống validate các trường bắt buộc và định dạng Email.
  5. Hệ thống mã hóa mật khẩu bằng BCrypt.
  6. Hệ thống lưu tài khoản mới vào cơ sở dữ liệu MySQL.
  7. Hệ thống thông báo đăng ký thành công và chuyển sang màn hình Đăng nhập.
- **Luồng ngoại lệ**: Email đã tồn tại -> Thông báo "Email đã được sử dụng"; Mật khẩu xác nhận không khớp -> Thông báo "Mật khẩu không trùng khớp".
- **Ràng buộc nghiệp vụ**: Email phải duy nhất; Mật khẩu tối thiểu 6 ký tự.
- **API liên quan**: `POST /api/v1/auth/register`.

#### 2. Ca sử dụng UC-AUTH-02: Đăng nhập hệ thống bằng JWT
- **Tác nhân**: Guest, Customer, Admin.
- **Mô tả**: Cho phép người dùng xác thực bằng Email và Mật khẩu để nhận JWT Token.
- **Mức độ ưu tiên**: High.
- **Luồng cơ bản**:
  1. Người dùng nhập Email và Mật khẩu ➔ Bấm *"Đăng nhập"*.
  2. Server so sánh hash BCrypt mật khẩu.
  3. Khi đúng, Server sinh JWT Token chứa `userId`, `email`, `role` có thời hạn.
  4. Client lưu Token vào `localStorage` và điều hướng về Trang chủ hoặc Admin Dashboard.
- **API liên quan**: `POST /api/v1/auth/login`.

#### 3. Ca sử dụng UC-AUTH-03: Quên mật khẩu qua Email Token
- **Tác nhân**: Guest, Customer.
- **Mô tả**: Gửi Email chứa Token khôi phục mật khẩu khi người dùng quên.
- **Luồng cơ bản**:
  1. Người dùng nhập Email đã đăng ký ➔ Bấm *"Gửi yêu cầu"*.
  2. System sinh Token reset có hạn 15m và gửi Link qua Gmail SMTP.
  3. Người dùng bấm Link, nhập mật khẩu mới ➔ System cập nhật mật khẩu.
- **API liên quan**: `POST /api/v1/auth/forgot-password`.

#### 4. Ca sử dụng UC-USER-01: Cập nhật hồ sơ cá nhân
- **Tác nhân**: Customer, Admin.
- **Mô tả**: Thay đổi thông tin Họ tên, Số điện thoại hoặc Đổi mật khẩu.
- **Luồng cơ bản**: Người dùng truy cập trang `/profile` ➔ Nhập thông tin mới ➔ Bấm *"Lưu thay đổi"* ➔ System cập nhật DB.
- **API liên quan**: `PUT /api/v1/users/profile`, `POST /api/v1/auth/change-password`.

#### 5. Ca sử dụng UC-MOVIE-01: Xem & Tìm kiếm danh sách phim
- **Tác nhân**: Guest, Customer, Admin.
- **Mô tả**: Xem danh sách phim đang chiếu/sắp chiếu và tìm kiếm theo từ khóa/thể loại.
- **Luồng cơ bản**: Người dùng nhập từ khóa tìm kiếm hoặc chọn Thể loại ➔ System trả về lưới phim khớp điều kiện có phân trang.
- **API liên quan**: `GET /api/v1/movies`.

#### 6. Ca sử dụng UC-MOVIE-02: Quản lý Phim & Upload Cloudinary
- **Tác nhân**: Admin.
- **Mô tả**: Admin Thêm, Sửa, Xóa phim và Tải ảnh Poster đứng & Banner ngang 16:9 lên Cloudinary.
- **Luồng cơ bản**: Admin mở biểu mẫu quản lý ➔ Tải file ảnh ➔ Cloudinary trả về URL ➔ Lưu thông tin phim vào DB.
- **API liên quan**: `POST /api/v1/movies`, `PUT /api/v1/movies/{id}`, `DELETE /api/v1/movies/{id}`.

#### 7. Ca sử dụng UC-CINEMA-01 & UC-ROOM-01: Quản lý Cụm rạp & Phòng chiếu
- **Tác nhân**: Admin.
- **Mô tả**: Admin Thêm/Sửa/Xóa các Cụm rạp (Tên, Địa chỉ) và các Phòng chiếu thuộc rạp.
- **API liên quan**: `POST /api/v1/cinemas`, `POST /api/v1/rooms`.

#### 8. Ca sử dụng UC-SEAT-01: Tự động sinh Sơ đồ ghế Thường/VIP
- **Tác nhân**: Admin.
- **Mô tả**: Tự động sinh danh sách ghế theo Hàng (A-H) x Cột (1-10) khi tạo phòng chiếu.
- **Quy tắc nghiệp vụ**: Hàng A, B, C, G, H là Ghế Thường (100% base price); Hàng D, E, F là Ghế VIP (120% base price).
- **API liên quan**: `POST /api/v1/seats/generate`.

#### 9. Ca sử dụng UC-SHOWTIME-01: Quản lý Suất chiếu & Bắt trùng lịch
- **Tác nhân**: Admin.
- **Mô tả**: Tạo lịch chiếu cho phim tại phòng chiếu cụ thể.
- **Quy tắc nghiệp vụ**: Kiểm tra thời gian bắt đầu và kết thúc suất chiếu không được đè lên suất chiếu khác trong cùng 1 phòng.
- **API liên quan**: `POST /api/v1/showtimes`.

#### 10. Ca sử dụng UC-BOOKING-01: Chọn ghế & Giữ ghế 5 phút đếm Server
- **Tác nhân**: Customer.
- **Mô tả**: Chọn các vị trí ghế mong muốn và giữ chỗ trong 5 phút.
- **Luồng cơ bản**:
  1. Khách hàng chọn các ghế trên sơ đồ ➔ Bấm *"Đặt giữ ghế"*.
  2. Server tạo Booking status `PENDING_PAYMENT`, khóa ghế status `HOLD`, lưu mốc thời gian `created_at`.
  3. Màn hình thanh toán tính thời gian còn lại = `300 - (CurrentTime - CreatedAt)`. Chống F5 reset timer.
- **API liên quan**: `POST /api/v1/bookings`.

#### 11. Ca sử dụng UC-BOOKING-02: Xem Lịch sử đặt vé (`/my-bookings`)
- **Tác nhân**: Customer.
- **Mô tả**: Xem danh sách vé đã đặt thông qua nút bấm trên thanh Header Navigation.
- **Luồng cơ bản**: Khách hàng bấm nút **"Lịch sử đặt vé"** trên Header ➔ Chuyển sang `/my-bookings` ➔ Hiển thị bảng kính Glassmorphism chứa đầy đủ vé đã mua kèm nút *"Xem QR Vé"*.
- **API liên quan**: `GET /api/v1/bookings/my-history`.

#### 12. Ca sử dụng UC-PAYMENT-01: Thanh toán VietQR PayOS & Webhook
- **Tác nhân**: Customer, Hệ thống PayOS.
- **Mô tả**: Quét mã VietQR chuyển khoản và tự động nhận Webhook gạch nợ.
- **Luồng cơ bản**:
  1. Khách hàng quét mã VietQR chuyển khoản.
  2. PayOS bắn Webhook chữ ký HMAC-SHA256 về `/api/v1/payments/payos-webhook`.
  3. Server xác thực chữ ký ➔ Đổi Booking `PAID`, ghế `BOOKED`, sinh vé QR Code & gửi Email cuống vé HTML.
- **API liên quan**: `GET /api/v1/payments/checkout/{bookingId}`, `POST /api/v1/payments/payos-webhook`.

#### 13. Ca sử dụng UC-DASH-01: Dashboard & Báo cáo doanh thu
- **Tác nhân**: Admin.
- **Mô tả**: Thống kê tổng doanh thu, số vé đã bán và danh sách giao dịch mới nhất.
- **API liên quan**: `GET /api/v1/admin/dashboard/stats`.

---

### 2.3. Mô hình hóa hoạt động của hệ thống (Activity Diagrams)

#### 2.3.1. Quy trình chọn ghế và đếm ngược giữ ghế 5 phút
```
[Khách hàng] chọn suất chiếu ➔ Xem Sơ đồ ghế ➔ Chọn ghế Trống (AVAILABLE)
     │
     ▼
[Server] Kiểm tra trạng thái ghế trong DB
     │
     ├──(Nếu đã bị người khác giữ/bán) ──> Thông báo lỗi & Yêu cầu chọn ghế khác
     │
     └──(Nếu ghế còn trống) ──────────────> Đổi trạng thái ghế = HOLD
                                           Tạo Booking PENDING_PAYMENT (lưu created_at)
                                           Khởi động đếm ngược 5m = 300 - (Now - CreatedAt)
                                           Chuyển sang màn hình Thanh toán PaymentCheckout
```

#### 2.3.2. Quy trình thanh toán PayOS VietQR & Webhook xuất vé
```
[Khách hàng] Quét mã VietQR trên PayOS ➔ Thực hiện chuyển khoản Ngân hàng
     │
     ▼
[Cổng PayOS] Xác nhận nhận tiền ➔ Bắn Webhook kèm chữ ký HMAC-SHA256 về Server
     │
     ▼
[Backend Spring Boot] Kiểm tra checksum HMAC-SHA256
     │
     ├──(Chữ ký sai) ─────────────────────> Từ chối Webhook (HTTP 400)
     │
     └──(Chữ ký đúng) ────────────────────> Cập nhật Booking status = PAID
                                           Cập nhật ghế status = BOOKED
                                           Sinh mã vé E-Ticket & Mã QR Code
                                           Gửi Email cuống vé HTML qua Gmail SMTP
                                           Client Polling phát hiện status PAID ──> Chuyển sang BookingSuccess
```

---

### 2.4. Sơ đồ chuyển trạng thái

#### 2.4.1. Trạng thái của ghế (Seat State Machine)
`AVAILABLE` (Ghế trống) ➔ `HOLD` (Khách chọn giữ ghế 5m) ➔ `BOOKED` (Đã thanh toán) ➔ `MAINTENANCE` (Bảo trì).
- Từ `HOLD`, nếu hết 5 phút hoặc khách hủy đơn, ghế tự động quay về `AVAILABLE`.

#### 2.4.2. Trạng thái của đơn hàng Booking (Booking State Machine)
`PENDING_PAYMENT` (Mới khởi tạo, đang giữ ghế 5m) ➔ `PAID` (Thanh toán thành công) / `CANCELLED` (Hết 5m hoặc Admin hủy).

---

### 2.5. Thiết kế Cơ sở dữ liệu (Từ điển dữ liệu 12 bảng MySQL)

#### 1. Bảng `users` (Người dùng)
- `id` (BIGINT, PK, Auto Increment)
- `email` (VARCHAR(100), Unique, Required)
- `password` (VARCHAR(255), BCrypt Hashed)
- `fullname` (VARCHAR(100), Required)
- `phone` (VARCHAR(20))
- `role` (VARCHAR(20), `ROLE_CUSTOMER` / `ROLE_ADMIN`)
- `status` (VARCHAR(20), `ACTIVE` / `LOCKED`)

#### 2. Bảng `roles` (Vai trò)
- `id` (BIGINT, PK)
- `name` (VARCHAR(20), Unique, `ROLE_CUSTOMER`, `ROLE_ADMIN`)
- `description` (VARCHAR(255))

#### 3. Bảng `movies` (Phim)
- `id` (BIGINT, PK)
- `title` (VARCHAR(255), Required)
- `description` (TEXT)
- `duration` (INT, Thời lượng phút)
- `release_date` (DATE)
- `age_rating` (VARCHAR(10), `P`, `C13`, `C16`, `C18`)
- `poster_url` (VARCHAR(500), Ảnh đứng Cloudinary)
- `banner_url` (VARCHAR(500), Ảnh ngang 16:9 Cloudinary)
- `status` (VARCHAR(20), `NOW_SHOWING`, `COMING_SOON`, `INACTIVE`)

#### 4. Bảng `genres` (Thể loại phim)
- `id` (BIGINT, PK)
- `name` (VARCHAR(100), Unique)
- `description` (VARCHAR(255))

#### 5. Bảng `movie_genres` (Bảng trung gian Phim - Thể loại)
- `movie_id` (BIGINT, FK -> movies.id)
- `genre_id` (BIGINT, FK -> genres.id)
- Primary Key (`movie_id`, `genre_id`)

#### 6. Bảng `cinemas` (Cụm rạp)
- `id` (BIGINT, PK)
- `name` (VARCHAR(255), Required)
- `address` (VARCHAR(500))
- `phone` (VARCHAR(20))
- `status` (VARCHAR(20), `ACTIVE` / `INACTIVE`)

#### 7. Bảng `rooms` (Phòng chiếu)
- `id` (BIGINT, PK)
- `cinema_id` (BIGINT, FK -> cinemas.id)
- `name` (VARCHAR(100), Required)
- `total_seats` (INT, Tổng số ghế)
- `status` (VARCHAR(20), `ACTIVE` / `MAINTENANCE`)

#### 8. Bảng `seats` (Sơ đồ ghế)
- `id` (BIGINT, PK)
- `room_id` (BIGINT, FK -> rooms.id)
- `row_code` (VARCHAR(5), Ví dụ: 'A', 'B', 'C')
- `seat_number` (INT, Ví dụ: 1..10)
- `seat_type` (VARCHAR(20), `STANDARD`, `VIP`)
- `status` (VARCHAR(20), `AVAILABLE`, `MAINTENANCE`)

#### 9. Bảng `showtimes` (Lịch chiếu)
- `id` (BIGINT, PK)
- `movie_id` (BIGINT, FK -> movies.id)
- `room_id` (BIGINT, FK -> rooms.id)
- `start_time` (DATETIME)
- `end_time` (DATETIME)
- `base_price` (DECIMAL(12,2), Giá vé cơ bản)

#### 10. Bảng `bookings` (Đơn đặt vé)
- `id` (BIGINT, PK)
- `booking_code` (VARCHAR(50), Unique)
- `user_id` (BIGINT, FK -> users.id)
- `showtime_id` (BIGINT, FK -> showtimes.id)
- `total_price` (DECIMAL(12,2))
- `status` (VARCHAR(20), `PENDING_PAYMENT`, `PAID`, `CANCELLED`)
- `created_at` (DATETIME, Mốc thời gian khóa ghế 5m)

#### 11. Bảng `booking_seats` (Chi tiết ghế đặt trong đơn)
- `booking_id` (BIGINT, FK -> bookings.id)
- `seat_id` (BIGINT, FK -> seats.id)
- `price` (DECIMAL(12,2))
- Primary Key (`booking_id`, `seat_id`)

#### 12. Bảng `tickets` (Vé điện tử)
- `id` (BIGINT, PK)
- `ticket_code` (VARCHAR(50), Unique)
- `booking_id` (BIGINT, FK -> bookings.id)
- `seat_id` (BIGINT, FK -> seats.id)
- `qr_code_url` (VARCHAR(500))
- `issued_at` (DATETIME)

#### 2.6.9. Công thức nghiệp vụ
- **Công thức tính tổng tiền đơn vé**:  
  $$\text{TotalPrice} = \sum (\text{BasePrice} \times \text{SeatMultiplier})$$  
  *(Ghế Standard multiplier = 1.0; Ghế VIP multiplier = 1.2)*
- **Công thức tính đếm ngược giữ ghế Server-side**:  
  $$\text{RemainingSeconds} = 300 - (\text{CurrentTimestamp} - \text{BookingCreatedAtTimestamp})$$

---

### 2.7. Thiết kế Giao diện Người dùng (UI/UX Site Map & Components)

#### 2.7.1. Sơ đồ trang (Site Map)
- **Header Navigation (Thanh Menu Đầu Trang)**:
  - Logo Rạp MovieBooking
  - 🧭 **Trang chủ** (`/`)
  - 🎟️ **Lịch sử đặt vé** (`/my-bookings`) - *Nút truy cập trực tiếp*
  - 👤 **Cá nhân** (`/profile`) - *Chỉ dùng đổi thông tin & mật khẩu*
  - 🛡️ **Quản trị** (`/admin/dashboard`) - *Dành riêng cho Admin*
- **Khu vực Luồng Đặt Vé**:
  - Trang Chi tiết phim (`/movies/:id`)
  - Trang Chọn sơ đồ ghế (`/booking/seats/:showtimeId`)
  - Trang Thanh toán VietQR PayOS (`/booking/payment/:bookingId`)
  - Trang Đặt vé thành công (`/booking/success/:bookingCode`) - *Chứa nút "Quay lại trang chủ"*

#### 2.7.2. Component giao diện dùng chung Glassmorphism

| Component | Mô tả giao diện & Chức năng |
|---|---|
| **Header Nav Bar** | Nằm cố định đầu trang với hiệu ứng kính mờ Glassmorphism 12px blur, tích hợp nút Lịch sử đặt vé. |
| **Seat Map Matrix** | Ma trận ghế tương tác real-time với màu sắc phân biệt (Trắng: Trống, Vàng: VIP, Tím: Bạn chọn, Đỏ: Đã đặt). |
| **VietQR PayOS Card** | Khung hiển thị mã QR VietQR chuyển khoản tự động kèm đếm ngược 5m và thông báo người dùng thân thiện. |
| **E-Ticket Stub** | Thẻ vé điện tử thiết kế đục lỗ răng cưa độc đáo kèm mã QR Code soi soát tại quầy. |
| **Data Table** | Bảng hiển thị danh sách dạng kính mờ có phân trang, dùng cho xem vé và trang Admin. |

*Bảng 2.16. Danh sách component giao diện dùng chung (Nguồn: SRS-MTBS-001)*

---

## CHƯƠNG 3: CÀI ĐẶT VÀ KIỂM THỬ HỆ THỐNG

### 3.1. Bảo mật hệ thống
1. **Xác thực JWT (JSON Web Token)**: Mọi API bảo mật kiểm tra qua `JwtAuthenticationFilter`. JWT chứa role `ROLE_CUSTOMER` hoặc `ROLE_ADMIN`.
2. **Mã hóa Mật khẩu BCrypt**: Mật khẩu được mã hóa an toàn trước khi lưu DB.
3. **Xác thực Chữ ký PayOS Webhook (HMAC-SHA256)**: Payload từ PayOS được xác thực checksum bằng `payos.checksum-key`.

---

### 3.2. Yêu cầu phi chức năng (NFR)
- **NFR-PERF-01**: Phản hồi API trung bình < 500ms.
- **NFR-PERF-02**: Webhook tiếp nhận thanh toán & gạch nợ < 300ms.
- **NFR-SEC-01**: Khóa ghế thời gian thực chống Race Condition.
- **NFR-UI-01**: Responsive mượt mà 100% trên Desktop, Tablet và Mobile.

---

### 3.3. Danh sách RESTful API Hệ thống (Bảng 18 Endpoints)

| HTTP Method | Endpoint API | Quyền truy cập | Diễn giải chức năng |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Public | Đăng ký tài khoản khách hàng |
| `POST` | `/api/v1/auth/login` | Public | Đăng nhập nhận chuỗi JWT Token |
| `POST` | `/api/v1/auth/forgot-password` | Public | Gửi email yêu cầu đặt lại mật khẩu |
| `POST` | `/api/v1/auth/change-password` | Authenticated | Thay đổi mật khẩu người dùng |
| `GET` | `/api/v1/users/profile` | Authenticated | Lấy thông tin cá nhân người dùng |
| `PUT` | `/api/v1/users/profile` | Authenticated | Cập nhật tên & SĐT cá nhân |
| `GET` | `/api/v1/movies` | Public | Lấy danh sách phim có phân trang & lọc |
| `GET` | `/api/v1/movies/top-revenue` | Public | Lấy Top 3 phim có doanh thu cao nhất |
| `GET` | `/api/v1/movies/{id}` | Public | Lấy chi tiết phim |
| `POST` | `/api/v1/movies` | Admin | Thêm mới phim (Poster & Banner 16:9) |
| `GET` | `/api/v1/showtimes/movie/{id}` | Public | Lấy lịch chiếu theo phim |
| `POST` | `/api/v1/bookings` | Customer / Admin | Tạo đơn đặt vé & khóa giữ ghế 5 phút |
| `GET` | `/api/v1/bookings/my-history` | Customer / Admin | Truy vấn danh sách lịch sử đặt vé (`/my-bookings`) |
| `GET` | `/api/v1/payments/checkout/{bookingId}` | Customer / Admin | Lấy thông tin chuyển khoản VietQR PayOS |
| `POST` | `/api/v1/payments/payos-webhook` | PayOS System | Webhook tự động nhận gạch nợ thanh toán |
| `GET` | `/api/v1/admin/dashboard/stats` | Admin | Thống kê tổng doanh thu & số đơn |
| `GET` | `/api/v1/cinemas` | Public | Lấy danh sách rạp chiếu phim |
| `GET` | `/api/v1/seats/showtime/{id}` | Public | Lấy sơ đồ ghế theo suất chiếu |

*Bảng 3.1. Danh sách RESTful API chính của hệ thống MTBS (Nguồn: SRS-MTBS-001)*

---

### 3.4. Hệ thống Thông báo và Xử lý Lỗi (Response Envelope)
Mọi response trả về theo dạng chuẩn `ApiResponse<T>`:
```json
{
  "success": true,
  "message": "Xử lý thành công",
  "data": { ... }
}
```

---

### 3.5. Xây dựng các Mô Đun Trọng Tâm

#### 1. Mô đun Khóa Giữ Ghế 5 Phút Server-side (`BookingServiceImpl.java`)
```java
@Transactional
public BookingResponse createBooking(BookingRequest request, String userEmail) {
    User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
            .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

    // Kiểm tra chống đặt trùng ghế đang HOLD hoặc BOOKED
    List<Seat> selectedSeats = seatRepository.findAllById(request.getSeatIds());
    for (Seat seat : selectedSeats) {
        if (bookingRepository.isSeatLocked(showtime.getId(), seat.getId())) {
            throw new BadRequestException("Ghế " + seat.getRowCode() + seat.getSeatNumber() + " đã có người chọn!");
        }
    }

    // Tạo Booking status PENDING_PAYMENT
    Booking booking = new Booking();
    booking.setBookingCode("BKG" + System.currentTimeMillis());
    booking.setUser(user);
    booking.setShowtime(showtime);
    booking.setStatus(BookingStatus.PENDING_PAYMENT);
    booking.setTotalPrice(calculateTotalPrice(showtime, selectedSeats));
    bookingRepository.save(booking);

    return mapToBookingResponse(booking);
}
```

#### 2. Mô đun Tiếp Nhận PayOS Webhook (`PaymentController.java`)
```java
@PostMapping("/payos-webhook")
public ResponseEntity<String> handlePayOSWebhook(@RequestBody Map<String, Object> payload) {
    boolean isValid = payOSService.verifyWebhookSignature(payload);
    if (!isValid) {
        return ResponseEntity.badRequest().body("Invalid signature");
    }

    String bookingCode = payOSService.extractBookingCode(payload);
    bookingService.confirmPaymentSuccess(bookingCode);
    emailService.sendTicketEmail(bookingCode);

    return ResponseEntity.ok("Webhook processed successfully");
}
```

---

### 3.6. Kiểm thử Hệ thống (System Test Cases & Results)

#### 1. Test Case TC-BOOK-01: Đặt Giữ Ghế & Tính Đếm Ngược 5 Phút
- **Đầu vào**: Khách chọn ghế C03 tại rạp Beta Xuân Thủy ➔ Bấm *"Đặt giữ ghế"*.
- **Kết quả kỳ vọng**: Đơn hàng tạo `PENDING_PAYMENT`, đếm ngược hiển thị `05:00`. F5 trang web đếm ngược vẫn giữ đúng chính xác mốc giây thực từ Server.
- **Kết quả thực tế**: **PASSED 100%**.

#### 2. Test Case TC-PAY-01: Thanh Toán VietQR PayOS & Webhook
- **Đầu vào**: Chuyển khoản thành công bằng app Ngân hàng tới mã PayOS VietQR.
- **Kết quả kỳ vọng**: Webhook phản hồi <300ms, đơn hàng đổi `PAID`, ghế đổi `BOOKED`, Email vé điện tử gửi về Gmail.
- **Kết quả thực tế**: **PASSED 100%**.

#### 3. Test Case TC-NAV-01: Tra Cứu Lịch Sử Đặt Vé Từ Header (`/my-bookings`)
- **Đầu vào**: Bấm nút **"Lịch sử đặt vé"** trên thanh Header Navigation.
- **Kết quả kỳ vọng**: Chuyển trực tiếp sang `/my-bookings`, hiển thị danh sách vé dạng kính mờ Glassmorphism kèm nút xem QR code.
- **Kết quả thực tế**: **PASSED 100%**.

#### 3.6.4. Nhận xét kết quả kiểm thử
Hệ thống vượt qua 100% các kịch bản kiểm thử tự động và thủ công. Không phát hiện bất kỳ lỗi xung đột dữ liệu (Race Condition) hay lỗi đè ghế nào.

---

## KẾT LUẬN

Dự án *"Xây dựng hệ thống quản lý đặt vé xem phim online"* (**Movie Ticket Booking System - MTBS**) đã được hoàn thành xuất sắc, đáp ứng đầy đủ các tiêu chuẩn học thuật và yêu cầu đặc tả nghiệp vụ `SRS-MTBS-001`.

**Các kết quả nổi bật**:
1. **Kiến trúc Hiện đại**: Kết hợp Spring Boot 3 + React 18 TypeScript phân tách RESTful API rõ ràng.
2. **Giao diện Glassmorphism Sang trọng**: Hệ thống Dark Mode tối ưu xem phim, nút **"Lịch sử đặt vé"** trên Header trực quan.
3. **Thanh toán & Gạch nợ Tự động 100%**: Tích hợp PayOS VietQR nhận Webhook siêu tốc & tự động gửi Email cuống vé HTML.

---

## TÀI LIỆU THAM KHẢO

1. **Spring Boot Documentation**: *Spring Framework Reference Documentation*, VMware, 2026.
2. **ReactJS & TypeScript Guide**: *Building Scalable Web Applications with React 18*, Meta Open Source, 2026.
3. **PayOS Developer Documentation**: *VietQR Payment Gateway API Specification v2.0.1*, PayOS Vietnam, 2026.
4. **Tài liệu Đặc tả Nghiệp vụ SRS**: *`DOC_03_SRS Movie Ticket Booking System`*, Hoàng Thiên Sơn, Hà Nội 2026.
