import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldCheck, Lock, Eye, Server, ArrowLeft, CheckCircle2 } from 'lucide-react';

const PrivacyPolicy: React.FC = () => {
  return (
    <div className="animate-fade-in" style={{ maxWidth: '900px', margin: '40px auto', padding: '0 20px', textAlign: 'left' }}>
      
      {/* Back link */}
      <Link to="/" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', textDecoration: 'none', marginBottom: '24px', fontSize: '14px', fontWeight: 500 }}>
        <ArrowLeft size={16} /> Quay lại trang chủ
      </Link>

      <div className="glass-card" style={{ padding: '40px', borderRadius: '24px' }}>
        <div style={{ borderBottom: '1px solid var(--border-color)', paddingBottom: '20px', marginBottom: '30px' }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '8px', padding: '6px 14px', borderRadius: '999px', backgroundColor: 'rgba(16, 185, 129, 0.15)', color: 'var(--success)', fontSize: '12px', fontWeight: 700, marginBottom: '12px' }}>
            <ShieldCheck size={14} /> AN TOÀN & BẢO MẬT TUYỆT ĐỐI
          </div>
          <h1 style={{ fontSize: '28px', fontWeight: 800, margin: '0 0 10px 0', color: 'var(--text-primary)' }}>
            Chính Sách Bảo Mật Quyền Riêng Tư
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '14px', margin: 0 }}>
            Movie Booking cam kết bảo vệ thông tin cá nhân và dữ liệu thanh toán của quý khách hàng
          </p>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', color: 'var(--text-secondary)', lineHeight: '1.7', fontSize: '15px' }}>
          
          <section>
            <h2 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Eye size={18} style={{ color: 'var(--success)' }} /> 1. Thu Thập Thông Tin Cá Nhân
            </h2>
            <p>
              Chúng tôi chỉ thu thập các thông tin cần thiết phục vụ việc đặt vé và chăm sóc khách hàng bao gồm: Họ tên, Email, Số điện thoại và Lịch sử giao dịch đặt vé. Dữ liệu này giúp chúng tôi gửi cuống vé điện tử QR code và hỗ trợ khi quý khách nhận vé tại rạp.
            </p>
          </section>

          <section>
            <h2 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Lock size={18} style={{ color: 'var(--success)' }} /> 2. Mã Hóa & Lưu Trữ Mật Khẩu
            </h2>
            <p>
              Mật khẩu tài khoản của người dùng được mã hóa một chiều bằng thuật toán bảo mật chuẩn quốc tế <strong>BCrypt Password Encoder</strong> trước khi lưu trữ vào Database. Ngay cả quản trị viên hệ thống cũng không thể đọc hoặc khôi phục trực tiếp mật khẩu gốc của bạn.
            </p>
          </section>

          <section>
            <h2 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Server size={18} style={{ color: 'var(--success)' }} /> 3. Bảo Mật Giao Dịch Thanh Toán
            </h2>
            <p>
              Hệ thống tích hợp cổng thanh toán ngân hàng chuyển khoản tự động bảo mật với mã giao dịch duy nhất. Tất cả thông tin chuyển khoản được tự động xác minh khép kín qua Webhook kết nối bảo vệ bằng chữ ký số.
            </p>
          </section>

          <section>
            <h2 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <CheckCircle2 size={18} style={{ color: 'var(--success)' }} /> 4. Cam Kết Không Chia Sẻ Dữ Liệu
            </h2>
            <p>
              Movie Booking tuyệt đối không bán, trao đổi hoặc chia sẻ thông tin cá nhân của khách hàng cho bên thứ ba vì mục đích thương mại. Thông tin chỉ được cung cấp cho các cụm rạp đối tác khi khách hàng đến check-in vé.
            </p>
          </section>

        </div>

        <div style={{ marginTop: '40px', borderTop: '1px solid var(--border-color)', paddingTop: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
          <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Mọi câu hỏi về bảo mật riêng tư xin gửi tới email bảo vệ dữ liệu</span>
          <Link to="/contact" className="btn btn-secondary" style={{ padding: '8px 16px', fontSize: '13px' }}>
            Liên hệ ban quản trị
          </Link>
        </div>
      </div>
    </div>
  );
};

export default PrivacyPolicy;
