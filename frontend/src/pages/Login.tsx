import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogIn } from 'lucide-react';

const Login: React.FC = () => {
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState<string>('admin@moviebooking.com');
  const [password, setPassword] = useState<string>('AdminPassword123');
  const [error, setError] = useState<string>('');
  const [submitting, setSubmitting] = useState<boolean>(false);

  const from = (location.state as any)?.from?.pathname || '/';

  useEffect(() => {
    if (isAuthenticated) {
      navigate(from, { replace: true });
    }
  }, [isAuthenticated, navigate, from]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      await login(email, password);
      navigate(from, { replace: true });
    } catch (err: any) {
      setError(err.message || 'Lỗi đăng nhập');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="animate-fade-in" style={{ maxWidth: '400px', margin: '40px auto', padding: '0 20px' }}>
      <div className="glass-card" style={{ padding: '30px', textAlign: 'left' }}>
        <h2 style={{ fontSize: '22px', fontWeight: 700, marginBottom: '8px', textAlign: 'center' }}>Đăng nhập tài khoản</h2>
        <p style={{ fontSize: '13px', color: 'var(--text-secondary)', textAlign: 'center', marginBottom: '24px' }}>
          Đăng nhập để đặt vé phim và xem lịch sử đặt chỗ.
        </p>

        {error && (
          <div style={{ 
            backgroundColor: 'rgba(239,68,68,0.1)', 
            border: '1px solid rgba(239,68,68,0.2)', 
            color: 'var(--danger)', 
            padding: '10px 14px', 
            borderRadius: 'var(--radius-sm)', 
            fontSize: '13px', 
            marginBottom: '16px' 
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Địa chỉ Email</label>
            <input 
              type="email" 
              className="form-control" 
              value={email} 
              onChange={(e) => setEmail(e.target.value)} 
              required 
            />
          </div>
          <div className="form-group">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <label className="form-label">Mật khẩu</label>
              <Link to="/forgot-password" style={{ fontSize: '12px', color: 'var(--primary)', textDecoration: 'none' }}>
                Quên mật khẩu?
              </Link>
            </div>
            <input 
              type="password" 
              className="form-control" 
              value={password} 
              onChange={(e) => setPassword(e.target.value)} 
              required 
            />
          </div>
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '10px' }} disabled={submitting}>
            {submitting ? 'Đang xử lý...' : 'Đăng nhập'}
          </button>
        </form>

        <div style={{ marginTop: '20px', textAlign: 'center', fontSize: '13px', color: 'var(--text-secondary)' }}>
          Chưa có tài khoản? <Link to="/register" style={{ color: 'var(--primary)', fontWeight: 600 }}>Đăng ký ngay</Link>
        </div>
      </div>

      <div className="glass-card" style={{ marginTop: '20px', padding: '16px', fontSize: '12px', textAlign: 'left', backgroundColor: 'rgba(99,102,241,0.05)' }}>
        <strong>Tài khoản Demo sẵn có:</strong>
        <ul style={{ margin: '6px 0 0 0', paddingLeft: '20px' }}>
          <li>Admin: <code>admin@moviebooking.com</code> / <code>AdminPassword123</code></li>
        </ul>
      </div>
    </div>
  );
};

export default Login;
