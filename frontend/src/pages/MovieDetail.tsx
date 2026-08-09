import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { Calendar, Film, MapPin, Clock, Languages, ShieldAlert, ArrowLeft, Ticket, Play, X } from 'lucide-react';

const MovieDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();

  const [movie, setMovie] = useState<any>(null);
  const [showtimes, setShowtimes] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');
  const [isTrailerOpen, setIsTrailerOpen] = useState<boolean>(false);

  // Dates state: Next 7 days for filtering showtimes
  const [dates, setDates] = useState<Date[]>([]);
  const [selectedDateStr, setSelectedDateStr] = useState<string>(''); // format YYYY-MM-DD

  // Helper to convert Youtube Watch URL to Embed URL
  const getEmbedYoutubeUrl = (url: string) => {
    if (!url) return '';
    if (url.includes('youtube.com/embed/')) return url;
    
    const watchMatch = url.match(/[?&]v=([^&]+)/);
    if (watchMatch && watchMatch[1]) {
      return `https://www.youtube.com/embed/${watchMatch[1]}?autoplay=1`;
    }
    
    const shortMatch = url.match(/youtu\.be\/([^?&]+)/);
    if (shortMatch && shortMatch[1]) {
      return `https://www.youtube.com/embed/${shortMatch[1]}?autoplay=1`;
    }
    
    return url;
  };

  const getLocalDateStr = (d: Date) => {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  // Initialize 7 days starting from today
  useEffect(() => {
    const list: Date[] = [];
    const today = new Date();
    for (let i = 0; i < 7; i++) {
      const d = new Date(today);
      d.setDate(today.getDate() + i);
      list.push(d);
    }
    setDates(list);
    if (list.length > 0) {
      setSelectedDateStr(getLocalDateStr(list[0]));
    }
  }, []);

  const fetchData = async () => {
    if (!id) return;
    setLoading(true);
    setError('');
    try {
      // 1. Fetch movie details
      const movieRes = await api.get(`/api/v1/movies/${id}`);
      if (movieRes.data?.success) {
        setMovie(movieRes.data.data);
      }

      // 2. Fetch showtimes for this movie
      const showtimesRes = await api.get(`/api/v1/showtimes?movieId=${id}`);
      if (showtimesRes.data?.success) {
        setShowtimes(showtimesRes.data.data || []);
      }
    } catch (err: any) {
      console.error('Error fetching movie details:', err);
      setError(err.response?.data?.message || err.message || 'Không thể tải thông tin phim.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [id]);

  const formatDayName = (date: Date) => {
    const today = new Date();
    if (date.toDateString() === today.toDateString()) {
      return 'Hôm nay';
    }
    const tomorrow = new Date();
    tomorrow.setDate(today.getDate() + 1);
    if (date.toDateString() === tomorrow.toDateString()) {
      return 'Ngày mai';
    }

    const options: Intl.DateTimeFormatOptions = { weekday: 'long' };
    return date.toLocaleDateString('vi-VN', options);
  };

  const formatDateLabel = (date: Date) => {
    return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
  };

  // Filter showtimes by the selected date
  const filteredShowtimes = showtimes.filter(s => {
    if (!s.startTime) return false;
    const sDate = s.startTime.split('T')[0];
    return sDate === selectedDateStr;
  });

  // Group showtimes by Cinema Name
  const groupedByCinema: { [key: string]: any[] } = {};
  filteredShowtimes.forEach(s => {
    const cinemaName = s.cinemaName || 'Rạp chiếu phim';
    if (!groupedByCinema[cinemaName]) {
      groupedByCinema[cinemaName] = [];
    }
    groupedByCinema[cinemaName].push(s);
  });

  const handleShowtimeClick = (showtimeId: number) => {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: { pathname: `/booking/seats/${showtimeId}` } } });
    } else if (user?.role === 'ADMIN') {
      alert('Tài khoản quản trị viên (Admin) không nên đặt vé. Tuy nhiên, hệ thống sẽ mở sơ đồ ghế để bạn kiểm thử rạp.');
      navigate(`/booking/seats/${showtimeId}`);
    } else {
      navigate(`/booking/seats/${showtimeId}`);
    }
  };

  const formatVND = (value: number) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: '100px 0' }}>
        <div className="spinner"></div>
      </div>
    );
  }

  if (error || !movie) {
    return (
      <div className="animate-fade-in" style={{ maxWidth: '800px', margin: '40px auto', padding: '0 20px', textAlign: 'left' }}>
        <div className="glass-card" style={{ padding: '30px', textAlign: 'center' }}>
          <ShieldAlert size={50} style={{ color: 'var(--danger)', marginBottom: '16px' }} />
          <h3 style={{ fontSize: '20px', fontWeight: 700, color: 'var(--danger)', marginBottom: '8px' }}>Không tải được dữ liệu</h3>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '24px' }}>{error || 'Phim không tồn tại trong hệ thống.'}</p>
          <Link to="/" className="btn btn-secondary">
            <ArrowLeft size={16} /> Quay lại trang chủ
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="animate-fade-in" style={{ maxWidth: '1200px', margin: '0 auto', padding: '0 20px', textAlign: 'left' }}>
      
      {/* Back link */}
      <Link to="/" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', textDecoration: 'none', marginBottom: '24px', fontSize: '14px', fontWeight: 500 }}>
        <ArrowLeft size={16} /> Quay lại danh sách phim
      </Link>

      {/* Movie Details Info Card with Blurred Poster Background */}
      <div 
        style={{ 
          position: 'relative', 
          borderRadius: '24px', 
          overflow: 'hidden', 
          marginBottom: '40px',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(255, 255, 255, 0.1)',
          border: '1px solid var(--border-color)',
          backgroundColor: '#0f172a'
        }}
      >
        {/* Blurred Poster Background Layer */}
        {movie.posterUrl && (
          <div 
            style={{
              position: 'absolute',
              inset: '-10px',
              backgroundImage: `url(${movie.posterUrl})`,
              backgroundSize: 'cover',
              backgroundPosition: 'center 20%',
              filter: 'blur(10px) brightness(0.55) saturate(1.2)',
              transform: 'scale(1.05)',
              pointerEvents: 'none',
              zIndex: 0
            }}
          />
        )}
        
        {/* Dark Gradient Overlay */}
        <div style={{ position: 'absolute', inset: 0, background: 'linear-gradient(to right, rgba(15,23,42,0.88) 0%, rgba(15,23,42,0.65) 55%, rgba(15,23,42,0.35) 100%)', zIndex: 1 }} />

        {/* Content Box */}
        <div style={{ position: 'relative', zIndex: 2, padding: '36px', display: 'flex', gap: '32px', flexWrap: 'wrap' }}>
          {/* Left Side: Poster + Trailer Button */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', width: '280px', flexShrink: 0 }}>
            <div style={{ width: '100%', height: '400px', borderRadius: '16px', overflow: 'hidden', backgroundColor: 'var(--bg-main)', boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.6)' }}>
              {movie.posterUrl ? (
                <img src={movie.posterUrl} alt={movie.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%', color: 'var(--text-muted)' }}>
                  <Film size={50} style={{ opacity: 0.2 }} />
                  <span>Chưa có poster</span>
                </div>
              )}
            </div>

            {/* Trailer Button */}
            {movie.trailerUrl && (
              <button 
                onClick={() => setIsTrailerOpen(true)}
                className="btn btn-primary"
                style={{
                  width: '100%',
                  padding: '12px 20px',
                  fontWeight: 700,
                  fontSize: '14px',
                  borderRadius: '12px',
                  gap: '8px',
                  background: 'linear-gradient(135deg, #ef4444, #dc2626)',
                  boxShadow: '0 4px 15px rgba(239, 68, 68, 0.4)',
                  cursor: 'pointer'
                }}
              >
                <Play size={18} fill="#ffffff" /> Xem Trailer Phim
              </button>
            )}
          </div>

          {/* Right Side: Metadata */}
          <div style={{ flexGrow: 1, flexBasis: '400px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', color: '#ffffff' }}>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '12px' }}>
                <span className="badge badge-info" style={{ fontSize: '11px', padding: '5px 12px', fontWeight: 800, letterSpacing: '0.5px' }}>{movie.ageRating}</span>
                <span className="badge badge-success" style={{ fontSize: '11px', padding: '5px 12px', fontWeight: 800 }}>{movie.status === 'NOW_SHOWING' ? '🔥 ĐANG CHIẾU' : movie.status}</span>
              </div>
              
              <h1 style={{ fontSize: '34px', fontWeight: 900, margin: '0 0 18px 0', color: '#ffffff', textShadow: '0 2px 10px rgba(0,0,0,0.5)' }}>{movie.title}</h1>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '14px', fontSize: '14px', color: '#cbd5e1', marginBottom: '24px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Clock size={16} style={{ color: '#818cf8' }} /> <strong>Thời lượng:</strong> {movie.duration} phút
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Languages size={16} style={{ color: '#818cf8' }} /> <strong>Ngôn ngữ:</strong> {movie.language || 'Tiếng Việt'}
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Calendar size={16} style={{ color: '#818cf8' }} /> <strong>Khởi chiếu:</strong> {new Date(movie.releaseDate).toLocaleDateString('vi-VN')}
                </div>
                {movie.director && (
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <UserIcon size={16} style={{ color: '#818cf8' }} /> <strong>Đạo diễn:</strong> {movie.director}
                  </div>
                )}
              </div>

              <div style={{ marginBottom: '24px' }}>
                <h3 style={{ fontSize: '16px', fontWeight: 800, margin: '0 0 8px 0', color: '#f8fafc' }}>Tóm tắt nội dung</h3>
                <p style={{ fontSize: '14px', color: '#cbd5e1', lineHeight: '1.7', margin: 0 }}>
                  {movie.description || 'Nội dung phim đang được cập nhật.'}
                </p>
              </div>
            </div>

            <div style={{ borderTop: '1px solid rgba(255, 255, 255, 0.15)', paddingTop: '16px', fontSize: '13px', color: '#94a3b8' }}>
              <strong>Thể loại:</strong> {movie.genres && movie.genres.map((g: any) => g.name).join(', ')}
            </div>
          </div>
        </div>
      </div>

      {/* Trailer Modal */}
      {isTrailerOpen && movie.trailerUrl && (
        <div 
          style={{ 
            position: 'fixed', 
            inset: 0, 
            backgroundColor: 'rgba(15, 23, 42, 0.9)', 
            backdropFilter: 'blur(10px)', 
            display: 'flex', 
            justifyContent: 'center', 
            alignItems: 'center', 
            zIndex: 9999, 
            padding: '20px' 
          }}
          onClick={() => setIsTrailerOpen(false)}
        >
          <div 
            className="animate-fade-in"
            style={{ 
              backgroundColor: '#0f172a', 
              width: '100%', 
              maxWidth: '900px', 
              borderRadius: '20px', 
              overflow: 'hidden',
              boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.8), 0 0 0 1px rgba(255, 255, 255, 0.1)',
              position: 'relative'
            }}
            onClick={(e) => e.stopPropagation()}
          >
            {/* Modal Header */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '18px 24px', borderBottom: '1px solid rgba(255, 255, 255, 0.1)', backgroundColor: 'rgba(30, 41, 59, 0.6)' }}>
              <h3 style={{ fontSize: '18px', fontWeight: 800, margin: 0, color: '#f8fafc', display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Play size={18} style={{ color: '#ef4444' }} fill="#ef4444" /> Trailer: {movie.title}
              </h3>
              <button 
                onClick={() => setIsTrailerOpen(false)}
                style={{ background: 'none', border: 'none', color: '#94a3b8', cursor: 'pointer', padding: '4px' }}
              >
                <X size={22} />
              </button>
            </div>

            {/* Video Container (16:9 Aspect Ratio) */}
            <div style={{ position: 'relative', paddingTop: '56.25%', width: '100%', backgroundColor: '#000000' }}>
              <iframe 
                src={getEmbedYoutubeUrl(movie.trailerUrl)} 
                title={`Trailer ${movie.title}`}
                style={{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', border: 'none' }}
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                allowFullScreen
              />
            </div>
          </div>
        </div>
      )}

      {/* Showtime Section */}
      <h2 style={{ fontSize: '22px', fontWeight: 800, marginBottom: '20px' }}>Lịch chiếu & Đặt vé</h2>

      {/* Date Selector Tabs */}
      <div style={{ 
        display: 'flex', 
        gap: '10px', 
        overflowX: 'auto', 
        paddingBottom: '10px', 
        marginBottom: '30px',
        borderBottom: '1px solid var(--border-color)' 
      }}>
        {dates.map((d, index) => {
          const dateStr = getLocalDateStr(d);
          const isSelected = dateStr === selectedDateStr;
          return (
            <button
              key={index}
              onClick={() => setSelectedDateStr(dateStr)}
              style={{
                border: 'none',
                padding: '12px 20px',
                borderRadius: 'var(--radius-sm)',
                cursor: 'pointer',
                flexShrink: 0,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                backgroundColor: isSelected ? 'var(--primary)' : 'var(--bg-card)',
                color: isSelected ? '#fff' : 'var(--text-secondary)',
                borderWidth: '1px',
                borderStyle: 'solid',
                borderColor: isSelected ? 'var(--primary)' : 'var(--border-color)',
                boxShadow: isSelected ? 'var(--shadow-glow)' : 'var(--shadow-sm)',
                transition: 'var(--transition)'
              }}
            >
              <span style={{ fontSize: '11px', fontWeight: 500, opacity: isSelected ? 0.9 : 0.7 }}>{formatDayName(d)}</span>
              <span style={{ fontSize: '16px', fontWeight: 700, marginTop: '2px' }}>{formatDateLabel(d)}</span>
            </button>
          );
        })}
      </div>

      {/* Showtimes List grouped by Cinema */}
      {showtimes.length === 0 ? (
        <div className="glass-card" style={{ padding: '50px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          Phim hiện chưa có lịch chiếu nào được lên lịch trong hệ thống.
        </div>
      ) : Object.keys(groupedByCinema).length === 0 ? (
        <div className="glass-card" style={{ padding: '50px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          Không có suất chiếu nào vào ngày {new Date(selectedDateStr).toLocaleDateString('vi-VN')}. Hãy chọn ngày khác ở trên.
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', marginBottom: '60px' }}>
          {Object.entries(groupedByCinema).map(([cinemaName, list]) => (
            <div key={cinemaName} className="glass-card" style={{ padding: '24px', textAlign: 'left' }}>
              
              {/* Cinema Header */}
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', borderBottom: '1px solid var(--border-color)', paddingBottom: '12px', marginBottom: '16px' }}>
                <MapPin size={18} style={{ color: 'var(--secondary)' }} />
                <h3 style={{ fontSize: '16px', fontWeight: 700, margin: 0, color: 'var(--text-primary)' }}>{cinemaName}</h3>
              </div>

              {/* Showtimes Grid */}
              <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
                {list.map((s) => {
                  // Format time (HH:MM) from ISO String
                  const time = s.startTime ? s.startTime.split('T')[1].substring(0, 5) : '';
                  return (
                    <button
                      key={s.id}
                      onClick={() => handleShowtimeClick(s.id)}
                      className="glass-card"
                      style={{
                        border: '1px solid var(--border-color)',
                        padding: '12px 20px',
                        cursor: 'pointer',
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        gap: '4px',
                        borderRadius: 'var(--radius-sm)',
                        backgroundColor: 'var(--bg-main)',
                        transition: 'var(--transition)'
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.borderColor = 'var(--primary)';
                        e.currentTarget.style.boxShadow = 'var(--shadow-glow)';
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.borderColor = 'var(--border-color)';
                        e.currentTarget.style.boxShadow = 'none';
                      }}
                    >
                      <span style={{ fontSize: '18px', fontWeight: 800, color: 'var(--primary)', display: 'flex', alignItems: 'center', gap: '4px' }}>
                        <Clock size={14} /> {time}
                      </span>
                      <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                        {s.roomName} • {formatVND(s.basePrice)}
                      </span>
                    </button>
                  );
                })}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

// Help helper
const UserIcon = (props: any) => <svg {...props} xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-user"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>;

export default MovieDetail;
