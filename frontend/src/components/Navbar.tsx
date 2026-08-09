import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Film, User as UserIcon, ShieldCheck, LogOut, LogIn, UserPlus, Compass, Ticket } from 'lucide-react';

const Navbar: React.FC = () => {
  const { user, logout, isAuthenticated, isAdmin } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    // Mặc định luôn luôn áp dụng Dark Mode cho toàn bộ hệ thống
    document.body.classList.add('dark');
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="glass-card" style={{
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      padding: '16px 24px',
      margin: '20px auto 30px auto',
      maxWidth: '1200px',
      width: 'calc(100% - 40px)',
      borderRadius: 'var(--radius-md)'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '24px' }}>
        <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div style={{
            background: 'linear-gradient(135deg, var(--primary), var(--secondary))',
            color: '#fff',
            padding: '8px',
            borderRadius: 'var(--radius-sm)'
          }}>
            <Film size={20} />
          </div>
          <span style={{ fontSize: '18px', fontWeight: 800, letterSpacing: '-0.5px' }}>
            MovieBooking
          </span>
        </Link>

        {/* Navigation Links */}
        <div style={{ display: 'flex', gap: '20px', fontSize: '14px', fontWeight: 500 }}>
          <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-secondary)' }}>
            <Compass size={16} /> Trang chủ
          </Link>
          {isAuthenticated && (
            <Link to="/my-bookings" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-secondary)' }}>
              <Ticket size={16} style={{ color: '#ef4444' }} /> Lịch sử đặt vé
            </Link>
          )}
          {isAuthenticated && (
            <Link to="/profile" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-secondary)' }}>
              <UserIcon size={16} /> Cá nhân
            </Link>
          )}
          {isAdmin && (
            <Link to="/admin/dashboard" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--primary)', fontWeight: 600 }}>
              <ShieldCheck size={16} /> Quản trị
            </Link>
          )}
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>

        {/* User Auth Section */}
        {isAuthenticated && user ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontSize: '14px', fontWeight: 600 }}>{user.fullname}</div>
              <span className={`badge ${user.role === 'ADMIN' ? 'badge-danger' : 'badge-info'}`} style={{ fontSize: '9px', padding: '2px 6px' }}>
                {user.role}
              </span>
            </div>
            <button onClick={handleLogout} className="btn btn-secondary" style={{ padding: '8px 12px', fontSize: '13px' }}>
              <LogOut size={14} /> Đăng xuất
            </button>
          </div>
        ) : (
          <div style={{ display: 'flex', gap: '10px' }}>
            <Link to="/login" className="btn btn-secondary" style={{ padding: '8px 16px', fontSize: '13px' }}>
              <LogIn size={14} /> Đăng nhập
            </Link>
            <Link to="/register" className="btn btn-primary" style={{ padding: '8px 16px', fontSize: '13px' }}>
              <UserPlus size={14} /> Đăng ký
            </Link>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
