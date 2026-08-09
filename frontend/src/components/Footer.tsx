import React from 'react';
import { Link } from 'react-router-dom';

const Footer: React.FC = () => {
  return (
    <footer style={{ 
      borderTop: '1px solid var(--border-color)', 
      padding: '24px', 
      textAlign: 'center', 
      fontSize: '13px', 
      color: 'var(--text-muted)',
      marginTop: '60px',
      backgroundColor: 'var(--bg-card)'
    }}>
      <div style={{ maxWidth: '1200px', margin: '0 auto', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px', padding: '0 20px' }}>
        <span>Movie Ticket Booking System © Boizi 2026. Tất cả các quyền được bảo lưu.</span>
        <div style={{ display: 'flex', gap: '16px' }}>
          <Link to="/terms" style={{ color: 'var(--text-muted)', textDecoration: 'none' }}>Điều khoản dịch vụ</Link>
          <Link to="/privacy" style={{ color: 'var(--text-muted)', textDecoration: 'none' }}>Chính sách bảo mật</Link>
          <Link to="/contact" style={{ color: 'var(--text-muted)', textDecoration: 'none' }}>Liên hệ hỗ trợ</Link>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
