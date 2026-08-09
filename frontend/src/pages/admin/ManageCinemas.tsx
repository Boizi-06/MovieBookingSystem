import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';
import AdminLayout from '../../components/AdminLayout';
import ConfirmModal from '../../components/ConfirmModal';
import { Plus, Edit2, Trash2, Building2, AlertCircle, X, Check, MapPin, Armchair } from 'lucide-react';

const ManageCinemas: React.FC = () => {
  // Cinema state
  const [cinemas, setCinemas] = useState<any[]>([]);
  const [selectedCinema, setSelectedCinema] = useState<any | null>(null);
  const [loadingCinemas, setLoadingCinemas] = useState<boolean>(true);
  const [searchKeyword, setSearchKeyword] = useState<string>('');
  const [cityFilter, setCityFilter] = useState<string>('');

  // Room state
  const [rooms, setRooms] = useState<any[]>([]);
  const [selectedRoom, setSelectedRoom] = useState<any | null>(null);
  const [loadingRooms, setLoadingRooms] = useState<boolean>(false);

  // Seat state
  const [seats, setSeats] = useState<any[]>([]);
  const [loadingSeats, setLoadingSeats] = useState<boolean>(false);

  // Status/Alert
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');

  // Cinema Modal state
  const [isCinemaModalOpen, setIsCinemaModalOpen] = useState<boolean>(false);
  const [isEditCinema, setIsEditCinema] = useState<boolean>(false);
  const [cinemaForm, setCinemaForm] = useState({ name: '', address: '', city: '', phone: '' });

  // Room Modal state
  const [isRoomModalOpen, setIsRoomModalOpen] = useState<boolean>(false);
  const [isEditRoom, setIsEditRoom] = useState<boolean>(false);
  const [roomForm, setRoomForm] = useState({ name: '', roomType: 'STANDARD' });

  // Fetch Cinemas
  const fetchCinemas = async () => {
    setLoadingCinemas(true);
    setError('');
    try {
      let url = '/api/v1/cinemas?size=100';
      if (searchKeyword) url += `&keyword=${encodeURIComponent(searchKeyword)}`;
      if (cityFilter) url += `&city=${encodeURIComponent(cityFilter)}`;

      const response = await api.get(url);
      if (response.data?.success) {
        const content = response.data.data.content || [];
        setCinemas(content);
        if (content.length > 0 && !selectedCinema) {
          setSelectedCinema(content[0]);
        }
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Không thể tải danh sách rạp.');
    } finally {
      setLoadingCinemas(false);
    }
  };

  // Fetch Rooms by Cinema ID
  const fetchRooms = async (cinemaId: number) => {
    setLoadingRooms(true);
    setRooms([]);
    setSelectedRoom(null);
    setSeats([]);
    try {
      const response = await api.get(`/api/v1/rooms?cinemaId=${cinemaId}&size=50`);
      if (response.data?.success) {
        const content = response.data.data.content || [];
        setRooms(content);
        if (content.length > 0) {
          setSelectedRoom(content[0]);
        }
      }
    } catch (err: any) {
      console.error('Error fetching rooms:', err);
    } finally {
      setLoadingRooms(false);
    }
  };

  // Fetch Seats by Room ID
  const fetchSeats = async (roomId: number) => {
    setLoadingSeats(true);
    try {
      const response = await api.get(`/api/v1/seats/room/${roomId}`);
      if (response.data?.success) {
        setSeats(response.data.data || []);
      }
    } catch (err: any) {
      console.error('Error fetching seats:', err);
    } finally {
      setLoadingSeats(false);
    }
  };

  useEffect(() => {
    fetchCinemas();
  }, [searchKeyword, cityFilter]);

  useEffect(() => {
    if (selectedCinema) {
      fetchRooms(selectedCinema.id);
    }
  }, [selectedCinema]);

  useEffect(() => {
    if (selectedRoom) {
      fetchSeats(selectedRoom.id);
    }
  }, [selectedRoom]);

  // Cinema Handlers
  const handleOpenCreateCinema = () => {
    setIsEditCinema(false);
    setCinemaForm({ name: '', address: '', city: '', phone: '' });
    setIsCinemaModalOpen(true);
  };

  const handleOpenEditCinema = (cinema: any) => {
    setIsEditCinema(true);
    setCinemaForm({
      name: cinema.name || '',
      address: cinema.address || '',
      city: cinema.city || '',
      phone: cinema.phone || ''
    });
    setIsCinemaModalOpen(true);
  };

  const handleSaveCinema = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      if (isEditCinema && selectedCinema) {
        await api.put(`/api/v1/cinemas/${selectedCinema.id}`, cinemaForm);
        setSuccess('Cập nhật rạp thành công!');
      } else {
        await api.post('/api/v1/cinemas', cinemaForm);
        setSuccess('Thêm rạp chiếu phim thành công!');
      }
      setIsCinemaModalOpen(false);
      fetchCinemas();
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Lưu thông tin rạp thất bại.');
    }
  };

  // Confirm Modal State
  const [confirmModal, setConfirmModal] = useState<{
    isOpen: boolean;
    type: 'DELETE_CINEMA' | 'DELETE_ROOM' | 'RESET_SEATS' | null;
    targetId: number | null;
    title: string;
    message: string;
    variant: 'danger' | 'warning';
  }>({
    isOpen: false,
    type: null,
    targetId: null,
    title: '',
    message: '',
    variant: 'danger',
  });

  const promptDeleteCinema = (id: number, name: string) => {
    setConfirmModal({
      isOpen: true,
      type: 'DELETE_CINEMA',
      targetId: id,
      title: 'Xác nhận xóa Rạp chiếu',
      message: `Bạn có chắc chắn muốn xóa rạp "${name}"? Tất cả phòng chiếu và dữ liệu ghế liên quan cũng sẽ bị ảnh hưởng.`,
      variant: 'danger',
    });
  };

  const executeDeleteCinema = async () => {
    if (!confirmModal.targetId) return;
    const id = confirmModal.targetId;
    try {
      await api.delete(`/api/v1/cinemas/${id}`);
      setSuccess('Xóa rạp chiếu thành công!');
      if (selectedCinema?.id === id) {
        setSelectedCinema(null);
      }
      fetchCinemas();
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Xóa rạp thất bại.');
    } finally {
      setConfirmModal({ isOpen: false, type: null, targetId: null, title: '', message: '', variant: 'danger' });
    }
  };

  const promptDeleteRoom = (id: number, name: string) => {
    setConfirmModal({
      isOpen: true,
      type: 'DELETE_ROOM',
      targetId: id,
      title: 'Xác nhận xóa Phòng chiếu',
      message: `Bạn có chắc chắn muốn xóa phòng chiếu "${name}"?`,
      variant: 'danger',
    });
  };

  const executeDeleteRoom = async () => {
    if (!confirmModal.targetId) return;
    const id = confirmModal.targetId;
    try {
      await api.delete(`/api/v1/rooms/${id}`);
      setSuccess('Xóa phòng chiếu thành công!');
      if (selectedCinema) fetchRooms(selectedCinema.id);
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Xóa phòng chiếu thất bại.');
    } finally {
      setConfirmModal({ isOpen: false, type: null, targetId: null, title: '', message: '', variant: 'danger' });
    }
  };
  // Room Handlers
  const handleOpenCreateRoom = () => {
    if (!selectedCinema) return;
    setIsEditRoom(false);
    setRoomForm({ name: '', roomType: 'STANDARD' });
    setIsRoomModalOpen(true);
  };

  const handleOpenEditRoom = (room: any) => {
    setIsEditRoom(true);
    setSelectedRoom(room);
    setRoomForm({ name: room.name || '', roomType: room.roomType || 'STANDARD' });
    setIsRoomModalOpen(true);
  };

  const handleSaveRoom = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedCinema) return;
    setError('');
    setSuccess('');
    try {
      if (isEditRoom && selectedRoom) {
        await api.put(`/api/v1/rooms/${selectedRoom.id}`, {
          cinemaId: selectedCinema.id,
          name: roomForm.name,
          roomType: roomForm.roomType
        });
        setSuccess('Cập nhật phòng chiếu thành công!');
      } else {
        await api.post('/api/v1/rooms', {
          cinemaId: selectedCinema.id,
          name: roomForm.name,
          roomType: roomForm.roomType
        });
        setSuccess('Tạo phòng chiếu thành công (đã sinh 60 ghế tự động)!');
      }
      setIsRoomModalOpen(false);
      fetchRooms(selectedCinema.id);
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Lưu phòng chiếu thất bại.');
    }
  };

  // Seat Status Toggle Handler
  const handleToggleSeatStatus = async (seat: any) => {
    const newStatus = seat.status === 'ACTIVE' ? 'MAINTENANCE' : 'ACTIVE';
    try {
      await api.put(`/api/v1/seats/${seat.id}/status`, { status: newStatus });
      setSeats(prev => prev.map(s => s.id === seat.id ? { ...s, status: newStatus } : s));
      setSuccess(`Đã đổi ghế ${seat.seatCode} sang ${newStatus === 'ACTIVE' ? 'Hoạt động' : 'Bảo trì'}`);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Không thể cập nhật trạng thái ghế.');
    }
  };

  const promptResetSeats = () => {
    if (!selectedRoom) return;
    setConfirmModal({
      isOpen: true,
      type: 'RESET_SEATS',
      targetId: selectedRoom.id,
      title: 'Đặt lại sơ đồ ghế',
      message: `Bạn có chắc chắn muốn đặt lại sơ đồ ghế cho phòng "${selectedRoom.name}"? (Khôi phục mặc định: 60 ghế từ hàng A đến F).`,
      variant: 'warning',
    });
  };

  const executeResetSeats = async () => {
    if (!selectedRoom) return;
    try {
      const response = await api.post(`/api/v1/rooms/${selectedRoom.id}/reset-seats`);
      if (response.data?.success) {
        setSuccess('Tái tạo sơ đồ ghế thành công!');
        fetchSeats(selectedRoom.id);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Tái tạo ghế thất bại.');
    } finally {
      setConfirmModal({ isOpen: false, type: null, targetId: null, title: '', message: '', variant: 'danger' });
    }
  };

  const handleConfirmAction = () => {
    if (confirmModal.type === 'DELETE_CINEMA') executeDeleteCinema();
    else if (confirmModal.type === 'DELETE_ROOM') executeDeleteRoom();
    else if (confirmModal.type === 'RESET_SEATS') executeResetSeats();
  };

  // Group seats by row (A, B, C, D, E, F, G, H)
  const rows = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];

  return (
    <AdminLayout>
      <div className="animate-fade-in" style={{ textAlign: 'left' }}>
        
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
          <div>
            <h1 style={{ fontSize: '26px', fontWeight: 800, margin: '0 0 4px 0' }}>Quản lý Rạp & Phòng chiếu</h1>
            <p style={{ color: 'var(--text-secondary)', fontSize: '14px', margin: 0 }}>Quản lý các cụm rạp, phòng chiếu và sơ đồ ghế ngồi.</p>
          </div>
          <button 
            onClick={handleOpenCreateCinema}
            className="btn-primary"
            style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '10px 18px', borderRadius: 'var(--radius-sm)' }}
          >
            <Plus size={18} /> Thêm Cụm Rạp Mới
          </button>
        </div>

        {/* Alerts */}
        {error && (
          <div style={{ padding: '12px 16px', borderRadius: 'var(--radius-sm)', backgroundColor: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.3)', color: '#f87171', display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '20px' }}>
            <AlertCircle size={18} /> {error}
          </div>
        )}
        {success && (
          <div style={{ padding: '12px 16px', borderRadius: 'var(--radius-sm)', backgroundColor: 'rgba(34, 197, 94, 0.15)', border: '1px solid rgba(34, 197, 94, 0.3)', color: '#4ade80', display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '20px' }}>
            <Check size={18} /> {success}
          </div>
        )}

        {/* Main 2-Column Layout */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '24px' }}>
          
          {/* Left Column: Cinemas & Rooms List */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            
            {/* Cinemas Card */}
            <div className="glass-card" style={{ padding: '20px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <h2 style={{ fontSize: '18px', fontWeight: 700, margin: 0, display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--primary)' }}>
                  <Building2 size={20} /> Cụm Rạp ({cinemas.length})
                </h2>
              </div>

              {/* Filters */}
              <div style={{ display: 'flex', gap: '8px', marginBottom: '16px' }}>
                <input 
                  type="text"
                  placeholder="Tìm tên rạp..."
                  value={searchKeyword}
                  onChange={(e) => setSearchKeyword(e.target.value)}
                  style={{ flex: 1, padding: '8px 12px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-card)', color: 'var(--text-main)', fontSize: '13px' }}
                />
              </div>

              {/* Cinema List */}
              {loadingCinemas ? (
                <p style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>Đang tải danh sách rạp...</p>
              ) : cinemas.length === 0 ? (
                <p style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>Chưa có rạp chiếu nào.</p>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', maxHeight: '350px', overflowY: 'auto' }}>
                  {cinemas.map(cinema => (
                    <div 
                      key={cinema.id}
                      onClick={() => setSelectedCinema(cinema)}
                      style={{
                        padding: '12px 14px',
                        borderRadius: 'var(--radius-sm)',
                        border: selectedCinema?.id === cinema.id ? '2px solid var(--primary)' : '1px solid var(--border-color)',
                        backgroundColor: selectedCinema?.id === cinema.id ? 'rgba(239, 68, 68, 0.08)' : 'var(--bg-card)',
                        cursor: 'pointer',
                        transition: 'all 0.2s ease',
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center'
                      }}
                    >
                      <div>
                        <h4 style={{ margin: '0 0 4px 0', fontSize: '14px', fontWeight: 700 }}>{cinema.name}</h4>
                        <p style={{ margin: 0, fontSize: '12px', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '4px' }}>
                          <MapPin size={12} /> {cinema.address}, {cinema.city}
                        </p>
                      </div>
                      <div style={{ display: 'flex', gap: '6px' }} onClick={(e) => e.stopPropagation()}>
                        <button onClick={() => handleOpenEditCinema(cinema)} style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', padding: '4px' }}>
                          <Edit2 size={14} />
                        </button>
                        <button onClick={() => promptDeleteCinema(cinema.id, cinema.name)} style={{ background: 'none', border: 'none', color: '#f87171', cursor: 'pointer', padding: '4px' }}>
                          <Trash2 size={14} />
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Rooms Card for Selected Cinema */}
            {selectedCinema && (
              <div className="glass-card" style={{ padding: '20px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                  <h3 style={{ fontSize: '16px', fontWeight: 700, margin: 0 }}>
                    Phòng chiếu thuộc {selectedCinema.name}
                  </h3>
                  <button 
                    onClick={handleOpenCreateRoom}
                    style={{ background: 'var(--primary)', color: '#fff', border: 'none', padding: '6px 12px', borderRadius: 'var(--radius-sm)', fontSize: '12px', fontWeight: 600, cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '4px' }}
                  >
                    <Plus size={14} /> Tạo Phòng
                  </button>
                </div>

                {loadingRooms ? (
                  <p style={{ color: 'var(--text-secondary)', fontSize: '13px' }}>Đang tải phòng chiếu...</p>
                ) : rooms.length === 0 ? (
                  <p style={{ color: 'var(--text-secondary)', fontSize: '13px' }}>Rạp này chưa có phòng chiếu nào.</p>
                ) : (
                  <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                    {rooms.map(room => (
                      <div 
                        key={room.id}
                        onClick={() => setSelectedRoom(room)}
                        style={{
                          padding: '8px 14px',
                          borderRadius: 'var(--radius-sm)',
                          border: selectedRoom?.id === room.id ? '2px solid #3b82f6' : '1px solid var(--border-color)',
                          backgroundColor: selectedRoom?.id === room.id ? 'rgba(59, 130, 246, 0.15)' : 'var(--bg-card)',
                          cursor: 'pointer',
                          display: 'flex',
                          alignItems: 'center',
                          gap: '8px',
                          fontSize: '13px',
                          fontWeight: 600
                        }}
                      >
                        <span>{room.name} ({room.roomType})</span>
                        <button onClick={(e) => { e.stopPropagation(); handleOpenEditRoom(room); }} style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', padding: 0 }} title="Sửa phòng">
                          <Edit2 size={12} />
                        </button>
                        <button onClick={(e) => { e.stopPropagation(); promptDeleteRoom(room.id, room.name); }} style={{ background: 'none', border: 'none', color: '#f87171', cursor: 'pointer', padding: 0 }} title="Xóa phòng">
                          <Trash2 size={12} />
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Right Column: Visual Seat Map Inspector */}
          <div className="glass-card" style={{ padding: '24px', display: 'flex', flexDirection: 'column' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
              <h2 style={{ fontSize: '18px', fontWeight: 700, margin: 0, display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--primary)' }}>
                <Armchair size={20} /> Sơ đồ Ghế Phòng chiếu
              </h2>
              {selectedRoom && (
                <button
                  onClick={promptResetSeats}
                  style={{ padding: '6px 12px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-card)', color: 'var(--text-main)', fontSize: '12px', fontWeight: 600, cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '4px' }}
                  title="Xóa ghế cũ và sinh lại ghế chuẩn: A-C Thường, D-E VIP, F Sweetbox"
                >
                  🔄 Tái tạo sơ đồ ghế
                </button>
              )}
            </div>

            {selectedRoom ? (
              <div>
                <p style={{ color: 'var(--text-secondary)', fontSize: '13px', marginBottom: '20px' }}>
                  Phòng: <strong style={{ color: 'var(--text-main)' }}>{selectedRoom.name} ({selectedRoom.roomType})</strong> - Tổng số ghế: <strong>{seats.length}</strong> (Click vào ghế để chuyển đổi trạng thái <span style={{ color: '#4ade80' }}>Hoạt động</span> / <span style={{ color: '#f87171' }}>Bảo trì</span>).
                </p>

                {/* Screen Visual Indicator */}
                <div style={{ textAlign: 'center', marginBottom: '28px' }}>
                  <div style={{ width: '80%', height: '8px', background: 'linear-gradient(90deg, transparent, var(--primary), transparent)', margin: '0 auto 8px auto', borderRadius: '4px' }} />
                  <span style={{ fontSize: '11px', color: 'var(--text-secondary)', letterSpacing: '2px', textTransform: 'uppercase', fontWeight: 700 }}>Màn Hình Chiếu</span>
                </div>

                {/* Legend */}
                <div style={{ display: 'flex', justifyContent: 'center', gap: '20px', marginBottom: '24px', fontSize: '12px' }}>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <span style={{ width: '14px', height: '14px', borderRadius: '4px', backgroundColor: '#3b82f6' }} /> Thường
                  </span>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <span style={{ width: '14px', height: '14px', borderRadius: '4px', backgroundColor: '#8b5cf6' }} /> VIP
                  </span>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <span style={{ width: '14px', height: '14px', borderRadius: '4px', backgroundColor: '#ec4899' }} /> Sweetbox (Đôi)
                  </span>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <span style={{ width: '14px', height: '14px', borderRadius: '4px', backgroundColor: '#ef4444' }} /> Bảo trì
                  </span>
                </div>

                {/* Grid */}
                {loadingSeats ? (
                  <p style={{ textAlign: 'center', color: 'var(--text-secondary)' }}>Đang tải sơ đồ ghế...</p>
                ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', alignItems: 'center' }}>
                    {rows.map(row => {
                      const rowSeats = seats.filter(s => s.seatRow === row);
                      if (rowSeats.length === 0) return null;
                      return (
                        <div key={row} style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                          <span style={{ width: '20px', fontWeight: 700, fontSize: '13px', color: 'var(--text-secondary)' }}>{row}</span>
                          <div style={{ display: 'flex', gap: '8px' }}>
                            {rowSeats.map(seat => {
                              const isCouple = seat.seatType === 'SWEETBOX' || seat.seatType === 'COUPLE';
                              let bg = '#3b82f6';
                              if (seat.seatType === 'VIP') bg = '#8b5cf6';
                              if (isCouple) bg = '#ec4899';
                              if (seat.status !== 'ACTIVE') bg = '#ef4444';

                              return (
                                <button
                                  key={seat.id}
                                  onClick={() => handleToggleSeatStatus(seat)}
                                  title={`Ghế ${seat.seatCode} (${seat.seatType}) - Click để ${seat.status === 'ACTIVE' ? 'chuyển Bảo trì' : 'Kích hoạt'}`}
                                  style={{
                                    width: isCouple ? '76px' : '36px',
                                    height: '36px',
                                    borderRadius: '6px',
                                    border: 'none',
                                    backgroundColor: bg,
                                    color: '#fff',
                                    fontSize: '11px',
                                    fontWeight: 700,
                                    cursor: 'pointer',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    opacity: seat.status !== 'ACTIVE' ? 0.6 : 1,
                                    transition: 'transform 0.15s ease'
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
                )}
              </div>
            ) : (
              <div style={{ textAlign: 'center', padding: '60px 0', color: 'var(--text-secondary)' }}>
                <Armchair size={48} style={{ opacity: 0.3, marginBottom: '12px' }} />
                <p>Vui lòng chọn một rạp và phòng chiếu bên trái để xem sơ đồ ghế.</p>
              </div>
            )}
          </div>

        </div>

        {/* Modal Cinema */}
        {isCinemaModalOpen && (
          <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(15, 23, 42, 0.85)', backdropFilter: 'blur(8px)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000, padding: '20px' }}>
            <div className="animate-fade-in" style={{ backgroundColor: 'var(--bg-card)', color: 'var(--text-main)', width: '100%', maxWidth: '480px', padding: '28px', borderRadius: '16px', border: '1px solid var(--border-color)', boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.7), 0 0 0 1px rgba(255, 255, 255, 0.1)', position: 'relative' }}>
              <button onClick={() => setIsCinemaModalOpen(false)} style={{ position: 'absolute', top: '16px', right: '16px', background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}>
                <X size={20} />
              </button>

              <h2 style={{ fontSize: '20px', fontWeight: 800, marginBottom: '20px', color: 'var(--primary)' }}>
                {isEditCinema ? 'Cập nhật Cụm Rạp' : 'Thêm Cụm Rạp Mới'}
              </h2>

              <form onSubmit={handleSaveCinema} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '13px', fontWeight: 700, marginBottom: '6px', color: 'var(--text-main)' }}>Tên Rạp chiếu *</label>
                  <input 
                    type="text" required placeholder="VD: CGV Bà Triệu"
                    value={cinemaForm.name} onChange={e => setCinemaForm({...cinemaForm, name: e.target.value})}
                    style={{ width: '100%', padding: '10px 14px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-secondary)', color: 'var(--text-main)' }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '13px', fontWeight: 700, marginBottom: '6px', color: 'var(--text-main)' }}>Địa chỉ *</label>
                  <input 
                    type="text" required placeholder="VD: Tầng 6 Vincom Center, 191 Bà Triệu"
                    value={cinemaForm.address} onChange={e => setCinemaForm({...cinemaForm, address: e.target.value})}
                    style={{ width: '100%', padding: '10px 14px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-secondary)', color: 'var(--text-main)' }}
                  />
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                  <div>
                    <label style={{ display: 'block', fontSize: '13px', fontWeight: 700, marginBottom: '6px', color: 'var(--text-main)' }}>Tỉnh/Thành phố *</label>
                    <input 
                      type="text" required placeholder="VD: Hà Nội"
                      value={cinemaForm.city} onChange={e => setCinemaForm({...cinemaForm, city: e.target.value})}
                      style={{ width: '100%', padding: '10px 14px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-secondary)', color: 'var(--text-main)' }}
                    />
                  </div>
                  <div>
                    <label style={{ display: 'block', fontSize: '13px', fontWeight: 700, marginBottom: '6px', color: 'var(--text-main)' }}>Số điện thoại</label>
                    <input 
                      type="text" placeholder="VD: 1900 6017"
                      value={cinemaForm.phone} onChange={e => setCinemaForm({...cinemaForm, phone: e.target.value})}
                      style={{ width: '100%', padding: '10px 14px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-secondary)', color: 'var(--text-main)' }}
                    />
                  </div>
                </div>

                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '12px' }}>
                  <button type="button" onClick={() => setIsCinemaModalOpen(false)} style={{ padding: '10px 18px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', background: 'transparent', color: 'var(--text-main)', cursor: 'pointer' }}>
                    Hủy
                  </button>
                  <button type="submit" className="btn-primary" style={{ padding: '10px 24px', borderRadius: 'var(--radius-sm)' }}>
                    Lưu thông tin
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Modal Room */}
        {isRoomModalOpen && (
          <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(15, 23, 42, 0.85)', backdropFilter: 'blur(8px)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000, padding: '20px' }}>
            <div className="animate-fade-in" style={{ backgroundColor: 'var(--bg-card)', color: 'var(--text-main)', width: '100%', maxWidth: '420px', padding: '28px', borderRadius: '16px', border: '1px solid var(--border-color)', boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.7), 0 0 0 1px rgba(255, 255, 255, 0.1)', position: 'relative' }}>
              <button onClick={() => setIsRoomModalOpen(false)} style={{ position: 'absolute', top: '16px', right: '16px', background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}>
                <X size={20} />
              </button>

              <h2 style={{ fontSize: '20px', fontWeight: 800, marginBottom: '20px', color: 'var(--primary)' }}>
                {isEditRoom ? 'Sửa Phòng Chiếu' : 'Tạo Phòng Chiếu Mới'}
              </h2>

              <form onSubmit={handleSaveRoom} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, marginBottom: '6px' }}>Tên phòng chiếu *</label>
                  <input 
                    type="text" required placeholder="VD: Phòng 01 - IMAX"
                    value={roomForm.name} onChange={e => setRoomForm({...roomForm, name: e.target.value})}
                    style={{ width: '100%', padding: '10px 14px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-card)', color: 'var(--text-main)' }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, marginBottom: '6px' }}>Loại phòng *</label>
                  <select 
                    value={roomForm.roomType} onChange={e => setRoomForm({...roomForm, roomType: e.target.value})}
                    style={{ width: '100%', padding: '10px 14px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', backgroundColor: 'var(--bg-card)', color: 'var(--text-main)' }}
                  >
                    <option value="STANDARD">STANDARD (Tiêu chuẩn)</option>
                    <option value="VIP">VIP</option>
                    <option value="2D">2D</option>
                    <option value="3D">3D</option>
                    <option value="IMAX">IMAX</option>
                    <option value="4DX">4DX</option>
                  </select>
                </div>

                {!isEditRoom && (
                  <p style={{ fontSize: '12px', color: 'var(--text-secondary)', margin: 0, fontStyle: 'italic' }}>
                    * Hệ thống sẽ tự động sinh 60 ghế (Ma trận 6 hàng A-F x 10 cột) với các loại ghế Thường, VIP và Sweetbox.
                  </p>
                )}

                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '12px' }}>
                  <button type="button" onClick={() => setIsRoomModalOpen(false)} style={{ padding: '8px 16px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)', background: 'transparent', color: 'var(--text-main)', cursor: 'pointer' }}>
                    Hủy
                  </button>
                  <button type="submit" className="btn-primary" style={{ padding: '8px 20px', borderRadius: 'var(--radius-sm)' }}>
                    Lưu Phòng
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Global Confirmation Modal */}
        <ConfirmModal
          isOpen={confirmModal.isOpen}
          title={confirmModal.title}
          message={confirmModal.message}
          confirmText="Đồng ý"
          cancelText="Hủy bỏ"
          variant={confirmModal.variant}
          onConfirm={handleConfirmAction}
          onClose={() => setConfirmModal({ isOpen: false, type: null, targetId: null, title: '', message: '', variant: 'danger' })}
        />

      </div>
    </AdminLayout>
  );
};

export default ManageCinemas;
