import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldCheck, FileText, ArrowLeft, Clock, CheckCircle2 } from 'lucide-react';

const TermsOfService: React.FC = () => {
  return (
    <div className="animate-fade-in" style={{ maxWidth: '900px', margin: '40px auto', padding: '0 20px', textAlign: 'left' }}>
      
      {/* Back link */}
      <Link to="/" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', textDecoration: 'none', marginBottom: '24px', fontSize: '14px', fontWeight: 500 }}>
        <ArrowLeft size={16} /> Quay lại trang chủ
      </Link>

      <div className="glass-card" style={{ padding: '40px', borderRadius: '24px' }}>
        <div style={{ borderBottom: '1px solid var(--border-color)', paddingBottom: '20px', marginBottom: '30px' }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '8px', padding: '6px 14px', borderRadius: '999px', backgroundColor: 'rgba(99, 102, 241, 0.15)', color: 'var(--primary)', fontSize: '12px', fontWeight: 700, marginBottom: '12px' }}>
            <FileText size={14} /> QUY ĐỊNH & THỎA THUẬN SỬ DỤNG
          </div>
          <h1 style={{ fontSize: '28px', fontWeight: 800, margin: '0 0 10px 0', color: 'var(--text-primary)' }}>
            Điều Khoản Dịch Vụ - Movie Booking
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '14px', margin: 0 }}>
            Cập nhật lần cuối: Ngày 09 tháng 08 năm 2026 • Áp dụng cho toàn bộ người dùng và hệ thống đối tác
          </p>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', color: 'var(--text-secondary)', lineHeight: '1.7', fontSize: '15px' }}>
          
          <section>
            <h2 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <CheckCircle2 size={18} style={{ color: 'var(--primary)' }} /> 1. Chấp Thuận Điều Khoản
            </h2>
            <p>
              Khi truy cập, đăng ký tài khoản hoặc sử dụng hệ thống đặt vé trực tuyến <strong>Movie Booking</strong>, người dùng đồng ý tuân thủ toàn bộ các điều khoản dịch vụ và quy định dưới đây. Nếu bạn không đồng ý với bất kỳ phần nào của điều khoản này, xin vui lòng ngưng sử dụng dịch vụ.
            </p>
          </section>

          <section>
            <h2 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <CheckCircle2 size={18} style={{ color: 'var(--primary)' }} /> 2. Tài Khoản & Bảo Mật Thông Tin
            </h2>
            <p>
              Người dùng có trách nhiệm cung cấp thông tin chính xác khi đăng ký tài khoản (họ tên, email, số điện thoại). Bạn chịu trách nhiệm bảo mật mật khẩu tài khoản cá nhân và mọi hoạt động phát sinh dưới tài khoản của mình.
            </p>
          </section>

          <section>
            <h2 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <CheckCircle2 size={18} style={{ color: 'var(--primary)' }} /> 3. Quy Định Đặt Vé & Giữ Chỗ Tự Động
            </h2>
            <ul style={{ paddingLeft: '20px', margin: 0, display: 'flex', flexDirection: 'column', gap: '8px' }}>
              <li>Khi khách hàng chọn ghế và nhấn Đặt vé, hệ thống tự động khóa tạm thời (giữ chỗ) trong <strong>10 phút</strong>.</li>
              <li>Nếu thanh toán không hoàn tất trong thời gian quy định, đơn hàng sẽ tự động bị hủy và giải phóng ghế cho khách hàng khác.</li>
              <li>Vé điện tử chứa mã QR sẽ được gửi tự động về email đã đăng ký ngay khi giao dịch thành công.</li>
            </ul>
          </section>

          <section>
            <h2 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <CheckCircle2 size={18} style={{ color: 'var(--primary)' }} /> 4. Chính Sách Hoàn / Đổi Vé
            </h2>
            <p>
              Vé xem phim sau khi mua thành công không được hỗ trợ hoàn tiền hoặc hủy đơn theo quy định của các cụm rạp đối tác. Trong trường hợp có sự cố bất khả kháng từ phía cụm rạp (hoãn suất chiếu, sự cố kỹ thuật), trung tâm chăm sóc khách hàng sẽ hỗ trợ đổi suất chiếu khác hoặc hoàn tiền theo chính sách rạp.
            </p>
          </section>

          <section>
            <h2 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <CheckCircle2 size={18} style={{ color: 'var(--primary)' }} /> 5. Quyền & Trách Nhiệm Của Hệ Thống
            </h2>
            <p>
              Movie Booking cam kết bảo vệ thông tin khách hàng, duy trì hoạt động hệ thống ổn định và xử lý sự cố nhanh chóng. Hệ thống có quyền khóa hoặc chấm dứt tài khoản nếu phát hiện hành vi gian lận, phá hoại hoặc vi phạm pháp luật.
            </p>
          </section>

        </div>

        <div style={{ marginTop: '40px', borderTop: '1px solid var(--border-color)', paddingTop: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
          <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Mọi thắc mắc xin liên hệ bộ phận CSKH Movie Booking</span>
          <Link to="/contact" className="btn btn-primary" style={{ padding: '8px 16px', fontSize: '13px' }}>
            Liên hệ hỗ trợ ngay
          </Link>
        </div>
      </div>
    </div>
  );
};

export default TermsOfService;
