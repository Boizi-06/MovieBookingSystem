import React from 'react';
import { NavLink } from 'react-router-dom';
import { BarChart3, Film, Calendar, Users, ShieldAlert, Building2, Ticket, Gift, Popcorn } from 'lucide-react';

interface AdminLayoutProps {
  children: React.ReactNode;
}

const AdminLayout: React.FC<AdminLayoutProps> = ({ children }) => {
  return (
    <div style={{ display: 'flex', minHeight: 'calc(100vh - 120px)', maxWidth: '1400px', margin: '20px auto', padding: '0 20px', gap: '24px' }}>
      
      {/* Sidebar Navigation */}
      <aside className="glass-card" style={{ width: '260px', padding: '24px', flexShrink: 0, height: 'fit-content', textAlign: 'left' }}>
        <h2 style={{ fontSize: '18px', fontWeight: 800, marginBottom: '20px', color: 'var(--primary)', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <ShieldAlert size={20} /> Khu vực Quản trị
        </h2>
        
        <nav style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
          <NavLink 
            to="/admin/dashboard" 
            style={({ isActive }) => ({
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              padding: '12px 16px',
              borderRadius: 'var(--radius-sm)',
              textDecoration: 'none',
              fontSize: '14px',
              fontWeight: 600,
              color: isActive ? '#fff' : 'var(--text-secondary)',
              backgroundColor: isActive ? 'var(--primary)' : 'transparent',
              transition: 'var(--transition)'
            })}
          >
            <BarChart3 size={18} /> Bảng điều khiển
          </NavLink>

          <NavLink 
            to="/admin/movies" 
            style={({ isActive }) => ({
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              padding: '12px 16px',
              borderRadius: 'var(--radius-sm)',
              textDecoration: 'none',
              fontSize: '14px',
              fontWeight: 600,
              color: isActive ? '#fff' : 'var(--text-secondary)',
              backgroundColor: isActive ? 'var(--primary)' : 'transparent',
              transition: 'var(--transition)'
            })}
          >
            <Film size={18} /> Quản lý Phim
          </NavLink>

          <NavLink 
            to="/admin/cinemas" 
            style={({ isActive }) => ({
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              padding: '12px 16px',
              borderRadius: 'var(--radius-sm)',
              textDecoration: 'none',
              fontSize: '14px',
              fontWeight: 600,
              color: isActive ? '#fff' : 'var(--text-secondary)',
              backgroundColor: isActive ? 'var(--primary)' : 'transparent',
              transition: 'var(--transition)'
            })}
          >
            <Building2 size={18} /> Quản lý Rạp & Phòng
          </NavLink>

          <NavLink 
            to="/admin/showtimes" 
            style={({ isActive }) => ({
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              padding: '12px 16px',
              borderRadius: 'var(--radius-sm)',
              textDecoration: 'none',
              fontSize: '14px',
              fontWeight: 600,
              color: isActive ? '#fff' : 'var(--text-secondary)',
              backgroundColor: isActive ? 'var(--primary)' : 'transparent',
              transition: 'var(--transition)'
            })}
          >
            <Calendar size={18} /> Quản lý Lịch chiếu
          </NavLink>

          <NavLink 
            to="/admin/bookings" 
            style={({ isActive }) => ({
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              padding: '12px 16px',
              borderRadius: 'var(--radius-sm)',
              textDecoration: 'none',
              fontSize: '14px',
              fontWeight: 600,
              color: isActive ? '#fff' : 'var(--text-secondary)',
              backgroundColor: isActive ? 'var(--primary)' : 'transparent',
              transition: 'var(--transition)'
            })}
          >
            <Ticket size={18} /> Quản lý Đơn hàng
          </NavLink>

          <NavLink 
            to="/admin/users" 
            style={({ isActive }) => ({
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              padding: '12px 16px',
              borderRadius: 'var(--radius-sm)',
              textDecoration: 'none',
              fontSize: '14px',
              fontWeight: 600,
              color: isActive ? '#fff' : 'var(--text-secondary)',
              backgroundColor: isActive ? 'var(--primary)' : 'transparent',
              transition: 'var(--transition)'
            })}
          >
            <Users size={18} /> Quản lý Người dùng
          </NavLink>
        </nav>
      </aside>

      {/* Main Content Area */}
      <main style={{ flexGrow: 1, minWidth: 0 }}>
        {children}
      </main>
    </div>
  );
};

export default AdminLayout;
