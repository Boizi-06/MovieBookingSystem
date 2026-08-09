import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../services/api';

const ForgotPassword: React.FC = () => {
  const [email, setEmail] = useState<string>('');
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');
  const [submitting, setSubmitting] = useState<boolean>(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSubmitting(true);
    try {
      const response = await api.post('/api/v1/auth/forgot-password', { email });
      if (response.data && response.data.success) {
        setSuccess('Mã xác thực khôi phục mật khẩu đã được gửi về email của bạn! Vui lòng kiểm tra hộp thư email và nhập mã để hoàn tất đặt lại mật khẩu mới.');
        setEmail('');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Gửi yêu cầu thất bại.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="animate-fade-in" style={{ maxWidth: '400px', margin: '40px auto', padding: '0 20px' }}>
      <div className="glass-card" style={{ padding: '30px', textAlign: 'left' }}>
        <h2 style={{ fontSize: '20px', fontWeight: 700, marginBottom: '8px', textAlign: 'center' }}>Quên mật khẩu</h2>
        <p style={{ fontSize: '13px', color: 'var(--text-secondary)', textAlign: 'center', marginBottom: '24px' }}>
          Nhập địa chỉ email của bạn, hệ thống sẽ gửi mã xác thực khôi phục mật khẩu.
        </p>

        {error && (
          <div style={{ backgroundColor: 'rgba(239,68,68,0.1)', border: '1px solid rgba(239,68,68,0.2)', color: 'var(--danger)', padding: '10px 14px', borderRadius: 'var(--radius-sm)', fontSize: '13px', marginBottom: '16px' }}>
            {error}
          </div>
        )}

        {success && (
          <div style={{ backgroundColor: 'rgba(16,185,129,0.1)', border: '1px solid rgba(16,185,129,0.2)', color: 'var(--success)', padding: '10px 14px', borderRadius: 'var(--radius-sm)', fontSize: '13px', marginBottom: '20px' }}>
            {success}
            <div style={{ marginTop: '16px', textAlign: 'center' }}>
              <Link to="/reset-password" className="btn btn-primary" style={{ display: 'inline-block', fontSize: '13px', padding: '8px 16px', textDecoration: 'none' }}>
                👉 Đến trang Đặt lại Mật khẩu
              </Link>
            </div>
          </div>
        )}

        {!success && (
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Email tài khoản</label>
              <input 
                type="email" 
                className="form-control" 
                value={email} 
                onChange={(e) => setEmail(e.target.value)} 
                required 
                placeholder="customer@gmail.com"
              />
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '10px' }} disabled={submitting}>
              {submitting ? 'Đang gửi...' : 'Gửi mã khôi phục'}
            </button>
          </form>
        )}

        <div style={{ marginTop: '20px', textAlign: 'center', fontSize: '13px', color: 'var(--text-secondary)' }}>
          Quay lại <Link to="/login" style={{ color: 'var(--primary)', fontWeight: 600 }}>Đăng nhập</Link>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;
