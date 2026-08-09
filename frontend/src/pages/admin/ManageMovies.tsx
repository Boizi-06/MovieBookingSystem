import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';
import AdminLayout from '../../components/AdminLayout';
import ConfirmModal from '../../components/ConfirmModal';
import { Plus, Edit2, Trash2, Search, Film, AlertCircle, X, Check } from 'lucide-react';

const ManageMovies: React.FC = () => {
  const [movies, setMovies] = useState<any[]>([]);
  const [genres, setGenres] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');

  // Pagination & Search
  const [keyword, setKeyword] = useState<string>('');
  const [currentPage, setCurrentPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [totalElements, setTotalElements] = useState<number>(0);
  const pageSize = 8;

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [isEditMode, setIsEditMode] = useState<boolean>(false);
  const [currentMovieId, setCurrentMovieId] = useState<number | null>(null);

  // Form State
  const [title, setTitle] = useState<string>('');
  const [posterUrl, setPosterUrl] = useState<string>('');
  const [bannerUrl, setBannerUrl] = useState<string>('');
  const [duration, setDuration] = useState<number>(120);
  const [posterFile, setPosterFile] = useState<File | null>(null);
  const [bannerFile, setBannerFile] = useState<File | null>(null);
  const [releaseDate, setReleaseDate] = useState<string>('');
  const [endDate, setEndDate] = useState<string>('');
  const [ageRating, setAgeRating] = useState<string>('P');
  const [language, setLanguage] = useState<string>('Tiếng Việt');
  const [director, setDirector] = useState<string>('');
  const [cast, setCast] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [trailerUrl, setTrailerUrl] = useState<string>('');
  const [status, setStatus] = useState<string>('NOW_SHOWING');
  const [selectedGenreIds, setSelectedGenreIds] = useState<number[]>([]);

  const fetchGenres = async () => {
    try {
      const response = await api.get('/api/v1/genres?size=100');
      if (response.data?.success) {
        setGenres(response.data.data.content || []);
      }
    } catch (err) {
      console.error('Error fetching genres:', err);
    }
  };

  const fetchMovies = async (page = 0) => {
    setLoading(true);
    setError('');
    try {
      let url = `/api/v1/movies?page=${page}&size=${pageSize}`;
      if (keyword.trim()) {
        url += `&keyword=${encodeURIComponent(keyword.trim())}`;
      }
      const response = await api.get(url);
      if (response.data?.success) {
        const data = response.data.data;
        setMovies(data.content || []);
        setTotalPages(data.totalPages || 0);
        setTotalElements(data.totalElements || 0);
        setCurrentPage(data.number || 0);
      }
    } catch (err: any) {
      setError(err.message || 'Không thể tải danh sách phim.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchGenres();
    fetchMovies(0);
  }, []);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchMovies(0);
  };

  const openAddModal = () => {
    setIsEditMode(false);
    setCurrentMovieId(null);
    setTitle('');
    setDescription('');
    setDuration(120);
    setReleaseDate('');
    setEndDate('');
    setAgeRating('P');
    setLanguage('Tiếng Việt');
    setDirector('');
    setCast('');
    setPosterUrl('');
    setBannerUrl('');
    setPosterFile(null);
    setBannerFile(null);
    setTrailerUrl('');
    setStatus('NOW_SHOWING');
    setSelectedGenreIds([]);
    setIsModalOpen(true);
  };

  const openEditModal = (movie: any) => {
    setIsEditMode(true);
    setCurrentMovieId(movie.id);
    setTitle(movie.title || '');
    setDescription(movie.description || '');
    setDuration(movie.duration || 120);
    setReleaseDate(movie.releaseDate || '');
    setEndDate(movie.endDate || '');
    setAgeRating(movie.ageRating || 'P');
    setLanguage(movie.language || 'Tiếng Việt');
    setDirector(movie.director || '');
    setCast(movie.cast || '');
    setPosterUrl(movie.posterUrl || '');
    setBannerUrl(movie.bannerUrl || '');
    setPosterFile(null);
    setBannerFile(null);
    setTrailerUrl(movie.trailerUrl || '');
    setStatus(movie.status || 'NOW_SHOWING');
    setSelectedGenreIds(movie.genres ? movie.genres.map((g: any) => g.id) : []);
    setIsModalOpen(true);
  };

  const handleGenreToggle = (genreId: number) => {
    if (selectedGenreIds.includes(genreId)) {
      setSelectedGenreIds(selectedGenreIds.filter(id => id !== genreId));
    } else {
      setSelectedGenreIds([...selectedGenreIds, genreId]);
    }
  };

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (selectedGenreIds.length === 0) {
      setError('Vui lòng chọn ít nhất một thể loại phim.');
      return;
    }

    let uploadedPosterUrl = posterUrl;
    // If a new poster file is selected, upload to Cloudinary
    if (posterFile) {
      try {
        const formData = new FormData();
        formData.append('file', posterFile);
        const uploadResp = await api.post('/api/v1/upload-poster', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        if (uploadResp.data?.success) {
          uploadedPosterUrl = uploadResp.data.data;
        } else {
          setError('Tải ảnh poster lên Cloudinary thất bại.');
          return;
        }
      } catch (err: any) {
        setError(err.response?.data?.message || err.message || 'Lỗi khi tải poster lên.');
        return;
      }
    }

    let uploadedBannerUrl = bannerUrl;
    // If a new banner poster file is selected, upload to Cloudinary
    if (bannerFile) {
      try {
        const formData = new FormData();
        formData.append('file', bannerFile);
        const uploadResp = await api.post('/api/v1/upload-poster', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        if (uploadResp.data?.success) {
          uploadedBannerUrl = uploadResp.data.data;
        } else {
          setError('Tải ảnh banner poster ngang lên Cloudinary thất bại.');
          return;
        }
      } catch (err: any) {
        setError(err.response?.data?.message || err.message || 'Lỗi khi tải banner poster ngang lên Cloudinary.');
        return;
      }
    }

    const payload = {
      title,
      description,
      duration,
      releaseDate,
      endDate: endDate || null,
      ageRating,
      language,
      director,
      cast,
      posterUrl: uploadedPosterUrl,
      bannerUrl: uploadedBannerUrl,
      trailerUrl,
      status,
      genreIds: selectedGenreIds
    };

    try {
      if (isEditMode && currentMovieId) {
        const response = await api.put(`/api/v1/movies/${currentMovieId}`, payload);
        if (response.data?.success) {
          setSuccess('Cập nhật thông tin phim thành công!');
          setIsModalOpen(false);
          fetchMovies(currentPage);
        }
      } else {
        const response = await api.post('/api/v1/movies', payload);
        if (response.data?.success) {
          setSuccess('Thêm phim mới thành công!');
          setIsModalOpen(false);
          fetchMovies(0);
        }
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Lưu thông tin phim thất bại.');
    }
  };

  // Confirm Modal State
  const [confirmModal, setConfirmModal] = useState<{ isOpen: boolean; movieId: number | null; movieTitle: string }>({
    isOpen: false,
    movieId: null,
    movieTitle: ''
  });

  const promptDeleteMovie = (movieId: number, movieTitle: string) => {
    setConfirmModal({ isOpen: true, movieId, movieTitle });
  };

  const executeDeleteMovie = async () => {
    if (!confirmModal.movieId) return;
    const { movieId, movieTitle } = confirmModal;
    setError('');
    setSuccess('');
    try {
      const response = await api.delete(`/api/v1/movies/${movieId}`);
      if (response.data?.success) {
        setSuccess(`Xóa phim "${movieTitle}" thành công!`);
        fetchMovies(currentPage);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Xóa phim thất bại.');
    } finally {
      setConfirmModal({ isOpen: false, movieId: null, movieTitle: '' });
    }
  };

  return (
    <AdminLayout>
      <div className="animate-fade-in" style={{ textAlign: 'left' }}>
        
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
          <div>
            <h1 style={{ fontSize: '26px', fontWeight: 800, margin: '0' }}>Quản lý Phim</h1>
            <p style={{ color: 'var(--text-secondary)', fontSize: '14px', margin: '4px 0 0 0' }}>Tổng số phim: {totalElements}</p>
          </div>
          <button onClick={openAddModal} className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Plus size={16} /> Thêm phim mới
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

        {/* Filters */}
        <div className="glass-card" style={{ padding: '16px 20px', marginBottom: '24px' }}>
          <form onSubmit={handleSearchSubmit} style={{ display: 'flex', gap: '12px' }}>
            <div style={{ position: 'relative', flexGrow: 1 }}>
              <input 
                type="text" 
                className="form-control" 
                placeholder="Tìm kiếm phim theo từ khóa..." 
                value={keyword}
                onChange={e => setKeyword(e.target.value)}
                style={{ paddingLeft: '38px', marginBottom: 0 }}
              />
              <Search size={16} style={{ position: 'absolute', left: '14px', top: '12px', color: 'var(--text-muted)' }} />
            </div>
            <button type="submit" className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              Tìm kiếm
            </button>
          </form>
        </div>

        {/* Movies Table */}
        {loading ? (
          <div style={{ display: 'flex', justifyContent: 'center', padding: '60px 0' }}><div className="spinner"></div></div>
        ) : movies.length === 0 ? (
          <div className="glass-card" style={{ padding: '60px', textAlign: 'center', color: 'var(--text-secondary)' }}>
            Không tìm thấy phim nào.
          </div>
        ) : (
          <div className="glass-card" style={{ padding: '10px', overflowX: 'auto', marginBottom: '24px' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid var(--border-color)', textAlign: 'left' }}>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Ảnh</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Tên phim</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Thông tin</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Thể loại</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Trạng thái</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700, textAlign: 'center' }}>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {movies.map((movie) => (
                  <tr key={movie.id} style={{ borderBottom: '1px solid var(--border-color)' }} className="table-row-hover">
                    <td style={{ padding: '12px 16px' }}>
                      <div style={{ width: '45px', height: '65px', borderRadius: '4px', overflow: 'hidden', backgroundColor: 'var(--bg-main)' }}>
                        {movie.posterUrl ? (
                          <img src={movie.posterUrl} alt={movie.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                        ) : (
                          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', color: 'var(--text-muted)' }}><Film size={20} /></div>
                        )}
                      </div>
                    </td>
                    <td style={{ padding: '12px 16px', fontWeight: 600, fontSize: '14px' }}>
                      <div>{movie.title}</div>
                      <span className="badge badge-info" style={{ fontSize: '10px', marginTop: '4px' }}>{movie.ageRating}</span>
                    </td>
                    <td style={{ padding: '12px 16px', fontSize: '13px', color: 'var(--text-secondary)' }}>
                      <div>Thời lượng: {movie.duration} phút</div>
                      <div>Ngôn ngữ: {movie.language}</div>
                    </td>
                    <td style={{ padding: '12px 16px', fontSize: '13px' }}>
                      <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap' }}>
                        {movie.genres && movie.genres.map((g: any) => (
                          <span key={g.id} style={{ padding: '2px 6px', borderRadius: '4px', backgroundColor: 'var(--bg-main)', fontSize: '11px', color: 'var(--text-secondary)' }}>{g.name}</span>
                        ))}
                      </div>
                    </td>
                    <td style={{ padding: '12px 16px' }}>
                      <span className={`badge ${movie.status === 'NOW_SHOWING' ? 'badge-success' : movie.status === 'UPCOMING' ? 'badge-info' : 'badge-danger'}`}>
                        {movie.status === 'NOW_SHOWING' ? 'Đang chiếu' : movie.status === 'UPCOMING' ? 'Sắp chiếu' : movie.status === 'ENDED' ? 'Đã kết thúc' : 'Không hoạt động'}
                      </span>
                    </td>
                    <td style={{ padding: '12px 16px', textAlign: 'center' }}>
                      <div style={{ display: 'flex', justifyContent: 'center', gap: '8px' }}>
                        <button onClick={() => openEditModal(movie)} className="btn btn-secondary" style={{ padding: '6px', minWidth: 'auto' }} title="Sửa">
                          <Edit2 size={14} style={{ color: 'var(--primary)' }} />
                        </button>
                        <button onClick={() => promptDeleteMovie(movie.id, movie.title)} className="btn btn-secondary" style={{ padding: '6px', minWidth: 'auto' }} title="Xóa">
                          <Trash2 size={14} style={{ color: 'var(--danger)' }} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div style={{ display: 'flex', justifyContent: 'center', gap: '10px', marginBottom: '40px' }}>
            <button disabled={currentPage === 0} onClick={() => fetchMovies(currentPage - 1)} className="btn btn-secondary" style={{ padding: '6px 12px' }}>Trước</button>
            <span style={{ display: 'flex', alignItems: 'center', fontSize: '14px', color: 'var(--text-secondary)' }}>Trang {currentPage + 1} / {totalPages}</span>
            <button disabled={currentPage === totalPages - 1} onClick={() => fetchMovies(currentPage + 1)} className="btn btn-secondary" style={{ padding: '6px 12px' }}>Sau</button>
          </div>
        )}

        {/* Form Modal */}
        {isModalOpen && (
          <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(15, 23, 42, 0.85)', backdropFilter: 'blur(8px)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000, padding: '24px' }}>
            <div className="animate-fade-in" style={{ backgroundColor: 'var(--bg-card)', color: 'var(--text-main)', width: '100%', maxWidth: '720px', maxHeight: '90vh', overflowY: 'auto', padding: '32px', borderRadius: '16px', border: '1px solid var(--border-color)', boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.7), 0 0 0 1px rgba(255, 255, 255, 0.1)', position: 'relative' }}>
              <button onClick={() => setIsModalOpen(false)} style={{ position: 'absolute', top: '16px', right: '16px', background: 'transparent', border: 'none', cursor: 'pointer', color: 'var(--text-secondary)' }} aria-label="Đóng modal">
                <X size={20} />
              </button>
              <h2 style={{ fontSize: '22px', fontWeight: 800, marginBottom: '24px', borderBottom: '1px solid var(--border-color)', paddingBottom: '12px', color: 'var(--primary)' }}>
                {isEditMode ? 'Cập nhật thông tin Phim' : 'Thêm phim mới'}
              </h2>
              <form onSubmit={handleFormSubmit}>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '20px' }}>
                  <div className="form-group" style={{ gridColumn: '1 / -1' }}>
                    <label className="form-label">Tên phim *</label>
                    <input type="text" className="form-control" value={title} onChange={e => setTitle(e.target.value)} required placeholder="Nhập tên phim" />
                  </div>
                  <div className="form-group" style={{ gridColumn: '1 / -1' }}>
                    <label className="form-label">Tóm tắt phim</label>
                    <textarea className="form-control" style={{ height: '100px', resize: 'vertical' }} value={description} onChange={e => setDescription(e.target.value)} placeholder="Nhập mô tả phim" />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Thời lượng (phút) *</label>
                    <input type="number" className="form-control" value={duration} onChange={e => setDuration(Number(e.target.value))} required min={1} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Ngôn ngữ *</label>
                    <input type="text" className="form-control" value={language} onChange={e => setLanguage(e.target.value)} required />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Ngày khởi chiếu *</label>
                    <input type="date" className="form-control" value={releaseDate} onChange={e => setReleaseDate(e.target.value)} required />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Ngày kết thúc chiếu</label>
                    <input type="date" className="form-control" value={endDate} onChange={e => setEndDate(e.target.value)} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Độ tuổi *</label>
                    <select className="form-control" value={ageRating} onChange={e => setAgeRating(e.target.value)} required>
                      <option value="P">P (Mọi lứa tuổi)</option>
                      <option value="C13">C13 (Trên 13 tuổi)</option>
                      <option value="C16">C16 (Trên 16 tuổi)</option>
                      <option value="C18">C18 (Trên 18 tuổi)</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Trạng thái *</label>
                    <select className="form-control" value={status} onChange={e => setStatus(e.target.value)} required>
                      <option value="NOW_SHOWING">Đang chiếu (NOW_SHOWING)</option>
                      <option value="UPCOMING">Sắp chiếu (UPCOMING)</option>
                      <option value="ENDED">Đã kết thúc (ENDED)</option>
                      <option value="INACTIVE">Không hoạt động (INACTIVE)</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Đạo diễn</label>
                    <input type="text" className="form-control" value={director} onChange={e => setDirector(e.target.value)} placeholder="Tên đạo diễn" />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Diễn viên</label>
                    <input type="text" className="form-control" value={cast} onChange={e => setCast(e.target.value)} placeholder="Tên các diễn viên" />
                  </div>
                  <div className="form-group" style={{ gridColumn: '1 / -1' }}>
                    <label className="form-label">Ảnh Poster Đứng (Upload Cloudinary / URL)</label>
                    <input type="file" className="form-control" accept="image/*" onChange={e => {
                      const file = e.target.files && e.target.files[0];
                      if (file) {
                        setPosterFile(file);
                        setPosterUrl(URL.createObjectURL(file));
                      }
                    }} />
                    <input type="text" className="form-control" style={{ marginTop: '8px' }} value={posterUrl} onChange={e => setPosterUrl(e.target.value)} placeholder="Hoặc dán trực tiếp URL ảnh poster đứng..." />
                  </div>
                  <div className="form-group" style={{ gridColumn: '1 / -1' }}>
                    <label className="form-label">Ảnh Poster Ngang - Banner Slider (Upload Cloudinary / URL)</label>
                    <input type="file" className="form-control" accept="image/*" onChange={e => {
                      const file = e.target.files && e.target.files[0];
                      if (file) {
                        setBannerFile(file);
                        setBannerUrl(URL.createObjectURL(file));
                      }
                    }} />
                    <input type="text" className="form-control" style={{ marginTop: '8px' }} value={bannerUrl} onChange={e => setBannerUrl(e.target.value)} placeholder="Hoặc dán trực tiếp URL ảnh poster ngang..." />
                    {bannerUrl && (
                      <div style={{ marginTop: '10px', borderRadius: '12px', overflow: 'hidden', height: '140px', border: '1px solid var(--border-color)' }}>
                        <img src={bannerUrl} alt="Xem trước Poster Ngang" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                      </div>
                    )}
                  </div>
                  <div className="form-group" style={{ gridColumn: '1 / -1' }}>
                    <label className="form-label">Trailer URL (Youtube)</label>
                    <input type="text" className="form-control" value={trailerUrl} onChange={e => setTrailerUrl(e.target.value)} placeholder="Đường dẫn video trailer" />
                  </div>
                  <div className="form-group" style={{ gridColumn: '1 / -1' }}>
                    <label className="form-label" style={{ marginBottom: '8px', display: 'block' }}>Thể loại phim *</label>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))', gap: '10px', padding: '12px', border: '1px solid var(--border-color)', borderRadius: 'var(--radius-sm)', backgroundColor: 'var(--bg-main)' }}>
                      {genres.map(g => (
                        <label key={g.id} style={{ display: 'flex', alignItems: 'center', gap: '6px', cursor: 'pointer', fontSize: '13px' }}>
                          <input type="checkbox" checked={selectedGenreIds.includes(g.id)} onChange={() => handleGenreToggle(g.id)} />
                          {g.name}
                        </label>
                      ))}
                    </div>
                  </div>
                </div>
                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '32px', borderTop: '1px solid var(--border-color)', paddingTop: '20px' }}>
                  <button type="button" onClick={() => setIsModalOpen(false)} className="btn btn-secondary">Hủy</button>
                  <button type="submit" className="btn btn-primary">Lưu</button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Delete Confirmation Modal */}
        <ConfirmModal
          isOpen={confirmModal.isOpen}
          title="Xác nhận xóa Phim"
          message={`Bạn có chắc chắn muốn xóa/ẩn phim "${confirmModal.movieTitle}"? Hành động này sẽ chuyển trạng thái phim sang ngưng hoạt động.`}
          confirmText="Xóa phim"
          cancelText="Hủy"
          variant="danger"
          onConfirm={executeDeleteMovie}
          onClose={() => setConfirmModal({ isOpen: false, movieId: null, movieTitle: '' })}
        />

      </div>
    </AdminLayout>
  );
};

export default ManageMovies;
