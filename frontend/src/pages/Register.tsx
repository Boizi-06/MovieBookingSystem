import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../services/api';

const Register: React.FC = () => {
  const navigate = useNavigate();
  const [fullname, setFullname] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [phone, setPhone] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');
  const [submitting, setSubmitting] = useState<boolean>(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    
    if (password !== confirmPassword) {
      setError('Mật khẩu xác nhận không khớp.');
      return;
    }

    setSubmitting(true);
    try {
      const response = await api.post('/api/v1/auth/register', {
        fullname,
        email,
        phone,
        password,
        confirmPassword
      });
      if (response.data && response.data.success) {
        setSuccess('Đăng ký tài khoản thành công! Bạn đang được chuyển đến trang kích hoạt...');
        const registeredEmail = email;
        setFullname('');
        setEmail('');
        setPhone('');
        setPassword('');
        setConfirmPassword('');
        setTimeout(() => {
          navigate('/verify-email', { state: { email: registeredEmail } });
        }, 1500);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Đăng ký thất bại.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="animate-fade-in" style={{ maxWidth: '420px', margin: '30px auto', padding: '0 20px' }}>
      <div className="glass-card" style={{ padding: '30px', textAlign: 'left' }}>
        <h2 style={{ fontSize: '22px', fontWeight: 700, marginBottom: '8px', textAlign: 'center' }}>Đăng ký tài khoản</h2>
        <p style={{ fontSize: '13px', color: 'var(--text-secondary)', textAlign: 'center', marginBottom: '24px' }}>
          Đăng ký tài khoản thành viên để đặt vé và trải nghiệm dịch vụ.
        </p>

        {error && (
          <div style={{ backgroundColor: 'rgba(239,68,68,0.1)', border: '1px solid rgba(239,68,68,0.2)', color: 'var(--danger)', padding: '10px 14px', borderRadius: 'var(--radius-sm)', fontSize: '13px', marginBottom: '16px' }}>
            {error}
          </div>
        )}

        {success && (
          <div style={{ backgroundColor: 'rgba(16,185,129,0.1)', border: '1px solid rgba(16,185,129,0.2)', color: 'var(--success)', padding: '10px 14px', borderRadius: 'var(--radius-sm)', fontSize: '13px', marginBottom: '16px' }}>
            {success}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Họ và Tên</label>
            <input type="text" className="form-control" value={fullname} onChange={(e) => setFullname(e.target.value)} required placeholder="Nguyễn Văn A" />
          </div>
          <div className="form-group">
            <label className="form-label">Địa chỉ Email</label>
            <input type="email" className="form-control" value={email} onChange={(e) => setEmail(e.target.value)} required placeholder="customer@gmail.com" />
          </div>
          <div className="form-group">
            <label className="form-label">Số điện thoại</label>
            <input type="text" className="form-control" value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="0912345678" />
          </div>
          <div className="form-group">
            <label className="form-label">Mật khẩu</label>
            <input type="password" className="form-control" value={password} onChange={(e) => setPassword(e.target.value)} required placeholder="Tối thiểu 6 ký tự" />
          </div>
          <div className="form-group">
            <label className="form-label">Xác nhận mật khẩu</label>
            <input type="password" className="form-control" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required placeholder="Nhập lại mật khẩu" />
          </div>
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '10px' }} disabled={submitting}>
            {submitting ? 'Đang xử lý...' : 'Đăng ký'}
          </button>
        </form>

        <div style={{ marginTop: '20px', textAlign: 'center', fontSize: '13px', color: 'var(--text-secondary)' }}>
          Đã có tài khoản? <Link to="/login" style={{ color: 'var(--primary)', fontWeight: 600 }}>Đăng nhập ngay</Link>
        </div>
      </div>
    </div>
  );
};

export default Register;
