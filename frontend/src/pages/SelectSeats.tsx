import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { ArrowLeft, Armchair, Clock, MapPin, Film, ShieldAlert, AlertCircle, CheckCircle2, Ticket } from 'lucide-react';

const SelectSeats: React.FC = () => {
  const { showtimeId } = useParams<{ showtimeId: string }>();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();

  const [seats, setSeats] = useState<any[]>([]);
  const [showtimeDetail, setShowtimeDetail] = useState<any | null>(null);
  const [selectedSeatIds, setSelectedSeatIds] = useState<number[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  const fetchSeatsAndShowtime = async () => {
    if (!showtimeId) return;
    setLoading(true);
    setError('');
    try {
      // 1. Fetch seat grid for showtime
      const seatsRes = await api.get(`/api/v1/seats/showtime/${showtimeId}`);
      if (seatsRes.data?.success) {
        setSeats(seatsRes.data.data || []);
      }

      // 2. Fetch showtime details
      const showtimesRes = await api.get('/api/v1/showtimes');
      if (showtimesRes.data?.success) {
        const found = showtimesRes.data.data.find((s: any) => s.id === Number(showtimeId));
        if (found) {
          setShowtimeDetail(found);
        }
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Không thể tải sơ đồ ghế.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSeatsAndShowtime();
  }, [showtimeId]);

  const handleSeatClick = (seat: any) => {
    if (seat.status !== 'AVAILABLE') return; // Cannot select BOOKED, HOLD, or MAINTENANCE

    if (selectedSeatIds.includes(seat.seatId)) {
      setSelectedSeatIds(selectedSeatIds.filter(id => id !== seat.seatId));
    } else {
      setSelectedSeatIds([...selectedSeatIds, seat.seatId]);
    }
  };

  const selectedSeats = seats.filter(s => selectedSeatIds.includes(s.seatId));
  const totalPrice = selectedSeats.reduce((sum, s) => sum + (s.price || 0), 0);

  const formatVND = (value: number) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
  };

  const handleProceedToBooking = async () => {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: { pathname: `/booking/seats/${showtimeId}` } } });
      return;
    }

    if (selectedSeatIds.length === 0) {
      setError('Vui lòng chọn ít nhất một vị trí ghế.');
      return;
    }

    setSubmitting(true);
    setError('');
    try {
      const response = await api.post('/api/v1/bookings', {
        showtimeId: Number(showtimeId),
        seatIds: selectedSeatIds
      });

      if (response.data?.success) {
        const booking = response.data.data;
        navigate(`/booking/payment/${booking.id}`);
      }
    } catch (err: any) {
      if (err.response?.status === 403 || err.response?.status === 401) {
        setError('Phiên đăng nhập đã hết hạn hoặc bạn cần đăng nhập tài khoản Khách hàng để giữ ghế.');
        setTimeout(() => {
          navigate('/login', { state: { from: { pathname: `/booking/seats/${showtimeId}` } } });
        }, 1500);
      } else {
        setError(err.response?.data?.message || err.message || 'Đặt ghế giữ chỗ thất bại.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const rows = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <div className="spinner"></div>
      </div>
    );
  }

  return (
    <div className="animate-fade-in" style={{ maxWidth: '1200px', margin: '0 auto', padding: '20px', textAlign: 'left' }}>
      
      {/* Header / Back Link */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <button 
          onClick={() => navigate(-1)} 
          style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '6px', fontSize: '14px', fontWeight: 600 }}
        >
          <ArrowLeft size={18} /> Quay lại
        </button>

        {showtimeDetail && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px', fontSize: '14px' }}>
            <span style={{ fontWeight: 700, color: 'var(--primary)', display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Film size={18} /> {showtimeDetail.movieTitle}
            </span>
            <span style={{ color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '6px' }}>
              <MapPin size={16} /> {showtimeDetail.cinemaName} ({showtimeDetail.roomName})
            </span>
            <span style={{ color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Clock size={16} /> {showtimeDetail.startTime ? new Date(showtimeDetail.startTime).toLocaleString('vi-VN') : ''}
            </span>
          </div>
        )}
      </div>

      {/* Alert Error */}
      {error && (
        <div style={{ padding: '12px 16px', borderRadius: 'var(--radius-sm)', backgroundColor: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.3)', color: '#f87171', display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '20px' }}>
          <AlertCircle size={18} /> {error}
        </div>
      )}

      {/* Main Grid & Checkout Panel Layout */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: '24px', flexWrap: 'wrap' }}>
        
        {/* Left Column: Visual Seat Selection Grid */}
        <div className="glass-card" style={{ padding: '24px', textAlign: 'center' }}>
          
          {/* Screen Visual Indicator Curved Cinema Glow */}
          <div style={{ marginBottom: '36px', perspective: '400px' }}>
            <div style={{
              width: '85%',
              height: '14px',
              margin: '0 auto 10px auto',
              borderTop: '3px solid var(--primary)',
              borderRadius: '50% 50% 0 0 / 100% 100% 0 0',
              boxShadow: '0 -6px 20px rgba(99, 102, 241, 0.5), inset 0 2px 10px rgba(99, 102, 241, 0.3)',
              background: 'linear-gradient(180deg, rgba(99, 102, 241, 0.25) 0%, transparent 100%)'
            }} />
            <span style={{ fontSize: '11px', color: 'var(--text-muted)', letterSpacing: '4px', textTransform: 'uppercase', fontWeight: 800 }}>MÀN HÌNH CHIẾU</span>
          </div>

          {/* Legend */}
          <div style={{ display: 'flex', justifyContent: 'center', gap: '18px', marginBottom: '32px', flexWrap: 'wrap', fontSize: '12px', fontWeight: 600, padding: '10px 16px', borderRadius: '12px', backgroundColor: 'var(--bg-main)', border: '1px solid var(--border-color)' }}>
            <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <span style={{ width: '14px', height: '14px', borderRadius: '4px', backgroundColor: '#3b82f6' }} /> Ghế Thường
            </span>
            <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <span style={{ width: '14px', height: '14px', borderRadius: '4px', backgroundColor: '#8b5cf6' }} /> Ghế VIP
            </span>
            <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <span style={{ width: '14px', height: '14px', borderRadius: '4px', backgroundColor: '#ec4899' }} /> Sweetbox (Đôi)
            </span>
            <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <span style={{ width: '14px', height: '14px', borderRadius: '4px', backgroundColor: '#22c55e', boxShadow: '0 0 8px rgba(34, 197, 94, 0.5)' }} /> Đang chọn
            </span>
            <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <span style={{ width: '14px', height: '14px', borderRadius: '4px', backgroundColor: '#475569', opacity: 0.6 }} /> Đã bán / Giữ
            </span>
          </div>

          {/* Seat Grid */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', alignItems: 'center', overflowX: 'auto', paddingBottom: '16px' }}>
            {rows.map(row => {
              const rowSeats = seats.filter(s => s.seatRow === row);
              if (rowSeats.length === 0) return null;

              return (
                <div key={row} style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <span style={{ width: '20px', fontWeight: 700, fontSize: '13px', color: 'var(--text-secondary)' }}>{row}</span>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    {rowSeats.map(seat => {
                      const isSelected = selectedSeatIds.includes(seat.seatId);
                      const isCouple = seat.seatType === 'SWEETBOX' || seat.seatType === 'COUPLE';
                      const isOccupied = seat.status === 'BOOKED' || seat.status === 'HOLD' || seat.status === 'MAINTENANCE';

                      let bg = '#3b82f6';
                      if (seat.seatType === 'VIP') bg = '#8b5cf6';
                      if (isCouple) bg = '#ec4899';
                      if (isSelected) bg = '#22c55e';
                      if (isOccupied) bg = '#475569';

                      return (
                        <button
                          key={seat.seatId}
                          onClick={() => handleSeatClick(seat)}
                          disabled={isOccupied}
                          title={`Ghế ${seat.seatCode} (${seat.seatType}) - ${formatVND(seat.price)}`}
                          style={{
                            width: isCouple ? '76px' : '36px',
                            height: '36px',
                            borderRadius: '6px',
                            border: isSelected ? '2px solid #ffffff' : 'none',
                            backgroundColor: bg,
                            color: '#fff',
                            fontSize: '11px',
                            fontWeight: 700,
                            cursor: isOccupied ? 'not-allowed' : 'pointer',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            opacity: isOccupied ? 0.4 : 1,
                            transform: isSelected ? 'scale(1.08)' : 'scale(1)',
                            transition: 'all 0.15s ease',
                            boxShadow: isSelected ? '0 0 12px rgba(34, 197, 94, 0.6)' : 'none'
                          }}
                        >
                          {seat.seatCode}
                        </button>
                      );
                    })}
                  </div>
                </div>
              );
            })}
          </div>

        </div>

        {/* Right Column: Booking Summary Card */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div className="glass-card" style={{ padding: '24px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', height: '100%' }}>
            <div>
              <h3 style={{ fontSize: '18px', fontWeight: 800, marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--primary)' }}>
                <Ticket size={20} /> Thông tin Vé chọn
              </h3>

              {showtimeDetail && (
                <div style={{ marginBottom: '20px', borderBottom: '1px solid var(--border-color)', paddingBottom: '16px', fontSize: '13px', display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  <div><strong>Phim:</strong> {showtimeDetail.movieTitle}</div>
                  <div><strong>Rạp:</strong> {showtimeDetail.cinemaName}</div>
                  <div><strong>Phòng:</strong> {showtimeDetail.roomName}</div>
                  <div><strong>Suất chiếu:</strong> {showtimeDetail.startTime ? new Date(showtimeDetail.startTime).toLocaleString('vi-VN') : ''}</div>
                </div>
              )}

              {/* Selected Seats Chips */}
              <div style={{ marginBottom: '20px' }}>
                <label style={{ display: 'block', fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '8px', fontWeight: 600 }}>
                  Vị trí ghế đã chọn ({selectedSeats.length}):
                </label>
                {selectedSeats.length === 0 ? (
                  <p style={{ fontSize: '13px', color: 'var(--text-muted)', fontStyle: 'italic', margin: 0 }}>Chưa chọn ghế nào.</p>
                ) : (
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
                    {selectedSeats.map(seat => (
                      <span key={seat.seatId} style={{ padding: '4px 10px', borderRadius: '4px', backgroundColor: 'var(--primary)', color: '#fff', fontSize: '12px', fontWeight: 700 }}>
                        {seat.seatCode} ({formatVND(seat.price)})
                      </span>
                    ))}
                  </div>
                )}
              </div>
            </div>

            {/* Total & Action */}
            <div style={{ borderTop: '1px solid var(--border-color)', paddingTop: '16px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <span style={{ fontSize: '14px', fontWeight: 600, color: 'var(--text-secondary)' }}>TỔNG CỘNG:</span>
                <span style={{ fontSize: '22px', fontWeight: 800, color: '#22c55e' }}>{formatVND(totalPrice)}</span>
              </div>

              <button
                onClick={handleProceedToBooking}
                disabled={submitting || selectedSeatIds.length === 0}
                className="btn-primary"
                style={{ width: '100%', padding: '12px', borderRadius: 'var(--radius-sm)', fontWeight: 700, fontSize: '15px', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '8px' }}
              >
                {submitting ? 'Đang tạo giữ ghế...' : 'Xác nhận Giữ Ghế (5 Phút)'}
              </button>
            </div>
          </div>
        </div>

      </div>

    </div>
  );
};

export default SelectSeats;
