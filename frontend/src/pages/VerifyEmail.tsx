import React, { useState, useEffect } from 'react';
import { Link, useSearchParams, useLocation } from 'react-router-dom';
import { api } from '../services/api';
import { CheckCircle2, AlertCircle, ShieldAlert } from 'lucide-react';

const VerifyEmail: React.FC = () => {
  const [searchParams] = useSearchParams();
  const location = useLocation();
  const email = location.state?.email || '';
  
  const [token, setToken] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [success, setSuccess] = useState<boolean>(false);
  const [error, setError] = useState<string>('');
  const [autoVerified, setAutoVerified] = useState<boolean>(false);

  useEffect(() => {
    const tokenParam = searchParams.get('token');
    if (tokenParam) {
      setToken(tokenParam);
      handleVerify(tokenParam);
      setAutoVerified(true);
    }
  }, [searchParams]);

  const handleVerify = async (verifyToken: string) => {
    if (!verifyToken.trim()) return;
    setLoading(true);
    setError('');
    setSuccess(false);
    try {
      const response = await api.get(`/api/v1/auth/verify-email?token=${verifyToken.trim()}`);
      if (response.data && response.data.success) {
        setSuccess(true);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Xác thực kích hoạt thất bại.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="animate-fade-in" style={{ maxWidth: '450px', margin: '50px auto', padding: '0 20px' }}>
      <div className="glass-card" style={{ padding: '40px', textAlign: 'center' }}>
        <h2 style={{ fontSize: '22px', fontWeight: 700, marginBottom: '24px' }}>Xác thực Kích hoạt Tài khoản</h2>

        {loading ? (
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '16px', padding: '20px 0' }}>
            <div className="spinner"></div>
            <p style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>Đang kết nối hệ thống kích hoạt tài khoản...</p>
          </div>
        ) : success ? (
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '16px', padding: '10px 0' }}>
            <CheckCircle2 size={56} style={{ color: 'var(--success)' }} />
            <h3 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--success)', margin: 0 }}>Kích hoạt thành công!</h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '14px', margin: '0 0 10px 0' }}>
              Tài khoản của bạn đã được kích hoạt. Bây giờ bạn đã có thể đăng nhập vào hệ thống để mua vé xem phim.
            </p>
            <Link to="/login" className="btn btn-primary" style={{ padding: '10px 24px' }}>
              Đăng nhập ngay
            </Link>
          </div>
        ) : error ? (
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '16px', padding: '10px 0' }}>
            <AlertCircle size={56} style={{ color: 'var(--danger)' }} />
            <h3 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--danger)', margin: 0 }}>Xác thực không thành công</h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '14px', margin: '0 0 10px 0' }}>
              {error}
            </p>
            {!autoVerified && (
              <button onClick={() => handleVerify(token)} className="btn btn-primary" style={{ padding: '8px 16px' }}>
                Thử lại
              </button>
            )}
          </div>
        ) : (
          <div style={{ textAlign: 'left' }}>
            {email ? (
              <p style={{ color: 'var(--text-secondary)', fontSize: '14px', marginBottom: '20px', textAlign: 'center' }}>
                Hệ thống đã gửi liên kết và mã kích hoạt tới hòm thư <strong>{email}</strong>. Vui lòng kiểm tra email và nhập mã xác thực tài khoản để hoàn tất:
              </p>
            ) : (
              <p style={{ color: 'var(--text-secondary)', fontSize: '14px', marginBottom: '20px', textAlign: 'center' }}>
                Nhập mã xác thực tài khoản được gửi trong email đăng ký của bạn.
              </p>
            )}
            <div className="form-group">
              <label className="form-label">Mã xác thực tài khoản</label>
              <input 
                type="text" 
                className="form-control" 
                value={token} 
                onChange={(e) => setToken(e.target.value)} 
                placeholder="Nhập mã xác thực từ email..."
              />
            </div>
            <button 
              onClick={() => handleVerify(token)} 
              className="btn btn-primary" 
              style={{ width: '100%', marginTop: '10px' }}
              disabled={!token.trim()}
            >
              Kích hoạt tài khoản
            </button>
          </div>
        )}

        <div style={{ marginTop: '30px', borderTop: '1px solid var(--border-color)', paddingTop: '20px', fontSize: '13px' }}>
          <Link to="/login" style={{ color: 'var(--text-muted)', textDecoration: 'none' }}>Quay lại Đăng nhập</Link>
        </div>
      </div>
    </div>
  );
};

export default VerifyEmail;
