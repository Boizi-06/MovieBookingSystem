import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import { AlertCircle, ChevronRight, ChevronLeft, Film, Search, SlidersHorizontal, Ticket, Play, X, Trophy } from 'lucide-react';

const Home: React.FC = () => {
  const navigate = useNavigate();
  const [movies, setMovies] = useState<any[]>([]);
  const [genres, setGenres] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  // Banner Carousel State
  const [bannerMovies, setBannerMovies] = useState<any[]>([]);
  const [currentSlideIndex, setCurrentSlideIndex] = useState<number>(0);

  // Top 3 Revenue Movies Modal State
  const [topRevenueMovies, setTopRevenueMovies] = useState<any[]>([]);
  const [showTopModal, setShowTopModal] = useState<boolean>(false);
  const [currentTopIndex, setCurrentTopIndex] = useState<number>(0);

  // Filters State
  const [keyword, setKeyword] = useState<string>('');
  const [selectedGenre, setSelectedGenre] = useState<string>('');
  const [selectedStatus, setSelectedStatus] = useState<string>('NOW_SHOWING'); // Default to NOW_SHOWING

  // Fetch Top Revenue Movies on mount
  useEffect(() => {
    const fetchTopRevenueMovies = async () => {
      try {
        const response = await api.get('/api/v1/movies/top-revenue?limit=3');
        if (response.data?.success && response.data.data.length > 0) {
          setTopRevenueMovies(response.data.data);
          setShowTopModal(true);
        }
      } catch (err) {
        console.error('Error fetching top revenue movies:', err);
      }
    };
    fetchTopRevenueMovies();
  }, []);

  const handlePrevTop = () => {
    if (topRevenueMovies.length === 0) return;
    setCurrentTopIndex(prev => (prev - 1 + topRevenueMovies.length) % topRevenueMovies.length);
  };

  const handleNextTop = () => {
    if (topRevenueMovies.length === 0) return;
    setCurrentTopIndex(prev => (prev + 1) % topRevenueMovies.length);
  };

  // Fetch Banner Movies (NOW_SHOWING) on mount
  useEffect(() => {
    const fetchBannerMovies = async () => {
      try {
        const response = await api.get('/api/v1/movies?status=NOW_SHOWING&size=8');
        if (response.data?.success) {
          const list = response.data.data.content || [];
          setBannerMovies(list);
        }
      } catch (err) {
        console.error('Error fetching banner movies:', err);
      }
    };
    fetchBannerMovies();
  }, []);

  // Auto-play slide every 5 seconds
  useEffect(() => {
    if (bannerMovies.length <= 1) return;
    const timer = setInterval(() => {
      setCurrentSlideIndex(prev => (prev + 1) % bannerMovies.length);
    }, 5000);
    return () => clearInterval(timer);
  }, [bannerMovies.length]);

  const handlePrevSlide = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (bannerMovies.length === 0) return;
    setCurrentSlideIndex(prev => (prev - 1 + bannerMovies.length) % bannerMovies.length);
  };

  const handleNextSlide = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (bannerMovies.length === 0) return;
    setCurrentSlideIndex(prev => (prev + 1) % bannerMovies.length);
  };

  // Fetch Genres on mount
  useEffect(() => {
    const fetchGenres = async () => {
      try {
        const response = await api.get('/api/v1/genres?size=100');
        if (response.data && response.data.success) {
          setGenres(response.data.data.content || []);
        }
      } catch (err) {
        console.error('Error fetching genres:', err);
      }
    };
    fetchGenres();
  }, []);

  // Fetch Movies when filters change
  const fetchMovies = async () => {
    setLoading(true);
    setError('');
    try {
      let url = `/api/v1/movies?status=${selectedStatus}&size=12`;
      if (keyword.trim()) {
        url += `&keyword=${encodeURIComponent(keyword.trim())}`;
      }
      if (selectedGenre) {
        url += `&genreId=${selectedGenre}`;
      }
      const response = await api.get(url);
      if (response.data && response.data.success) {
        setMovies(response.data.data.content || []);
      }
    } catch (err: any) {
      setError(err.message || 'Không thể tải danh sách phim.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMovies();
  }, [selectedGenre, selectedStatus]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchMovies();
  };

  const activeBannerMovie = bannerMovies[currentSlideIndex];

  return (
    <div className="animate-fade-in aurora-page-wrapper" style={{ maxWidth: '1200px', margin: '0 auto', padding: '0 20px', textAlign: 'left' }}>
      
      {/* Top 3 Revenue Movies Popup Modal (NCC Style) */}
      {showTopModal && topRevenueMovies.length > 0 && (
        <div 
          style={{
            position: 'fixed',
            inset: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.85)',
            backdropFilter: 'blur(8px)',
            zIndex: 9999,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '20px',
            animation: 'fadeIn 0.3s ease'
          }}
          onClick={() => setShowTopModal(false)}
        >
          <div 
            style={{ position: 'relative', maxWidth: '400px', width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center' }}
            onClick={e => e.stopPropagation()}
          >
            {/* Top Poster Card Container */}
            <div 
              onClick={() => {
                setShowTopModal(false);
                navigate(`/movies/${topRevenueMovies[currentTopIndex].id}`);
              }}
              style={{
                position: 'relative',
                width: '100%',
                borderRadius: '24px',
                overflow: 'hidden',
                boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.9), 0 0 0 1px rgba(255, 255, 255, 0.2)',
                backgroundColor: '#1e293b',
                cursor: 'pointer',
                transition: 'all 0.3s ease'
              }}
            >
              {/* Close Button X */}
              <button 
                onClick={(e) => { e.stopPropagation(); setShowTopModal(false); }}
                style={{
                  position: 'absolute',
                  top: '14px',
                  right: '14px',
                  width: '36px',
                  height: '36px',
                  borderRadius: '50%',
                  backgroundColor: 'rgba(15, 23, 42, 0.8)',
                  border: '1px solid rgba(255, 255, 255, 0.3)',
                  color: '#ffffff',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  cursor: 'pointer',
                  zIndex: 10,
                  backdropFilter: 'blur(4px)'
                }}
                title="Đóng Hộp Thoại"
                aria-label="Đóng Hộp Thoại"
              >
                <X size={20} />
              </button>

              {/* Top Revenue Badge */}
              <div style={{
                position: 'absolute',
                top: '14px',
                left: '14px',
                padding: '6px 14px',
                borderRadius: '999px',
                background: 'linear-gradient(135deg, #f59e0b, #d97706)',
                color: '#ffffff',
                fontSize: '11px',
                fontWeight: 900,
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                boxShadow: '0 4px 15px rgba(245, 158, 11, 0.5)',
                zIndex: 10
              }}>
                <Trophy size={14} /> TOP {currentTopIndex + 1} DOANH THU
              </div>

              {/* Poster Image */}
              <div style={{ width: '100%', height: '500px', backgroundColor: '#0f172a', position: 'relative' }}>
                {topRevenueMovies[currentTopIndex].posterUrl ? (
                  <img 
                    src={topRevenueMovies[currentTopIndex].posterUrl} 
                    alt={topRevenueMovies[currentTopIndex].title} 
                    style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                  />
                ) : (
                  <div style={{ width: '100%', height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', color: '#94a3b8' }}>
                    <Film size={60} style={{ opacity: 0.4 }} />
                    <span style={{ marginTop: '12px', fontWeight: 600 }}>{topRevenueMovies[currentTopIndex].title}</span>
                  </div>
                )}

                {/* Bottom Overlay Gradient with Movie Title & Action */}
                <div style={{
                  position: 'absolute',
                  inset: 0,
                  background: 'linear-gradient(to top, rgba(15, 23, 42, 0.98) 0%, rgba(15, 23, 42, 0.5) 45%, transparent 100%)',
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'flex-end',
                  padding: '24px',
                  color: '#ffffff',
                  textAlign: 'left'
                }}>
                  <h3 style={{ fontSize: '20px', fontWeight: 900, margin: '0 0 6px 0', textShadow: '0 2px 10px rgba(0,0,0,0.8)' }}>
                    {topRevenueMovies[currentTopIndex].title}
                  </h3>
                  <div style={{ display: 'flex', gap: '8px', alignItems: 'center', marginBottom: '14px' }}>
                    <span style={{ fontSize: '11px', fontWeight: 800, padding: '3px 8px', borderRadius: '4px', backgroundColor: 'rgba(59, 130, 246, 0.4)', color: '#93c5fd' }}>
                      {topRevenueMovies[currentTopIndex].ageRating}
                    </span>
                    <span style={{ fontSize: '12px', color: '#cbd5e1' }}>
                      ⏱️ {topRevenueMovies[currentTopIndex].duration} phút
                    </span>
                  </div>
                  
                  <div className="btn btn-primary" style={{ padding: '10px 20px', fontSize: '13px', fontWeight: 800, borderRadius: '12px', justifyContent: 'center', gap: '8px', boxShadow: '0 4px 15px rgba(99, 102, 241, 0.5)' }}>
                    <Ticket size={16} /> ĐẶT VÉ XEM PHIM NGAY
                  </div>
                </div>
              </div>
            </div>

            {/* Bottom Controls Pill Bar (NCC Style: < Trước   1/3   Tiếp >) */}
            <div style={{
              marginTop: '16px',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '16px',
              padding: '8px 22px',
              borderRadius: '999px',
              backgroundColor: 'rgba(15, 23, 42, 0.95)',
              border: '1px solid rgba(255, 255, 255, 0.2)',
              boxShadow: '0 10px 25px rgba(0,0,0,0.6)',
              color: '#ffffff',
              fontSize: '13px',
              fontWeight: 700
            }}>
              <button 
                onClick={handlePrevTop}
                style={{ background: 'none', border: 'none', color: '#ffffff', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '4px', fontSize: '13px', fontWeight: 700 }}
              >
                <ChevronLeft size={16} /> Trước
              </button>

              <span style={{ color: 'var(--text-muted)', fontSize: '13px', fontFamily: 'monospace', fontWeight: 700 }}>
                {currentTopIndex + 1} / {topRevenueMovies.length}
              </span>

              <button 
                onClick={handleNextTop}
                style={{ background: 'none', border: 'none', color: '#ffffff', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '4px', fontSize: '13px', fontWeight: 700 }}
              >
                Tiếp <ChevronRight size={16} />
              </button>
            </div>

          </div>
        </div>
      )}
      
      {/* NCC-Style Movie Poster Banner Slider / Carousel */}
      <div style={{ position: 'relative', marginBottom: '36px' }}>
        {activeBannerMovie ? (
          <div 
            onClick={() => navigate(`/movies/${activeBannerMovie.id}`)}
            style={{
              height: '420px',
              borderRadius: '24px',
              overflow: 'hidden',
              position: 'relative',
              cursor: 'pointer',
              boxShadow: '0 20px 40px -15px rgba(0, 0, 0, 0.6), 0 0 0 1px rgba(255, 255, 255, 0.1)',
              backgroundColor: '#0b0f19',
              transition: 'all 0.5s ease-in-out'
            }}
          >
            {/* Pure Wide Horizontal Landscape Poster Image (No Text Overlay) */}
            <div 
              style={{
                position: 'absolute',
                inset: 0,
                backgroundImage: `url(${activeBannerMovie.bannerUrl || activeBannerMovie.posterUrl})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                filter: 'none',
                transform: 'scale(1)',
                zIndex: 0
              }}
            />
          </div>
        ) : (
          <div className="glass-card" style={{ height: '380px', borderRadius: '24px', display: 'flex', justifyContent: 'center', alignItems: 'center', color: 'var(--text-secondary)' }}>
            <Film size={40} style={{ opacity: 0.3 }} />
          </div>
        )}

        {/* Left Arrow Navigation Button */}
        <button 
          onClick={handlePrevSlide} 
          style={{
            position: 'absolute',
            left: '20px',
            top: '50%',
            transform: 'translateY(-50%)',
            width: '48px',
            height: '48px',
            borderRadius: '50%',
            backgroundColor: 'rgba(15, 23, 42, 0.75)',
            border: '1px solid rgba(255, 255, 255, 0.25)',
            color: '#ffffff',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 10,
            cursor: 'pointer',
            backdropFilter: 'blur(8px)',
            transition: 'all 0.2s ease',
            boxShadow: '0 4px 15px rgba(0,0,0,0.4)'
          }}
          onMouseEnter={(e) => { e.currentTarget.style.backgroundColor = 'rgba(99, 102, 241, 0.85)'; e.currentTarget.style.scale = '1.1'; }}
          onMouseLeave={(e) => { e.currentTarget.style.backgroundColor = 'rgba(15, 23, 42, 0.75)'; e.currentTarget.style.scale = '1'; }}
          title="Phim trước đó"
          aria-label="Phim trước đó"
        >
          <ChevronLeft size={24} />
        </button>

        {/* Right Arrow Navigation Button */}
        <button 
          onClick={handleNextSlide} 
          style={{
            position: 'absolute',
            right: '20px',
            top: '50%',
            transform: 'translateY(-50%)',
            width: '48px',
            height: '48px',
            borderRadius: '50%',
            backgroundColor: 'rgba(15, 23, 42, 0.75)',
            border: '1px solid rgba(255, 255, 255, 0.25)',
            color: '#ffffff',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 10,
            cursor: 'pointer',
            backdropFilter: 'blur(8px)',
            transition: 'all 0.2s ease',
            boxShadow: '0 4px 15px rgba(0,0,0,0.4)'
          }}
          onMouseEnter={(e) => { e.currentTarget.style.backgroundColor = 'rgba(99, 102, 241, 0.85)'; e.currentTarget.style.scale = '1.1'; }}
          onMouseLeave={(e) => { e.currentTarget.style.backgroundColor = 'rgba(15, 23, 42, 0.75)'; e.currentTarget.style.scale = '1'; }}
          title="Phim tiếp theo"
          aria-label="Phim tiếp theo"
        >
          <ChevronRight size={24} />
        </button>

        {/* Pagination Dots Indicator */}
        {bannerMovies.length > 0 && (
          <div style={{ position: 'absolute', bottom: '16px', left: '50%', transform: 'translateX(-50%)', display: 'flex', gap: '8px', zIndex: 10 }}>
            {bannerMovies.map((_, idx) => (
              <button
                key={idx}
                onClick={(e) => { e.stopPropagation(); setCurrentSlideIndex(idx); }}
                style={{
                  border: 'none',
                  padding: 0,
                  width: idx === currentSlideIndex ? '24px' : '8px',
                  height: '8px',
                  borderRadius: '4px',
                  backgroundColor: idx === currentSlideIndex ? '#38bdf8' : 'rgba(255, 255, 255, 0.4)',
                  cursor: 'pointer',
                  transition: 'all 0.3s ease'
                }}
                aria-label={`Chuyển tới slide ${idx + 1}`}
              />
            ))}
          </div>
        )}
      </div>

      {/* Search & Filters Section */}
      <div className="glass-card" style={{ padding: '24px', marginBottom: '36px', display: 'flex', flexDirection: 'column', gap: '16px', borderRadius: '20px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
          <h2 style={{ fontSize: '20px', fontWeight: 800, margin: 0, display: 'flex', alignItems: 'center', gap: '10px' }}>
            <SlidersHorizontal size={22} style={{ color: 'var(--primary)' }} /> Tìm kiếm & Bộ lọc Phim
          </h2>
          
          {/* Status Tabs */}
          <div style={{ display: 'flex', backgroundColor: 'var(--bg-main)', padding: '4px', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}>
            <button 
              onClick={() => setSelectedStatus('NOW_SHOWING')} 
              style={{
                border: 'none',
                padding: '8px 20px',
                borderRadius: 'var(--radius-sm)',
                fontSize: '13px',
                fontWeight: 700,
                cursor: 'pointer',
                backgroundColor: selectedStatus === 'NOW_SHOWING' ? 'var(--primary)' : 'transparent',
                color: selectedStatus === 'NOW_SHOWING' ? '#fff' : 'var(--text-secondary)',
                boxShadow: selectedStatus === 'NOW_SHOWING' ? '0 4px 12px rgba(99, 102, 241, 0.3)' : 'none',
                transition: 'var(--transition)'
              }}
            >
              🔥 Phim Đang Chiếu
            </button>
            <button 
              onClick={() => setSelectedStatus('UPCOMING')} 
              style={{
                border: 'none',
                padding: '8px 20px',
                borderRadius: 'var(--radius-sm)',
                fontSize: '13px',
                fontWeight: 700,
                cursor: 'pointer',
                backgroundColor: selectedStatus === 'UPCOMING' ? 'var(--primary)' : 'transparent',
                color: selectedStatus === 'UPCOMING' ? '#fff' : 'var(--text-secondary)',
                boxShadow: selectedStatus === 'UPCOMING' ? '0 4px 12px rgba(99, 102, 241, 0.3)' : 'none',
                transition: 'var(--transition)'
              }}
            >
              ✨ Phim Sắp Chiếu
            </button>
          </div>
        </div>

        {/* Input Controls */}
        <form onSubmit={handleSearchSubmit} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
          {/* Search bar */}
          <div className="form-group" style={{ marginBottom: 0, position: 'relative' }}>
            <input 
              type="text" 
              className="form-control" 
              placeholder="Nhập tên phim cần tìm..." 
              value={keyword}
              onChange={e => setKeyword(e.target.value)}
              style={{ paddingLeft: '40px', height: '44px', fontSize: '14px' }}
            />
            <Search size={18} style={{ position: 'absolute', left: '14px', top: '13px', color: 'var(--text-muted)' }} />
          </div>

          {/* Genre select */}
          <div className="form-group" style={{ marginBottom: 0 }}>
            <select 
              className="form-control" 
              value={selectedGenre} 
              onChange={e => setSelectedGenre(e.target.value)}
              style={{ height: '44px', fontSize: '14px' }}
            >
              <option value="">Tất cả thể loại</option>
              {genres.map(g => (
                <option key={g.id} value={g.id}>{g.name}</option>
              ))}
            </select>
          </div>

          {/* Search Button */}
          <button type="submit" className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', height: '44px', fontWeight: 700 }}>
            <Search size={18} /> Tìm kiếm
          </button>
        </form>
      </div>

      {/* Movie Grid */}
      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '60px 0' }}>
          <div className="spinner"></div>
        </div>
      ) : error ? (
        <div className="glass-card" style={{ padding: '20px', display: 'flex', alignItems: 'center', gap: '10px', borderColor: 'var(--danger)' }}>
          <AlertCircle style={{ color: 'var(--danger)' }} />
          <span style={{ color: 'var(--danger)' }}>{error}</span>
        </div>
      ) : movies.length === 0 ? (
        <div className="glass-card" style={{ padding: '60px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          Không tìm thấy phim nào khớp với điều kiện tìm kiếm.
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '28px', marginBottom: '50px' }}>
          {movies.map((movie) => (
            <div key={movie.id} className="movie-card-glow">
              <div style={{ height: '380px', backgroundColor: 'var(--bg-main)', overflow: 'hidden', position: 'relative' }}>
                {movie.posterUrl ? (
                  <img src={movie.posterUrl} alt={movie.title} className="movie-poster-img" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%', width: '100%', color: 'var(--text-muted)', fontSize: '14px', textAlign: 'center', padding: '20px' }}>
                    <Film size={44} style={{ opacity: 0.3, marginBottom: '8px' }} />
                    Chưa có poster
                  </div>
                )}

                <div className="movie-poster-overlay">
                  <Link to={`/movies/${movie.id}`} className="btn btn-primary" style={{ padding: '10px 20px', fontWeight: 700, borderRadius: '999px', backdropFilter: 'blur(4px)' }}>
                    Xem Lịch & Đặt Vé <ChevronRight size={16} />
                  </Link>
                </div>

                <span style={{ position: 'absolute', top: '12px', right: '12px', fontSize: '11px', fontWeight: 800, padding: '4px 10px', borderRadius: '8px', backgroundColor: 'rgba(15, 23, 42, 0.75)', border: '1px solid rgba(255, 255, 255, 0.2)', color: '#f8fafc', backdropFilter: 'blur(6px)' }}>
                  {movie.ageRating}
                </span>
              </div>
              <div style={{ padding: '22px', flexGrow: 1, display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                <div>
                  <h3 style={{ fontSize: '19px', fontWeight: 800, margin: '0 0 10px 0', color: 'var(--text-primary)', lineHeight: 1.3 }}>{movie.title}</h3>
                  <div style={{ display: 'flex', gap: '6px', marginBottom: '14px', flexWrap: 'wrap' }}>
                    {movie.genres && movie.genres.map((genre: any) => (
                      <span key={genre.id} style={{ fontSize: '11px', fontWeight: 600, padding: '3px 10px', borderRadius: '6px', backgroundColor: 'rgba(99, 102, 241, 0.1)', color: 'var(--primary)', border: '1px solid rgba(99, 102, 241, 0.2)' }}>
                        {genre.name}
                      </span>
                    ))}
                  </div>
                  <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginBottom: '18px', display: '-webkit-box', WebkitLineClamp: 3, WebkitBoxOrient: 'vertical', overflow: 'hidden', lineHeight: 1.5 }}>
                    {movie.description || 'Chưa có mô tả ngắn về bộ phim này.'}
                  </p>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '13px', borderTop: '1px solid var(--border-color)', paddingTop: '14px' }}>
                  <span style={{ color: 'var(--text-muted)', fontWeight: 500 }}>⏱️ {movie.duration} phút</span>
                  <Link to={`/movies/${movie.id}`} className="btn btn-primary" style={{ padding: '6px 14px', fontSize: '12px', fontWeight: 700 }}>
                    Chi tiết <ChevronRight size={14} />
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Home;
