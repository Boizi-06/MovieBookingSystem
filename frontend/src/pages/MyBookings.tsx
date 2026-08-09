import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import { Ticket, ArrowLeft, Calendar, MapPin, Film, Clock, AlertCircle, CheckCircle2, QrCode, CreditCard } from 'lucide-react';

const MyBookings: React.FC = () => {
  const navigate = useNavigate();
  const [bookingHistory, setBookingHistory] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  const fetchHistory = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get('/api/v1/bookings/my-history');
      if (response.data?.success) {
        setBookingHistory(response.data.data || []);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Không thể tải lịch sử đặt vé.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const formatVND = (value: number) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
  };

  return (
    <div className="animate-fade-in" style={{ maxWidth: '1100px', margin: '0 auto', padding: '0 20px 40px 20px', textAlign: 'left' }}>
      
      {/* Header & Back Link */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <button
          onClick={() => navigate(-1)}
          style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '6px', fontSize: '14px', fontWeight: 600 }}
        >
          <ArrowLeft size={18} /> Quay lại
        </button>

        <h2 style={{ fontSize: '24px', fontWeight: 800, margin: 0, display: 'flex', alignItems: 'center', gap: '10px', color: 'var(--text-primary)' }}>
          <Ticket size={24} style={{ color: 'var(--danger)' }} /> Lịch Sử Đặt Vé Của Bạn
        </h2>

        <div style={{ width: '80px' }}></div>
      </div>

      {error && (
        <div className="glass-card" style={{ padding: '16px', marginBottom: '24px', display: 'flex', alignItems: 'center', gap: '10px', borderColor: 'var(--danger)', color: 'var(--danger)' }}>
          <AlertCircle size={20} />
          <span style={{ fontSize: '14px', fontWeight: 600 }}>{error}</span>
        </div>
      )}

      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '80px 0' }}>
          <div className="spinner"></div>
        </div>
      ) : bookingHistory.length === 0 ? (
        <div className="glass-card" style={{ padding: '60px 20px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          <Ticket size={48} style={{ opacity: 0.3, marginBottom: '16px' }} />
          <h3 style={{ fontSize: '18px', fontWeight: 700, margin: '0 0 8px 0', color: 'var(--text-primary)' }}>Bạn chưa có vé phim nào</h3>
          <p style={{ fontSize: '14px', marginBottom: '24px' }}>Hãy chọn một bộ phim yêu thích và trải nghiệm đặt vé ngay hôm nay!</p>
          <Link to="/" className="btn btn-primary" style={{ padding: '12px 24px', fontSize: '14px', fontWeight: 700 }}>
            Đặt Vé Xem Phim Ngay
          </Link>
        </div>
      ) : (
        <div className="glass-card" style={{ padding: '24px', borderRadius: 'var(--radius-lg)' }}>
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px', textAlign: 'left' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-secondary)' }}>
                  <th style={{ padding: '14px' }}>Mã đặt vé</th>
                  <th style={{ padding: '14px' }}>Tên phim</th>
                  <th style={{ padding: '14px' }}>Rạp & Phòng chiếu</th>
                  <th style={{ padding: '14px' }}>Thời gian chiếu</th>
                  <th style={{ padding: '14px' }}>Vị trí ghế</th>
                  <th style={{ padding: '14px' }}>Tổng tiền</th>
                  <th style={{ padding: '14px' }}>Trạng thái</th>
                  <th style={{ padding: '14px', textAlign: 'center' }}>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {bookingHistory.map(item => (
                  <tr key={item.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                    <td style={{ padding: '14px', fontWeight: 800, fontFamily: 'monospace', color: 'var(--primary)' }}>
                      {item.bookingCode}
                    </td>
                    <td style={{ padding: '14px', fontWeight: 700, color: 'var(--text-primary)' }}>
                      {item.movieTitle}
                    </td>
                    <td style={{ padding: '14px', fontSize: '13px', color: 'var(--text-secondary)' }}>
                      <div style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{item.cinemaName}</div>
                      <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{item.roomName}</div>
                    </td>
                    <td style={{ padding: '14px', fontSize: '13px', color: 'var(--text-primary)' }}>
                      {item.startTime ? new Date(item.startTime).toLocaleString('vi-VN') : '-'}
                    </td>
                    <td style={{ padding: '14px' }}>
                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
                        {item.seatCodes?.map((code: string) => (
                          <span key={code} style={{
                            padding: '3px 8px',
                            borderRadius: '4px',
                            backgroundColor: 'rgba(99, 102, 241, 0.15)',
                            border: '1px solid rgba(99, 102, 241, 0.3)',
                            color: 'var(--primary)',
                            fontSize: '11px',
                            fontWeight: 800
                          }}>
                            {code}
                          </span>
                        ))}
                      </div>
                    </td>
                    <td style={{ padding: '14px', fontWeight: 800, color: '#22c55e', fontSize: '15px' }}>
                      {formatVND(item.totalPrice)}
                    </td>
                    <td style={{ padding: '14px' }}>
                      <span className={`badge ${item.status === 'PAID' ? 'badge-success' : item.status === 'PENDING_PAYMENT' ? 'badge-info' : 'badge-danger'}`} style={{ fontSize: '11px', padding: '4px 8px' }}>
                        {item.status === 'PAID' ? 'Đã thanh toán' : item.status === 'PENDING_PAYMENT' ? 'Chờ thanh toán' : item.status === 'CANCELLED' ? 'Đã hủy' : item.status}
                      </span>
                    </td>
                    <td style={{ padding: '14px', textAlign: 'center' }}>
                      {item.status === 'PAID' ? (
                        <Link
                          to={`/booking/success/${item.bookingCode}`}
                          className="btn btn-secondary"
                          style={{ padding: '6px 12px', fontSize: '12px', fontWeight: 700, gap: '4px', textDecoration: 'none', display: 'inline-flex', alignItems: 'center' }}
                        >
                          <QrCode size={14} /> Xem QR Vé
                        </Link>
                      ) : item.status === 'PENDING_PAYMENT' ? (
                        <Link
                          to={`/booking/payment/${item.id}`}
                          className="btn btn-primary"
                          style={{ padding: '6px 12px', fontSize: '12px', fontWeight: 700, gap: '4px', textDecoration: 'none', display: 'inline-flex', alignItems: 'center' }}
                        >
                          <CreditCard size={14} /> Thanh toán
                        </Link>
                      ) : (
                        <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>-</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default MyBookings;
