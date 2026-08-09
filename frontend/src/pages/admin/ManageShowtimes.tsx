import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';
import AdminLayout from '../../components/AdminLayout';
import ConfirmModal from '../../components/ConfirmModal';
import { Plus, Edit2, Trash2, Calendar, AlertCircle, X, Check, MapPin, Film, Clock } from 'lucide-react';

const ManageShowtimes: React.FC = () => {
  const [showtimes, setShowtimes] = useState<any[]>([]);
  const [movies, setMovies] = useState<any[]>([]);
  const [cinemas, setCinemas] = useState<any[]>([]);
  const [rooms, setRooms] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');

  // Filters State
  const [filterMovieId, setFilterMovieId] = useState<string>('');
  const [filterCinemaId, setFilterCinemaId] = useState<string>('');
  const [filterDate, setFilterDate] = useState<string>('');

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [isEditMode, setIsEditMode] = useState<boolean>(false);
  const [currentShowtimeId, setCurrentShowtimeId] = useState<number | null>(null);

  // Form State
  const [movieId, setMovieId] = useState<string>('');
  const [cinemaId, setCinemaId] = useState<string>('');
  const [roomId, setRoomId] = useState<string>('');
  const [startTime, setStartTime] = useState<string>('');
  const [basePrice, setBasePrice] = useState<number>(90000);
  const [status, setStatus] = useState<string>('SCHEDULED');

  const fetchMoviesAndCinemas = async () => {
    try {
      const moviesRes = await api.get('/api/v1/movies?size=100');
      if (moviesRes.data?.success) {
        setMovies(moviesRes.data.data.content || []);
      }
    } catch (err) {
      console.error('Error fetching movies:', err);
    }

    try {
      const cinemasRes = await api.get('/api/v1/cinemas?size=100');
      if (cinemasRes.data?.success) {
        setCinemas(cinemasRes.data.data.content || []);
      }
    } catch (err) {
      console.error('Error fetching cinemas:', err);
    }
  };

  const fetchShowtimes = async () => {
    setLoading(true);
    setError('');
    try {
      let queryParams = '';
      const paramsList: string[] = [];
      if (filterMovieId) paramsList.push(`movieId=${filterMovieId}`);
      if (filterCinemaId) paramsList.push(`cinemaId=${filterCinemaId}`);
      if (filterDate) paramsList.push(`date=${filterDate}`);
      
      if (paramsList.length > 0) {
        queryParams = '?' + paramsList.join('&');
      }

      // API Trả về mảng suât chiếu trực tiếp
      const response = await api.get(`/api/v1/showtimes${queryParams}`);
      if (response.data?.success) {
        setShowtimes(response.data.data || []);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Không thể tải danh sách lịch chiếu.');
    } finally {
      setLoading(false);
    }
  };

  const fetchRoomsForCinema = async (cId: string) => {
    if (!cId) {
      setRooms([]);
      return;
    }
    try {
      const response = await api.get(`/api/v1/rooms?cinemaId=${cId}&size=50`);
      if (response.data?.success) {
        setRooms(response.data.data.content || []);
      }
    } catch (err) {
      console.error('Error fetching rooms:', err);
    }
  };

  useEffect(() => {
    fetchMoviesAndCinemas();
    fetchShowtimes();
  }, []);

  // Fetch showtimes on filter change
  useEffect(() => {
    fetchShowtimes();
  }, [filterMovieId, filterCinemaId, filterDate]);

  // Fetch rooms when form cinema selection changes
  useEffect(() => {
    fetchRoomsForCinema(cinemaId);
  }, [cinemaId]);

  const [modalError, setModalError] = useState<string>('');

  const openAddModal = () => {
    setIsEditMode(false);
    setCurrentShowtimeId(null);
    setMovieId('');
    setCinemaId('');
    setRoomId('');
    setStartTime('');
    setBasePrice(90000);
    setStatus('SCHEDULED');
    setModalError('');
    setIsModalOpen(true);
  };

  const openEditModal = async (s: any) => {
    setIsEditMode(true);
    setCurrentShowtimeId(s.id);
    setMovieId(s.movieId ? s.movieId.toString() : '');
    setCinemaId(s.cinemaId ? s.cinemaId.toString() : '');
    setModalError('');
    
    // Đợi tải các phòng của rạp đó trước để render
    if (s.cinemaId) {
      await fetchRoomsForCinema(s.cinemaId.toString());
    }
    
    setRoomId(s.roomId ? s.roomId.toString() : '');
    
    // Định dạng lại ISO Start Time thành YYYY-MM-DDTHH:MM cho input datetime-local
    const timeFormatted = s.startTime ? s.startTime.substring(0, 16) : '';
    setStartTime(timeFormatted);
    setBasePrice(s.basePrice || 90000);
    setStatus(s.status || 'SCHEDULED');
    setIsModalOpen(true);
  };

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setModalError('');

    if (!movieId || !roomId || !startTime) {
      setModalError('Vui lòng nhập đầy đủ các trường bắt buộc.');
      return;
    }

    const formattedStartTime = startTime.length === 16 ? startTime + ':00' : startTime;

    const payload = {
      movieId: Number(movieId),
      roomId: Number(roomId),
      startTime: formattedStartTime,
      basePrice: Number(basePrice),
      status
    };

    try {
      if (isEditMode && currentShowtimeId) {
        const response = await api.put(`/api/v1/showtimes/${currentShowtimeId}`, payload);
        if (response.data?.success) {
          setSuccess('Cập nhật lịch chiếu thành công!');
          setIsModalOpen(false);
          fetchShowtimes();
        }
      } else {
        const response = await api.post('/api/v1/showtimes', payload);
        if (response.data?.success) {
          setSuccess('Thêm lịch chiếu mới thành công!');
          setIsModalOpen(false);
          fetchShowtimes();
        }
      }
    } catch (err: any) {
      const errMsg = err.response?.data?.message || err.message || 'Lưu lịch chiếu thất bại.';
      setModalError(errMsg);
      setError(errMsg);
    }
  };

  // Confirm Modal State
  const [confirmModal, setConfirmModal] = useState<{ isOpen: boolean; showtime: any | null }>({
    isOpen: false,
    showtime: null
  });

  const promptDeleteShowtime = (s: any) => {
    setConfirmModal({ isOpen: true, showtime: s });
  };

  const executeDeleteShowtime = async () => {
    if (!confirmModal.showtime) return;
    const s = confirmModal.showtime;
    setError('');
    setSuccess('');
    try {
      const response = await api.delete(`/api/v1/showtimes/${s.id}`);
      if (response.data?.success) {
        setSuccess('Xóa/Hủy lịch chiếu thành công!');
        fetchShowtimes();
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Hủy lịch chiếu thất bại.');
    } finally {
      setConfirmModal({ isOpen: false, showtime: null });
    }
  };

  const formatVND = (value: number) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
  };

  const formatDateTime = (isoStr: string) => {
    if (!isoStr) return '';
    const d = new Date(isoStr);
    return d.toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  };

  return (
    <AdminLayout>
      <div className="animate-fade-in" style={{ textAlign: 'left' }}>
        
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
          <div>
            <h1 style={{ fontSize: '26px', fontWeight: 800, margin: '0' }}>Quản lý Lịch chiếu</h1>
            <p style={{ color: 'var(--text-secondary)', fontSize: '14px', margin: '4px 0 0 0' }}>Hiển thị danh sách suất chiếu của phim.</p>
          </div>
          <button onClick={openAddModal} className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Plus size={16} /> Thêm lịch chiếu mới
          </button>
        </div>

        {/* Notifications */}
        {success && (
          <div style={{ backgroundColor: 'rgba(16,185,129,0.1)', border: '1px solid rgba(16,185,129,0.2)', color: 'var(--success)', padding: '12px 16px', borderRadius: 'var(--radius-sm)', fontSize: '14px', marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Check size={16} /> {success}
          </div>
        )}

        {error && (
          <div style={{ backgroundColor: 'rgba(239,68,68,0.1)', border: '1px solid rgba(239,68,68,0.2)', color: 'var(--danger)', padding: '12px 16px', borderRadius: 'var(--radius-sm)', fontSize: '14px', marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <AlertCircle size={16} /> {error}
          </div>
        )}

        {/* Search & Filter Controls */}
        <div className="glass-card" style={{ padding: '20px', marginBottom: '24px', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px' }}>
          
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label" style={{ fontSize: '12px' }}>Bộ lọc Phim</label>
            <select className="form-control" value={filterMovieId} onChange={e => setFilterMovieId(e.target.value)}>
              <option value="">Tất cả phim</option>
              {movies.map(m => (
                <option key={m.id} value={m.id}>{m.title}</option>
              ))}
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label" style={{ fontSize: '12px' }}>Bộ lọc Rạp</label>
            <select className="form-control" value={filterCinemaId} onChange={e => setFilterCinemaId(e.target.value)}>
              <option value="">Tất cả rạp</option>
              {cinemas.map(c => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label" style={{ fontSize: '12px' }}>Lọc theo Ngày chiếu</label>
            <input type="date" className="form-control" value={filterDate} onChange={e => setFilterDate(e.target.value)} />
          </div>

        </div>

        {/* Showtimes Table */}
        {loading ? (
          <div style={{ display: 'flex', justifyContent: 'center', padding: '60px 0' }}><div className="spinner"></div></div>
        ) : showtimes.length === 0 ? (
          <div className="glass-card" style={{ padding: '60px', textAlign: 'center', color: 'var(--text-secondary)' }}>
            Không tìm thấy lịch chiếu nào thỏa mãn điều kiện.
          </div>
        ) : (
          <div className="glass-card" style={{ padding: '10px', overflowX: 'auto', marginBottom: '40px' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid var(--border-color)', textAlign: 'left' }}>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Phim</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Rạp / Phòng</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Suất chiếu</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Giá vé cơ bản</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Trạng thái</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700, textAlign: 'center' }}>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {showtimes.map((s) => (
                  <tr key={s.id} style={{ borderBottom: '1px solid var(--border-color)' }} className="table-row-hover">
                    <td style={{ padding: '12px 16px', fontWeight: 600, fontSize: '14px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <Film size={16} style={{ color: 'var(--text-muted)' }} />
                      {s.movieTitle}
                    </td>
                    <td style={{ padding: '12px 16px', fontSize: '13px' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontWeight: 500 }}>
                        <MapPin size={13} style={{ color: 'var(--secondary)' }} />
                        {s.cinemaName}
                      </div>
                      <div style={{ paddingLeft: '19px', color: 'var(--text-muted)', fontSize: '11px', marginTop: '2px' }}>{s.roomName}</div>
                    </td>
                    <td style={{ padding: '12px 16px', fontSize: '13px' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontWeight: 600 }}>
                        <Clock size={13} style={{ color: 'var(--primary)' }} />
                        {formatDateTime(s.startTime)}
                      </div>
                    </td>
                    <td style={{ padding: '12px 16px', fontSize: '14px', fontWeight: 700, color: 'var(--primary)' }}>
                      {formatVND(s.basePrice)}
                    </td>
                    <td style={{ padding: '12px 16px' }}>
                      <span className={`badge ${
                        s.status === 'OPEN' ? 'badge-success' : 
                        s.status === 'SCHEDULED' ? 'badge-info' : 
                        s.status === 'CANCELLED' ? 'badge-danger' : 'badge-secondary'
                      }`}>
                        {s.status === 'OPEN' ? 'Đang mở bán' : 
                         s.status === 'SCHEDULED' ? 'Đã xếp lịch' : 
                         s.status === 'CANCELLED' ? 'Đã hủy' : 'Đã khóa'}
                      </span>
                    </td>
                    <td style={{ padding: '12px 16px', textAlign: 'center' }}>
                      <div style={{ display: 'flex', justifyContent: 'center', gap: '8px' }}>
                        <button disabled={s.status === 'CANCELLED'} onClick={() => openEditModal(s)} className="btn btn-secondary" style={{ padding: '6px', minWidth: 'auto' }} title="Sửa">
                          <Edit2 size={14} style={{ color: s.status === 'CANCELLED' ? 'var(--text-muted)' : 'var(--primary)' }} />
                        </button>
                        <button disabled={s.status === 'CANCELLED'} onClick={() => promptDeleteShowtime(s)} className="btn btn-secondary" style={{ padding: '6px', minWidth: 'auto' }} title="Xóa/Hủy">
                          <Trash2 size={14} style={{ color: s.status === 'CANCELLED' ? 'var(--text-muted)' : 'var(--danger)' }} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Delete Confirmation Modal */}
        <ConfirmModal
          isOpen={confirmModal.isOpen}
          title="Xác nhận xóa Suất chiếu"
          message={
            confirmModal.showtime
              ? `Bạn có chắc chắn muốn xóa/hủy suất chiếu phim "${confirmModal.showtime.movieTitle}" lúc ${confirmModal.showtime.startTime ? confirmModal.showtime.startTime.replace('T', ' ').substring(0, 16) : ''}?`
              : ''
          }
          confirmText="Hủy suất chiếu"
          cancelText="Bỏ qua"
          variant="danger"
          onConfirm={executeDeleteShowtime}
          onClose={() => setConfirmModal({ isOpen: false, showtime: null })}
        />

        {/* Showtime Modal */}
        {isModalOpen && (
          <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(15, 23, 42, 0.85)', backdropFilter: 'blur(8px)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000, padding: '20px' }}>
            <div className="animate-fade-in" style={{ backgroundColor: 'var(--bg-card)', color: 'var(--text-main)', width: '100%', maxWidth: '520px', padding: '30px', borderRadius: '16px', border: '1px solid var(--border-color)', boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.7), 0 0 0 1px rgba(255, 255, 255, 0.1)', position: 'relative', textAlign: 'left' }}>
              <button 
                onClick={() => setIsModalOpen(false)} 
                style={{ position: 'absolute', right: '20px', top: '20px', background: 'transparent', border: 'none', cursor: 'pointer', color: 'var(--text-secondary)' }}
              >
                <X size={20} />
              </button>

              <h2 style={{ fontSize: '20px', fontWeight: 800, marginBottom: '20px', borderBottom: '1px solid var(--border-color)', paddingBottom: '10px', color: 'var(--primary)' }}>
                {isEditMode ? 'Cập nhật Lịch chiếu' : 'Thêm Lịch chiếu mới'}
              </h2>

              {modalError && (
                <div style={{ padding: '12px 16px', borderRadius: 'var(--radius-sm)', backgroundColor: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.3)', color: '#f87171', display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '20px', fontSize: '13px' }}>
                  <AlertCircle size={16} /> {modalError}
                </div>
              )}

              <form onSubmit={handleFormSubmit}>
                
                {/* Movie Selector */}
                <div className="form-group">
                  <label className="form-label">Chọn phim *</label>
                  <select className="form-control" value={movieId} onChange={e => setMovieId(e.target.value)} required>
                    <option value="">-- Chọn phim --</option>
                    {movies.map(m => (
                      <option key={m.id} value={m.id} disabled={m.status === 'INACTIVE'}>
                        {m.title} {m.status === 'INACTIVE' ? '(Đã ngừng chiếu)' : ''}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Cinema Selector */}
                <div className="form-group">
                  <label className="form-label">Chọn rạp *</label>
                  <select className="form-control" value={cinemaId} onChange={e => setCinemaId(e.target.value)} required>
                    <option value="">-- Chọn rạp chiếu --</option>
                    {cinemas.map(c => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>

                {/* Room Selector */}
                <div className="form-group">
                  <label className="form-label">Chọn phòng chiếu *</label>
                  <select className="form-control" value={roomId} onChange={e => setRoomId(e.target.value)} required disabled={!cinemaId}>
                    <option value="">-- Chọn phòng --</option>
                    {rooms.map(r => (
                      <option key={r.id} value={r.id} disabled={r.status !== 'ACTIVE'}>
                        {r.name} ({r.roomType || '2D'}) {r.status !== 'ACTIVE' ? '(Đang bảo trì)' : ''}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Start Time Input */}
                <div className="form-group">
                  <label className="form-label">Thời gian bắt đầu *</label>
                  <input 
                    type="datetime-local" 
                    className="form-control" 
                    value={startTime} 
                    onChange={e => setStartTime(e.target.value)} 
                    min={new Date().toISOString().slice(0, 16)}
                    required 
                  />
                </div>

                {/* Base Ticket Price */}
                <div className="form-group">
                  <label className="form-label">Giá vé cơ bản (VND) *</label>
                  <input type="number" className="form-control" value={basePrice} onChange={e => setBasePrice(Number(e.target.value))} required min={0} step={5000} />
                </div>

                {/* Status Selection */}
                <div className="form-group">
                  <label className="form-label">Trạng thái suất chiếu *</label>
                  <select className="form-control" value={status} onChange={e => setStatus(e.target.value)} required>
                    <option value="SCHEDULED">SCHEDULED (Mới lên lịch)</option>
                    <option value="OPEN">OPEN (Mở bán vé)</option>
                    <option value="CLOSED">CLOSED (Khóa suất chiếu)</option>
                    <option value="CANCELLED">CANCELLED (Hủy suất chiếu)</option>
                  </select>
                </div>

                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '24px', borderTop: '1px solid var(--border-color)', paddingTop: '20px' }}>
                  <button type="button" onClick={() => setIsModalOpen(false)} className="btn btn-secondary">Hủy</button>
                  <button type="submit" className="btn btn-primary">Lưu</button>
                </div>

              </form>
            </div>
          </div>
        )}

      </div>
    </AdminLayout>
  );
};

export default ManageShowtimes;
