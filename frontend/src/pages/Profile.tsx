import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { User, Phone, Mail, ShieldAlert, KeyRound, AlertCircle, CheckCircle2 } from 'lucide-react';

const Profile: React.FC = () => {
  const { user } = useAuth();
  
  // Profile state
  const [profile, setProfile] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');
  const [updateSuccess, setUpdateSuccess] = useState<string>('');
  
  // Form Update Profile state
  const [fullname, setFullname] = useState<string>('');
  const [phone, setPhone] = useState<string>('');
  const [updatingProfile, setUpdatingProfile] = useState<boolean>(false);

  // Form Change Password state
  const [oldPassword, setOldPassword] = useState<string>('');
  const [newPassword, setNewPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');
  const [changePasswordError, setChangePasswordError] = useState<string>('');
  const [changePasswordSuccess, setChangePasswordSuccess] = useState<string>('');
  const [changingPassword, setChangingPassword] = useState<boolean>(false);

  // Booking History state
  const [bookingHistory, setBookingHistory] = useState<any[]>([]);
  const [loadingHistory, setLoadingHistory] = useState<boolean>(true);

  const fetchProfile = async () => {
    try {
      const response = await api.get('/api/v1/users/profile');
      if (response.data && response.data.success) {
        const data = response.data.data;
        setProfile(data);
        setFullname(data.fullname);
        setPhone(data.phone || '');
      }
    } catch (err: any) {
      setError(err.message || 'Không thể tải thông tin hồ sơ.');
    } finally {
      setLoading(false);
    }
  };

  const fetchHistory = async () => {
    setLoadingHistory(true);
    try {
      const response = await api.get('/api/v1/bookings/my-history');
      if (response.data?.success) {
        setBookingHistory(response.data.data || []);
      }
    } catch (err) {
      console.error('Error fetching booking history:', err);
    } finally {
      setLoadingHistory(false);
    }
  };

  useEffect(() => {
    fetchProfile();
    fetchHistory();
  }, []);

  const formatVND = (value: number) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
  };

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setUpdateSuccess('');
    setError('');
    setUpdatingProfile(true);

    try {
      const response = await api.put('/api/v1/users/profile', { fullname, phone });
      if (response.data && response.data.success) {
        setProfile(response.data.data);
        setUpdateSuccess('Cập nhật hồ sơ thành công!');
        // Cập nhật thông tin trong localStorage để đồng bộ Navbar
        const storedUser = localStorage.getItem('user_info');
        if (storedUser) {
          const parsed = JSON.parse(storedUser);
          parsed.fullname = fullname;
          parsed.phone = phone;
          localStorage.setItem('user_info', JSON.stringify(parsed));
        }
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Cập nhật hồ sơ thất bại.');
    } finally {
      setUpdatingProfile(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setChangePasswordError('');
    setChangePasswordSuccess('');

    if (newPassword !== confirmPassword) {
      setChangePasswordError('Mật khẩu mới và mật khẩu xác nhận không khớp.');
      return;
    }

    setChangingPassword(true);
    try {
      const response = await api.post('/api/v1/auth/change-password', {
        currentPassword: oldPassword,
        newPassword,
        confirmNewPassword: confirmPassword
      });
      if (response.data && response.data.success) {
        setChangePasswordSuccess('Thay đổi mật khẩu thành công!');
        setOldPassword('');
        setNewPassword('');
        setConfirmPassword('');
      }
    } catch (err: any) {
      setChangePasswordError(err.response?.data?.message || err.message || 'Thay đổi mật khẩu thất bại.');
    } finally {
      setChangingPassword(false);
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: '60px 0' }}>
        <div className="spinner"></div>
      </div>
    );
  }

  return (
    <div className="animate-fade-in" style={{ maxWidth: '1000px', margin: '0 auto', padding: '0 20px', textAlign: 'left' }}>
      <h2 style={{ fontSize: '24px', fontWeight: 800, marginBottom: '24px' }}>Cài đặt hồ sơ cá nhân</h2>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(380px, 1fr))', gap: '30px' }}>
        {/* Left Column: Profile Info & Update */}
        <div className="glass-card" style={{ padding: '30px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '20px', marginBottom: '24px' }}>
            <div style={{ 
              width: '70px', 
              height: '70px', 
              borderRadius: '50%', 
              background: 'linear-gradient(135deg, var(--primary), var(--secondary))',
              color: '#fff',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: '24px',
              fontWeight: 700
            }}>
              {profile?.fullname.charAt(0).toUpperCase()}
            </div>
            <div>
              <h3 style={{ fontSize: '18px', fontWeight: 700, margin: '0 0 4px 0' }}>{profile?.fullname}</h3>
              <div style={{ display: 'flex', gap: '8px' }}>
                <span className="badge badge-success" style={{ fontSize: '10px' }}>{profile?.role}</span>
                <span className="badge badge-info" style={{ fontSize: '10px' }}>{profile?.status}</span>
              </div>
            </div>
          </div>

          <h4 style={{ fontSize: '15px', fontWeight: 600, borderBottom: '1px solid var(--border-color)', paddingBottom: '8px', marginBottom: '16px' }}>
            Cập nhật thông tin cơ bản
          </h4>

          {updateSuccess && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', backgroundColor: 'rgba(16,185,129,0.1)', color: 'var(--success)', padding: '10px 14px', borderRadius: 'var(--radius-sm)', fontSize: '13px', marginBottom: '16px' }}>
              <CheckCircle2 size={16} />
              <span>{updateSuccess}</span>
            </div>
          )}

          {error && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', backgroundColor: 'rgba(239,68,68,0.1)', color: 'var(--danger)', padding: '10px 14px', borderRadius: 'var(--radius-sm)', fontSize: '13px', marginBottom: '16px' }}>
              <AlertCircle size={16} />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleUpdateProfile}>
            <div className="form-group">
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Mail size={14} /> Địa chỉ Email (Đăng nhập)
              </label>
              <input type="text" className="form-control" value={profile?.email} disabled style={{ opacity: 0.6, cursor: 'not-allowed' }} />
            </div>
            <div className="form-group">
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <User size={14} /> Họ và Tên
              </label>
              <input type="text" className="form-control" value={fullname} onChange={(e) => setFullname(e.target.value)} required />
            </div>
            <div className="form-group">
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Phone size={14} /> Số điện thoại
              </label>
              <input type="text" className="form-control" value={phone} onChange={(e) => setPhone(e.target.value)} />
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%' }} disabled={updatingProfile}>
              {updatingProfile ? 'Đang lưu...' : 'Lưu thay đổi'}
            </button>
          </form>
        </div>

        {/* Right Column: Change Password */}
        <div className="glass-card" style={{ padding: '30px' }}>
          <h3 style={{ fontSize: '18px', fontWeight: 700, margin: '0 0 16px 0', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <KeyRound size={20} style={{ color: 'var(--primary)' }} /> Thay đổi mật khẩu
          </h3>

          {changePasswordSuccess && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', backgroundColor: 'rgba(16,185,129,0.1)', color: 'var(--success)', padding: '10px 14px', borderRadius: 'var(--radius-sm)', fontSize: '13px', marginBottom: '16px' }}>
              <CheckCircle2 size={16} />
              <span>{changePasswordSuccess}</span>
            </div>
          )}

          {changePasswordError && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', backgroundColor: 'rgba(239,68,68,0.1)', color: 'var(--danger)', padding: '10px 14px', borderRadius: 'var(--radius-sm)', fontSize: '13px', marginBottom: '16px' }}>
              <AlertCircle size={16} />
              <span>{changePasswordError}</span>
            </div>
          )}

          <form onSubmit={handleChangePassword}>
            <div className="form-group">
              <label className="form-label">Mật khẩu hiện tại</label>
              <input 
                type="password" 
                className="form-control" 
                value={oldPassword} 
                onChange={(e) => setOldPassword(e.target.value)} 
                required 
              />
            </div>
            <div className="form-group">
              <label className="form-label">Mật khẩu mới</label>
              <input 
                type="password" 
                className="form-control" 
                value={newPassword} 
                onChange={(e) => setNewPassword(e.target.value)} 
                required 
                placeholder="Tối thiểu 6 ký tự"
              />
            </div>
            <div className="form-group">
              <label className="form-label">Xác nhận mật khẩu mới</label>
              <input 
                type="password" 
                className="form-control" 
                value={confirmPassword} 
                onChange={(e) => setConfirmPassword(e.target.value)} 
                required 
              />
            </div>
            <button type="submit" className="btn btn-secondary" style={{ width: '100%', marginTop: '10px' }} disabled={changingPassword}>
              {changingPassword ? 'Đang thực hiện...' : 'Cập nhật mật khẩu'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Profile;
