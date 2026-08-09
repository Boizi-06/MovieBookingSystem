import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { api } from '../services/api';
import { CheckCircle2, Ticket, QrCode, Calendar, MapPin, Film, Home, ArrowLeft, Printer } from 'lucide-react';

const BookingSuccess: React.FC = () => {
  const { bookingCode } = useParams<{ bookingCode: string }>();
  const navigate = useNavigate();

  const [booking, setBooking] = useState<any | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  const fetchBookingDetail = async () => {
    if (!bookingCode) return;
    setLoading(true);
    try {
      const response = await api.get('/api/v1/bookings/my-history');
      if (response.data?.success) {
        const history = response.data.data || [];
        const found = history.find((b: any) => b.bookingCode === bookingCode);
        if (found) {
          setBooking(found);
        }
      }
    } catch (err) {
      console.error('Error fetching booking success detail:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBookingDetail();
  }, [bookingCode]);

  const formatVND = (value: number) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
  };

  const handlePrint = () => {
    window.print();
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <div className="spinner"></div>
      </div>
    );
  }

  return (
    <div className="animate-fade-in" style={{ maxWidth: '640px', margin: '20px auto', padding: '0 20px', textAlign: 'center' }}>
      
      {/* Success Badge Banner */}
      <div style={{ display: 'inline-flex', alignItems: 'center', gap: '8px', padding: '10px 20px', borderRadius: '30px', backgroundColor: 'rgba(34, 197, 94, 0.15)', border: '1px solid rgba(34, 197, 94, 0.3)', color: '#4ade80', fontWeight: 800, fontSize: '16px', marginBottom: '24px' }}>
        <CheckCircle2 size={22} /> ĐẶT VÉ & THANH TOÁN THÀNH CÔNG!
      </div>

      <h1 style={{ fontSize: '24px', fontWeight: 800, marginBottom: '8px' }}>Vé Xem Phim Điện Tử</h1>
      <p style={{ color: 'var(--text-secondary)', fontSize: '14px', marginBottom: '16px' }}>
        Cảm ơn bạn đã sử dụng dịch vụ. Hãy xuất mã QR code dưới đây cho nhân viên kiểm soát tại rạp.
      </p>

      {/* Email Notification Alert */}
      <div style={{ padding: '12px 16px', borderRadius: '8px', backgroundColor: 'rgba(56, 189, 248, 0.1)', border: '1px solid rgba(56, 189, 248, 0.3)', color: '#38bdf8', fontSize: '13px', marginBottom: '24px', textAlign: 'center' }}>
        📧 Thông tin vé đã được hệ thống tự động gửi tới Email của bạn. Hãy kiểm tra hộp thư đến nhé!
      </div>

      {/* Digital Ticket Stub Card */}
      <div style={{ 
        padding: '0', 
        borderRadius: '24px', 
        overflow: 'hidden', 
        border: '1px solid var(--border-color)', 
        boxShadow: '0 20px 40px -15px rgba(0, 0, 0, 0.4)', 
        backgroundColor: 'var(--bg-card)',
        textAlign: 'left',
        position: 'relative'
      }}>
        
        {/* Ticket Header Banner */}
        <div style={{ background: 'linear-gradient(135deg, #4f46e5 0%, #7c3aed 50%, #db2777 100%)', padding: '24px 28px', color: '#fff', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <span style={{ fontSize: '11px', textTransform: 'uppercase', letterSpacing: '2px', opacity: 0.9, fontWeight: 800 }}>ADMIT ONE • MOVIE TICKET</span>
            <h2 style={{ fontSize: '22px', fontWeight: 900, margin: '6px 0 0 0', textShadow: '0 2px 4px rgba(0,0,0,0.2)' }}>{booking?.movieTitle || 'Vé Xem Phim'}</h2>
          </div>
          <div style={{ padding: '10px', borderRadius: '50%', backgroundColor: 'rgba(255,255,255,0.15)', backdropFilter: 'blur(4px)' }}>
            <Ticket size={28} />
          </div>
        </div>

        {/* Ticket Body Details */}
        <div style={{ padding: '28px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
          
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '18px', fontSize: '14px' }}>
            <div>
              <span style={{ fontSize: '12px', color: 'var(--text-secondary)', display: 'block', marginBottom: '4px', fontWeight: 600 }}>Cụm Rạp Chiếu</span>
              <strong style={{ color: 'var(--text-primary)', fontSize: '15px' }}>{booking?.cinemaName || 'Tên rạp'}</strong>
            </div>

            <div>
              <span style={{ fontSize: '12px', color: 'var(--text-secondary)', display: 'block', marginBottom: '4px', fontWeight: 600 }}>Phòng Chiếu</span>
              <strong style={{ color: 'var(--text-primary)', fontSize: '15px' }}>{booking?.roomName || 'Phòng 01'}</strong>
            </div>

            <div>
              <span style={{ fontSize: '12px', color: 'var(--text-secondary)', display: 'block', marginBottom: '4px', fontWeight: 600 }}>Suất Chiếu</span>
              <strong style={{ color: 'var(--primary)', fontSize: '15px' }}>{booking?.startTime ? new Date(booking.startTime).toLocaleString('vi-VN') : ''}</strong>
            </div>

            <div>
              <span style={{ fontSize: '12px', color: 'var(--text-secondary)', display: 'block', marginBottom: '4px', fontWeight: 600 }}>Vị Trí Ghế Ngồi</span>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
                {booking?.seatCodes?.map((code: string) => (
                  <span key={code} style={{ display: 'inline-block', padding: '3px 10px', borderRadius: '6px', backgroundColor: 'rgba(99, 102, 241, 0.15)', color: 'var(--primary)', border: '1px solid rgba(99, 102, 241, 0.3)', fontSize: '12px', fontWeight: 800 }}>
                    {code}
                  </span>
                ))}
              </div>
            </div>
          </div>

          {/* QR Code Perforated Stub Section */}
          <div style={{ borderTop: '2px dashed var(--border-color)', borderBottom: '2px dashed var(--border-color)', padding: '24px 0', margin: '6px 0', display: 'flex', alignItems: 'center', justifyContent: 'space-around', flexWrap: 'wrap', gap: '20px', backgroundColor: 'var(--bg-main)', borderRadius: '12px' }}>
            <div style={{ padding: '12px', backgroundColor: '#ffffff', borderRadius: '12px', boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}>
              <img 
                src={`https://api.qrserver.com/v1/create-qr-code/?size=140x140&data=${bookingCode}`} 
                alt="Ticket QR Code" 
                style={{ width: '130px', height: '130px', display: 'block' }}
              />
            </div>

            <div style={{ textAlign: 'left', fontSize: '13px' }}>
              <span style={{ fontSize: '12px', color: 'var(--text-secondary)', display: 'block', marginBottom: '4px', fontWeight: 600 }}>Mã Vé Đặt (Booking Code)</span>
              <strong style={{ fontFamily: 'monospace', fontSize: '18px', color: 'var(--primary)', letterSpacing: '1px' }}>{bookingCode}</strong>

              <div style={{ marginTop: '12px' }}>
                <span style={{ fontSize: '12px', color: 'var(--text-secondary)', display: 'block', marginBottom: '4px', fontWeight: 600 }}>Mã Ghế Chi Tiết:</span>
                {booking?.ticketCodes?.map((tCode: string) => (
                  <div key={tCode} style={{ fontFamily: 'monospace', fontSize: '12px', color: '#ec4899', fontWeight: 700 }}>
                    • {tCode}
                  </div>
                ))}
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '14px' }}>
            <span style={{ color: 'var(--text-secondary)', fontWeight: 600 }}>Tổng thanh toán:</span>
            <strong style={{ fontSize: '22px', color: '#22c55e', fontWeight: 900 }}>{formatVND(booking?.totalPrice || 0)}</strong>
          </div>

        </div>

      </div>

      {/* Action Buttons */}
      <div style={{ display: 'flex', justifyContent: 'center', gap: '16px', marginTop: '32px' }}>
        <Link to="/" className="btn btn-primary" style={{ padding: '12px 28px', borderRadius: 'var(--radius-sm)', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '8px', fontWeight: 700 }}>
          <Home size={18} /> Quay lại trang chủ
        </Link>

        <Link to="/my-bookings" className="btn btn-secondary" style={{ padding: '12px 24px', borderRadius: 'var(--radius-sm)', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '8px', fontWeight: 600 }}>
          <Ticket size={18} /> Xem lịch sử đặt vé
        </Link>
      </div>

    </div>
  );
};

export default BookingSuccess;
