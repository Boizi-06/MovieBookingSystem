import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';
import AdminLayout from '../../components/AdminLayout';
import { Ticket, Search, Filter, AlertCircle, Eye, X, Calendar, MapPin, Film, CheckCircle2, Clock, XCircle } from 'lucide-react';

const ManageBookings: React.FC = () => {
  const [bookings, setBookings] = useState<any[]>([]);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [currentPage, setCurrentPage] = useState<number>(0);
  const [totalElements, setTotalElements] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  // Filters State
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [movieIdFilter, setMovieIdFilter] = useState<string>('');
  const [startDateFilter, setStartDateFilter] = useState<string>('');
  const [endDateFilter, setEndDateFilter] = useState<string>('');

  const [movies, setMovies] = useState<any[]>([]);

  // Modal State
  const [selectedBooking, setSelectedBooking] = useState<any | null>(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState<boolean>(false);

  const fetchMovies = async () => {
    try {
      const response = await api.get('/api/v1/movies?size=100');
      if (response.data?.success) {
        setMovies(response.data.data?.content || []);
      }
    } catch (e) {
      console.error('Lỗi tải danh sách phim cho bộ lọc:', e);
    }
  };

  const fetchBookings = async (page = 0) => {
    setLoading(true);
    setError('');
    try {
      let queryParams = `?page=${page}&size=10&sort=id,desc`;
      if (searchQuery) queryParams += `&search=${encodeURIComponent(searchQuery)}`;
      if (statusFilter) queryParams += `&status=${encodeURIComponent(statusFilter)}`;
      if (movieIdFilter) queryParams += `&movieId=${movieIdFilter}`;
      if (startDateFilter) queryParams += `&startDate=${startDateFilter}`;
      if (endDateFilter) queryParams += `&endDate=${endDateFilter}`;

      const response = await api.get(`/api/v1/bookings/admin${queryParams}`);
      if (response.data?.success) {
        const data = response.data.data;
        setBookings(data.content || []);
        setTotalPages(data.totalPages || 1);
        setTotalElements(data.totalElements || 0);
        setCurrentPage(data.number || 0);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Không thể tải danh sách đơn đặt vé.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMovies();
  }, []);

  useEffect(() => {
    fetchBookings(0);
  }, [searchQuery, statusFilter, movieIdFilter, startDateFilter, endDateFilter]);

  const handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < totalPages) {
      fetchBookings(newPage);
    }
  };

  const handleOpenDetailModal = (booking: any) => {
    setSelectedBooking(booking);
    setIsDetailModalOpen(true);
  };

  const formatVND = (value: number) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'PAID':
        return (
          <span style={{ padding: '4px 10px', borderRadius: '12px', fontSize: '12px', fontWeight: 700, backgroundColor: 'rgba(34, 197, 94, 0.15)', color: '#4ade80', border: '1px solid rgba(34, 197, 94, 0.3)', display: 'inline-flex', alignItems: 'center', gap: '4px' }}>
            <CheckCircle2 size={12} /> Đã thanh toán
          </span>
        );
      case 'PENDING_PAYMENT':
        return (
          <span style={{ padding: '4px 10px', borderRadius: '12px', fontSize: '12px', fontWeight: 700, backgroundColor: 'rgba(234, 179, 8, 0.15)', color: '#facc15', border: '1px solid rgba(234, 179, 8, 0.3)', display: 'inline-flex', alignItems: 'center', gap: '4px' }}>
            <Clock size={12} /> Chờ thanh toán
          </span>
        );
      case 'CANCELLED':
        return (
          <span style={{ padding: '4px 10px', borderRadius: '12px', fontSize: '12px', fontWeight: 700, backgroundColor: 'rgba(239, 68, 68, 0.15)', color: '#f87171', border: '1px solid rgba(239, 68, 68, 0.3)', display: 'inline-flex', alignItems: 'center', gap: '4px' }}>
            <XCircle size={12} /> Đã hủy
          </span>
        );
      default:
        return (
          <span style={{ padding: '4px 10px', borderRadius: '12px', fontSize: '12px', fontWeight: 700, backgroundColor: 'rgba(148, 163, 184, 0.15)', color: '#94a3b8' }}>
            {status}
          </span>
        );
    }
  };

  return (
    <AdminLayout>
      <div className="animate-fade-in" style={{ textAlign: 'left' }}>
        
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
          <div>
            <h1 style={{ fontSize: '26px', fontWeight: 800, margin: '0 0 4px 0' }}>Quản lý Đơn đặt vé</h1>
            <p style={{ color: 'var(--text-secondary)', fontSize: '14px', margin: 0 }}>Tra cứu, tìm kiếm và kiểm tra toàn bộ vé đã xuất trong hệ thống.</p>
          </div>
        </div>

        {/* Filters */}
        <div className="glass-card" style={{ padding: '16px', marginBottom: '24px', display: 'flex', gap: '12px', flexWrap: 'wrap', alignItems: 'center' }}>
          {/* Search Query */}
          <div style={{ flex: '1 1 200px', display: 'flex', alignItems: 'center', gap: '8px', backgroundColor: 'var(--bg-card)', padding: '8px 14px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)' }}>
            <Search size={18} style={{ color: 'var(--text-secondary)' }} />
            <input 
              type="text"
              placeholder="Tìm theo Mã đặt vé (BKG-...) hoặc Email..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={{ width: '100%', border: 'none', background: 'transparent', color: 'var(--text-main)', fontSize: '14px', outline: 'none' }}
            />
          </div>

          {/* Movie Filter */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <Film size={16} style={{ color: 'var(--text-secondary)' }} />
            <select 
              value={movieIdFilter}
              onChange={(e) => setMovieIdFilter(e.target.value)}
              style={{ padding: '8px 12px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-card)', color: 'var(--text-main)', fontSize: '13px', outline: 'none', maxWidth: '180px' }}
            >
              <option value="">Tất cả các Phim</option>
              {movies.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.title}
                </option>
              ))}
            </select>
          </div>

          {/* Status Filter */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <Filter size={16} style={{ color: 'var(--text-secondary)' }} />
            <select 
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              style={{ padding: '8px 12px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-card)', color: 'var(--text-main)', fontSize: '13px', outline: 'none' }}
            >
              <option value="">Tất cả trạng thái</option>
              <option value="PAID">Đã thanh toán (PAID)</option>
              <option value="PENDING_PAYMENT">Chờ thanh toán (PENDING)</option>
              <option value="CANCELLED">Đã hủy (CANCELLED)</option>
            </select>
          </div>

          {/* Date Range Filters */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <Calendar size={16} style={{ color: 'var(--text-secondary)' }} />
            <input 
              type="date"
              title="Từ ngày"
              value={startDateFilter}
              onChange={(e) => setStartDateFilter(e.target.value)}
              style={{ padding: '8px 10px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-card)', color: 'var(--text-main)', fontSize: '13px', outline: 'none' }}
            />
            <span style={{ color: 'var(--text-secondary)', fontSize: '12px' }}>đến</span>
            <input 
              type="date"
              title="Đến ngày"
              value={endDateFilter}
              onChange={(e) => setEndDateFilter(e.target.value)}
              style={{ padding: '8px 10px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-card)', color: 'var(--text-main)', fontSize: '13px', outline: 'none' }}
            />
          </div>

          {/* Reset Filters Button */}
          {(searchQuery || statusFilter || movieIdFilter || startDateFilter || endDateFilter) && (
            <button
              onClick={() => {
                setSearchQuery('');
                setStatusFilter('');
                setMovieIdFilter('');
                setStartDateFilter('');
                setEndDateFilter('');
              }}
              style={{ padding: '8px 14px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', background: 'transparent', color: 'var(--primary)', fontSize: '13px', fontWeight: 600, cursor: 'pointer', display: 'inline-flex', alignItems: 'center', gap: '4px' }}
            >
              <X size={14} /> Xóa bộ lọc
            </button>
          )}
        </div>

        {/* Error Alert */}
        {error && (
          <div style={{ padding: '12px 16px', borderRadius: 'var(--radius-sm)', backgroundColor: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.3)', color: '#f87171', display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '20px' }}>
            <AlertCircle size={18} /> {error}
          </div>
        )}

        {/* Bookings Table */}
        <div className="glass-card" style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px', textAlign: 'left' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--border-color)', color: 'var(--text-secondary)' }}>
                <th style={{ padding: '14px 16px' }}>Mã đặt vé</th>
                <th style={{ padding: '14px 16px' }}>Tên Phim</th>
                <th style={{ padding: '14px 16px' }}>Rạp / Phòng</th>
                <th style={{ padding: '14px 16px' }}>Suất chiếu</th>
                <th style={{ padding: '14px 16px' }}>Ghế ngồi</th>
                <th style={{ padding: '14px 16px' }}>Tổng tiền</th>
                <th style={{ padding: '14px 16px' }}>Trạng thái</th>
                <th style={{ padding: '14px 16px', textAlign: 'center' }}>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={8} style={{ textAlign: 'center', padding: '40px', color: 'var(--text-secondary)' }}>
                    Đang tải danh sách vé...
                  </td>
                </tr>
              ) : bookings.length === 0 ? (
                <tr>
                  <td colSpan={8} style={{ textAlign: 'center', padding: '40px', color: 'var(--text-secondary)' }}>
                    Không tìm thấy đơn đặt vé nào.
                  </td>
                </tr>
              ) : (
                bookings.map((booking) => (
                  <tr key={booking.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                    <td style={{ padding: '14px 16px', fontWeight: 700, fontFamily: 'monospace', color: 'var(--primary)' }}>
                      {booking.bookingCode}
                    </td>
                    <td style={{ padding: '14px 16px', fontWeight: 600 }}>
                      {booking.movieTitle}
                    </td>
                    <td style={{ padding: '14px 16px', color: 'var(--text-secondary)', fontSize: '13px' }}>
                      {booking.cinemaName} - <strong>{booking.roomName}</strong>
                    </td>
                    <td style={{ padding: '14px 16px', fontSize: '13px' }}>
                      {booking.startTime ? new Date(booking.startTime).toLocaleString('vi-VN') : ''}
                    </td>
                    <td style={{ padding: '14px 16px' }}>
                      {booking.seatCodes?.map((code: string) => (
                        <span key={code} style={{ display: 'inline-block', padding: '2px 6px', margin: '2px', borderRadius: '4px', backgroundColor: 'var(--bg-card)', border: '1px solid var(--border-color)', fontSize: '12px', fontWeight: 700 }}>
                          {code}
                        </span>
                      ))}
                    </td>
                    <td style={{ padding: '14px 16px', fontWeight: 700, color: '#22c55e' }}>
                      {formatVND(booking.totalPrice)}
                    </td>
                    <td style={{ padding: '14px 16px' }}>
                      {getStatusBadge(booking.status)}
                    </td>
                    <td style={{ padding: '14px 16px', textAlign: 'center' }}>
                      <button
                        onClick={() => handleOpenDetailModal(booking)}
                        style={{ padding: '6px 12px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', background: 'transparent', color: 'var(--text-main)', cursor: 'pointer', display: 'inline-flex', alignItems: 'center', gap: '6px', fontSize: '13px' }}
                      >
                        <Eye size={14} /> Chi tiết
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '20px', flexWrap: 'wrap', gap: '12px' }}>
            <span style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
              Hiển thị tổng cộng <strong>{totalElements}</strong> đơn đặt vé
            </span>
            <div style={{ display: 'flex', gap: '8px' }}>
              <button
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 0}
                style={{ padding: '6px 12px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', background: 'var(--bg-card)', color: 'var(--text-main)', cursor: currentPage === 0 ? 'not-allowed' : 'pointer', opacity: currentPage === 0 ? 0.5 : 1 }}
              >
                Trước
              </button>
              <span style={{ display: 'flex', alignItems: 'center', padding: '0 12px', fontSize: '13px', fontWeight: 600 }}>
                Trang {currentPage + 1} / {totalPages}
              </span>
              <button
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage >= totalPages - 1}
                style={{ padding: '6px 12px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', background: 'var(--bg-card)', color: 'var(--text-main)', cursor: currentPage >= totalPages - 1 ? 'not-allowed' : 'pointer', opacity: currentPage >= totalPages - 1 ? 0.5 : 1 }}
              >
                Sau
              </button>
            </div>
          </div>
        )}

        {/* Ticket Detail Modal */}
        {isDetailModalOpen && selectedBooking && (
          <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(15, 23, 42, 0.85)', backdropFilter: 'blur(8px)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000, padding: '20px' }}>
            <div className="animate-fade-in" style={{ backgroundColor: 'var(--bg-card)', color: 'var(--text-main)', width: '100%', maxWidth: '540px', padding: '28px', borderRadius: '16px', border: '1px solid var(--border-color)', boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.7), 0 0 0 1px rgba(255, 255, 255, 0.1)', position: 'relative' }}>
              <button onClick={() => setIsDetailModalOpen(false)} style={{ position: 'absolute', top: '16px', right: '16px', background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}>
                <X size={20} />
              </button>

              <h2 style={{ fontSize: '20px', fontWeight: 800, marginBottom: '4px', display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--primary)' }}>
                <Ticket size={22} /> Chi Tiết Đơn Đặt Vé
              </h2>
              <p style={{ color: 'var(--text-secondary)', fontSize: '13px', marginBottom: '20px', fontFamily: 'monospace' }}>
                Mã đơn: <strong>{selectedBooking.bookingCode}</strong>
              </p>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '14px', fontSize: '14px' }}>
                <div style={{ padding: '12px', borderRadius: 'var(--radius-sm)', backgroundColor: 'var(--bg-card)', border: '1px solid var(--border-color)' }}>
                  <h4 style={{ margin: '0 0 6px 0', fontSize: '15px', color: 'var(--text-main)', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <Film size={16} /> {selectedBooking.movieTitle}
                  </h4>
                  <p style={{ margin: '0 0 4px 0', fontSize: '13px', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <MapPin size={14} /> {selectedBooking.cinemaName} ({selectedBooking.roomName})
                  </p>
                  <p style={{ margin: 0, fontSize: '13px', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <Calendar size={14} /> Suất chiếu: <strong>{selectedBooking.startTime ? new Date(selectedBooking.startTime).toLocaleString('vi-VN') : ''}</strong>
                  </p>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                  <div>
                    <span style={{ fontSize: '12px', color: 'var(--text-secondary)', display: 'block' }}>Danh sách Ghế</span>
                    <div style={{ marginTop: '4px' }}>
                      {selectedBooking.seatCodes?.map((code: string) => (
                        <span key={code} style={{ display: 'inline-block', padding: '3px 8px', margin: '2px', borderRadius: '4px', backgroundColor: 'var(--primary)', color: '#fff', fontSize: '12px', fontWeight: 700 }}>
                          {code}
                        </span>
                      ))}
                    </div>
                  </div>
                  <div>
                    <span style={{ fontSize: '12px', color: 'var(--text-secondary)', display: 'block' }}>Tổng thanh toán</span>
                    <strong style={{ fontSize: '18px', color: '#22c55e', display: 'block', marginTop: '2px' }}>
                      {formatVND(selectedBooking.totalPrice)}
                    </strong>
                  </div>
                </div>

                <div>
                  <span style={{ fontSize: '12px', color: 'var(--text-secondary)', display: 'block', marginBottom: '4px' }}>Mã Vé Điện Tử (Ticket Codes)</span>
                  {selectedBooking.ticketCodes && selectedBooking.ticketCodes.length > 0 ? (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                      {selectedBooking.ticketCodes.map((tCode: string) => (
                        <span key={tCode} style={{ padding: '6px 12px', borderRadius: 'var(--radius-sm)', backgroundColor: 'rgba(59, 130, 246, 0.12)', border: '1px dashed #3b82f6', fontFamily: 'monospace', fontSize: '13px', fontWeight: 700, color: '#3b82f6' }}>
                          🎟️ {tCode}
                        </span>
                      ))}
                    </div>
                  ) : (
                    <p style={{ fontSize: '13px', color: 'var(--text-secondary)', fontStyle: 'italic', margin: 0 }}>Chưa xuất vé (chờ thanh toán).</p>
                  )}
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '24px' }}>
                <button
                  onClick={() => setIsDetailModalOpen(false)}
                  className="btn-primary"
                  style={{ padding: '8px 20px', borderRadius: 'var(--radius-sm)' }}
                >
                  Đóng
                </button>
              </div>
            </div>
          </div>
        )}

      </div>
    </AdminLayout>
  );
};

export default ManageBookings;
