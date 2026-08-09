import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';
import AdminLayout from '../../components/AdminLayout';
import ConfirmModal from '../../components/ConfirmModal';
import { Search, Users, AlertCircle, ShieldAlert, Lock, Unlock, Check } from 'lucide-react';

const ManageUsers: React.FC = () => {
  const [users, setUsers] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');

  // Pagination & Filters State
  const [keyword, setKeyword] = useState<string>('');
  const [filterRole, setFilterRole] = useState<string>('');
  const [filterStatus, setFilterStatus] = useState<string>('');
  const [currentPage, setCurrentPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [totalElements, setTotalElements] = useState<number>(0);
  const pageSize = 10;

  // Confirm Modal State
  const [confirmModal, setConfirmModal] = useState<{ isOpen: boolean; userItem: any | null }>({
    isOpen: false,
    userItem: null,
  });

  const fetchUsers = async (page = 0) => {
    setLoading(true);
    setError('');
    try {
      let url = `/api/v1/users?page=${page}&size=${pageSize}`;
      if (keyword.trim()) url += `&keyword=${encodeURIComponent(keyword.trim())}`;
      if (filterRole) url += `&role=${filterRole}`;
      if (filterStatus) url += `&status=${filterStatus}`;

      const response = await api.get(url);
      if (response.data?.success) {
        setUsers(response.data.data.content || []);
        setTotalPages(response.data.data.totalPages || 0);
        setTotalElements(response.data.data.totalElements || 0);
        setCurrentPage(page);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Không thể tải danh sách người dùng.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers(0);
  }, [filterRole, filterStatus]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchUsers(0);
  };

  const promptToggleStatus = (userItem: any) => {
    setConfirmModal({ isOpen: true, userItem });
  };

  const executeToggleStatus = async () => {
    if (!confirmModal.userItem) return;
    const userItem = confirmModal.userItem;
    const isCurrentlyActive = userItem.status === 'ACTIVE';
    const newStatus = isCurrentlyActive ? 'LOCKED' : 'ACTIVE';

    setError('');
    setSuccess('');
    try {
      const response = await api.put(`/api/v1/users/${userItem.id}/status`, { status: newStatus });
      if (response.data?.success) {
        setSuccess(`${isCurrentlyActive ? 'Khóa' : 'Mở khóa'} tài khoản "${userItem.fullname}" thành công!`);
        fetchUsers(currentPage);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Thao tác cập nhật trạng thái thất bại.');
    } finally {
      setConfirmModal({ isOpen: false, userItem: null });
    }
  };

  return (
    <AdminLayout>
      <div className="animate-fade-in" style={{ textAlign: 'left' }}>
        
        {/* Header */}
        <div style={{ marginBottom: '24px' }}>
          <h1 style={{ fontSize: '26px', fontWeight: 800, margin: '0' }}>Quản lý Người dùng</h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '14px', margin: '4px 0 0 0' }}>Tổng số tài khoản: {totalElements}</p>
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
        <div className="glass-card" style={{ padding: '20px', marginBottom: '24px', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px' }}>
          
          <form onSubmit={handleSearchSubmit} style={{ gridColumn: 'span 2', display: 'flex', gap: '10px' }}>
            <div style={{ position: 'relative', flexGrow: 1 }}>
              <input 
                type="text" 
                className="form-control" 
                placeholder="Tìm kiếm theo tên, email, SĐT..." 
                value={keyword}
                onChange={e => setKeyword(e.target.value)}
                style={{ paddingLeft: '38px', marginBottom: 0 }}
              />
              <Search size={16} style={{ position: 'absolute', left: '14px', top: '12px', color: 'var(--text-muted)' }} />
            </div>
            <button type="submit" className="btn btn-primary">Tìm kiếm</button>
          </form>

          <div className="form-group" style={{ marginBottom: 0 }}>
            <select className="form-control" value={filterRole} onChange={e => setFilterRole(e.target.value)}>
              <option value="">Tất cả vai trò</option>
              <option value="CUSTOMER">Khách hàng (CUSTOMER)</option>
              <option value="ADMIN">Quản trị viên (ADMIN)</option>
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: 0 }}>
            <select className="form-control" value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
              <option value="">Tất cả trạng thái</option>
              <option value="ACTIVE">Hoạt động (ACTIVE)</option>
              <option value="LOCKED">Đang bị khóa (LOCKED)</option>
              <option value="INACTIVE">Chưa kích hoạt (INACTIVE)</option>
            </select>
          </div>

        </div>

        {/* Users Table */}
        {loading ? (
          <div style={{ display: 'flex', justifyContent: 'center', padding: '60px 0' }}><div className="spinner"></div></div>
        ) : users.length === 0 ? (
          <div className="glass-card" style={{ padding: '60px', textAlign: 'center', color: 'var(--text-secondary)' }}>
            Không tìm thấy người dùng nào.
          </div>
        ) : (
          <div className="glass-card" style={{ padding: '10px', overflowX: 'auto', marginBottom: '24px' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid var(--border-color)', textAlign: 'left' }}>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Họ và Tên</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Địa chỉ Email</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Số điện thoại</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Vai trò</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Trạng thái</th>
                  <th style={{ padding: '12px 16px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700, textAlign: 'center' }}>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id} style={{ borderBottom: '1px solid var(--border-color)' }} className="table-row-hover">
                    <td style={{ padding: '12px 16px', fontWeight: 600, fontSize: '14px' }}>{u.fullname}</td>
                    <td style={{ padding: '12px 16px', fontSize: '13px' }}>{u.email}</td>
                    <td style={{ padding: '12px 16px', fontSize: '13px', color: 'var(--text-secondary)' }}>{u.phone || 'N/A'}</td>
                    <td style={{ padding: '12px 16px' }}>
                      <span className={`badge ${u.role === 'ADMIN' ? 'badge-danger' : 'badge-info'}`} style={{ fontSize: '10px' }}>
                        {u.role}
                      </span>
                    </td>
                    <td style={{ padding: '12px 16px' }}>
                      <span className={`badge ${
                        u.status === 'ACTIVE' ? 'badge-success' : 
                        u.status === 'LOCKED' ? 'badge-danger' : 'badge-secondary'
                      }`}>
                        {u.status === 'ACTIVE' ? 'Đang hoạt động' : 
                         u.status === 'LOCKED' ? 'Bị khóa' : 'Chưa kích hoạt'}
                      </span>
                    </td>
                    <td style={{ padding: '12px 16px', textAlign: 'center' }}>
                      {/* Cấm khóa tài khoản của chính mình hoặc các tài khoản admin khác để tránh tai nạn khóa hết admin */}
                      {u.role === 'ADMIN' ? (
                        <span style={{ fontSize: '12px', color: 'var(--text-muted)', fontStyle: 'italic' }}>Không được khóa</span>
                      ) : (
                        <button 
                          onClick={() => promptToggleStatus(u)} 
                          className="btn btn-secondary" 
                          style={{ 
                            padding: '6px 12px', 
                            fontSize: '12px', 
                            display: 'inline-flex', 
                            alignItems: 'center', 
                            gap: '6px',
                            borderColor: u.status === 'ACTIVE' ? 'var(--danger)' : 'var(--success)'
                          }}
                        >
                          {u.status === 'ACTIVE' ? (
                            <>
                              <Lock size={12} style={{ color: 'var(--danger)' }} />
                              <span style={{ color: 'var(--danger)', fontWeight: 600 }}>Khóa</span>
                            </>
                          ) : (
                            <>
                              <Unlock size={12} style={{ color: 'var(--success)' }} />
                              <span style={{ color: 'var(--success)', fontWeight: 600 }}>Mở khóa</span>
                            </>
                          )}
                        </button>
                      )}
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
            <button disabled={currentPage === 0} onClick={() => fetchUsers(currentPage - 1)} className="btn btn-secondary" style={{ padding: '6px 12px' }}>Trước</button>
            <span style={{ display: 'flex', alignItems: 'center', fontSize: '14px', color: 'var(--text-secondary)' }}>Trang {currentPage + 1} / {totalPages}</span>
            <button disabled={currentPage === totalPages - 1} onClick={() => fetchUsers(currentPage + 1)} className="btn btn-secondary" style={{ padding: '6px 12px' }}>Sau</button>
          </div>
        )}

        {/* Confirm Modal */}
        <ConfirmModal
          isOpen={confirmModal.isOpen}
          title={
            confirmModal.userItem?.status === 'ACTIVE'
              ? 'Xác nhận khóa Tài khoản'
              : 'Xác nhận mở khóa Tài khoản'
          }
          message={
            confirmModal.userItem
              ? `Bạn có chắc chắn muốn ${confirmModal.userItem.status === 'ACTIVE' ? 'khóa' : 'mở khóa'} tài khoản của "${confirmModal.userItem.fullname}" (${confirmModal.userItem.email})?`
              : ''
          }
          confirmText={confirmModal.userItem?.status === 'ACTIVE' ? 'Khóa tài khoản' : 'Mở khóa'}
          cancelText="Hủy bỏ"
          variant={confirmModal.userItem?.status === 'ACTIVE' ? 'danger' : 'info'}
          onConfirm={executeToggleStatus}
          onClose={() => setConfirmModal({ isOpen: false, userItem: null })}
        />

      </div>
    </AdminLayout>
  );
};

export default ManageUsers;
