import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import AdminLayout from '../components/AdminLayout';
import { DollarSign, Ticket, Film, Calendar, AlertCircle } from 'lucide-react';
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid, BarChart, Bar } from 'recharts';

const AdminDashboard: React.FC = () => {
  const [revenueData, setRevenueData] = useState<any[]>([]);
  const [ticketData, setTicketData] = useState<any[]>([]);
  const [movieData, setMovieData] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  // Date filters
  const [startDate, setStartDate] = useState<string>('');
  const [endDate, setEndDate] = useState<string>('');
  const [groupBy, setGroupBy] = useState<string>('DAY'); // DAY, MONTH, YEAR

  const fetchStats = async () => {
    setLoading(true);
    setError('');
    try {
      let queryParams = `?groupBy=${groupBy}`;
      if (startDate) queryParams += `&startDate=${startDate}`;
      if (endDate) queryParams += `&endDate=${endDate}`;

      let movieQueryParams = '';
      if (startDate && endDate) {
        movieQueryParams = `?startDate=${startDate}&endDate=${endDate}`;
      }

      // Gọi đồng thời cả 3 API bằng Promise.allSettled để tránh 1 API lỗi làm đứng cả trang Dashboard
      const [revRes, ticketRes, movieRes] = await Promise.allSettled([
        api.get(`/api/v1/statistics/revenue${queryParams}`),
        api.get(`/api/v1/statistics/tickets${queryParams}`),
        api.get(`/api/v1/statistics/movies${movieQueryParams}`)
      ]);

      if (revRes.status === 'fulfilled' && revRes.value.data?.success) {
        setRevenueData(revRes.value.data.data || []);
      }
      if (ticketRes.status === 'fulfilled' && ticketRes.value.data?.success) {
        setTicketData(ticketRes.value.data.data || []);
      }
      if (movieRes.status === 'fulfilled' && movieRes.value.data?.success) {
        setMovieData(movieRes.value.data.data || []);
      }
    } catch (err: any) {
      console.error('Error fetching statistics:', err);
      setError(err.response?.data?.message || err.message || 'Không thể tải dữ liệu thống kê.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, [groupBy, startDate, endDate]);

  // Aggregate stats cards
  const totalRevenue = revenueData.reduce((acc, curr) => acc + (curr.revenue || 0), 0);
  const totalTickets = ticketData.reduce((acc, curr) => acc + (curr.ticketCount || 0), 0);
  const topMovie = movieData.length > 0 ? movieData[0] : null;

  const formatVND = (value: number) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
  };

  // Convert dates like "2026-08-08" to "08/08" for cleaner charts
  const cleanRevenueData = revenueData.map(d => ({
    label: d.label && d.label.length > 5 ? d.label.substring(5) : (d.label || ''),
    'Doanh thu': d.revenue || 0
  }));

  const cleanTicketData = ticketData.map(d => ({
    label: d.label && d.label.length > 5 ? d.label.substring(5) : (d.label || ''),
    'Số vé': d.ticketCount || 0
  }));

  return (
    <AdminLayout>
      <div className="animate-fade-in" style={{ textAlign: 'left' }}>
        
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
          <div>
            <h1 style={{ fontSize: '26px', fontWeight: 800, margin: '0 0 4px 0' }}>Bảng điều khiển</h1>
            <p style={{ color: 'var(--text-secondary)', fontSize: '14px', margin: 0 }}>Thống kê doanh số bán vé và hoạt động của hệ thống rạp.</p>
          </div>

          {/* Group By selector */}
          <div style={{ display: 'flex', backgroundColor: 'var(--bg-card)', padding: '4px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)' }}>
            <button 
              onClick={() => setGroupBy('DAY')}
              style={{
                border: 'none', padding: '6px 14px', borderRadius: 'var(--radius-sm)', fontSize: '12px', fontWeight: 600, cursor: 'pointer',
                backgroundColor: groupBy === 'DAY' ? 'var(--primary)' : 'transparent',
                color: groupBy === 'DAY' ? '#fff' : 'var(--text-secondary)'
              }}
            >
              Ngày
            </button>
            <button 
              onClick={() => setGroupBy('MONTH')}
              style={{
                border: 'none', padding: '6px 14px', borderRadius: 'var(--radius-sm)', fontSize: '12px', fontWeight: 600, cursor: 'pointer',
                backgroundColor: groupBy === 'MONTH' ? 'var(--primary)' : 'transparent',
                color: groupBy === 'MONTH' ? '#fff' : 'var(--text-secondary)'
              }}
            >
              Tháng
            </button>
          </div>
        </div>

        {/* Date Filter Bar */}
        <div className="glass-card" style={{ padding: '16px 24px', marginBottom: '24px', display: 'flex', alignItems: 'center', gap: '16px', flexWrap: 'wrap' }}>
          <span style={{ fontSize: '13px', fontWeight: 700, color: 'var(--text-secondary)' }}>Bộ lọc thời gian:</span>
          
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Từ ngày</span>
            <input type="date" className="form-control" style={{ width: '150px', height: '34px', padding: '4px 10px', fontSize: '13px', marginBottom: 0 }} value={startDate} onChange={e => setStartDate(e.target.value)} />
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>đến ngày</span>
            <input type="date" className="form-control" style={{ width: '150px', height: '34px', padding: '4px 10px', fontSize: '13px', marginBottom: 0 }} value={endDate} onChange={e => setEndDate(e.target.value)} />
          </div>

          {(startDate || endDate) && (
            <button 
              onClick={() => { setStartDate(''); setEndDate(''); }}
              className="btn btn-secondary" 
              style={{ padding: '6px 12px', fontSize: '12px', height: '34px' }}
            >
              Xóa bộ lọc
            </button>
          )}
        </div>

        {error && (
          <div className="glass-card" style={{ padding: '16px 20px', display: 'flex', alignItems: 'center', gap: '10px', borderColor: 'var(--danger)', marginBottom: '24px' }}>
            <AlertCircle style={{ color: 'var(--danger)' }} />
            <span style={{ color: 'var(--danger)', fontSize: '14px' }}>{error}</span>
          </div>
        )}

        {/* Aggregate Cards */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '20px', marginBottom: '30px' }}>
          
          {/* Card 1: Revenue */}
          <div className="glass-card" style={{ padding: '24px', display: 'flex', alignItems: 'center', gap: '20px' }}>
            <div style={{ backgroundColor: 'rgba(99,102,241,0.1)', padding: '16px', borderRadius: '50%', color: 'var(--primary)' }}>
              <DollarSign size={28} />
            </div>
            <div>
              <p style={{ margin: 0, fontSize: '13px', color: 'var(--text-secondary)', fontWeight: 600 }}>TỔNG DOANH THU</p>
              <h3 style={{ margin: '4px 0 0 0', fontSize: '22px', fontWeight: 800 }}>{loading ? '---' : formatVND(totalRevenue)}</h3>
            </div>
          </div>

          {/* Card 2: Tickets */}
          <div className="glass-card" style={{ padding: '24px', display: 'flex', alignItems: 'center', gap: '20px' }}>
            <div style={{ backgroundColor: 'rgba(16,185,129,0.1)', padding: '16px', borderRadius: '50%', color: 'var(--success)' }}>
              <Ticket size={28} />
            </div>
            <div>
              <p style={{ margin: 0, fontSize: '13px', color: 'var(--text-secondary)', fontWeight: 600 }}>VÉ ĐÃ BÁN</p>
              <h3 style={{ margin: '4px 0 0 0', fontSize: '22px', fontWeight: 800 }}>{loading ? '---' : totalTickets.toLocaleString('vi-VN') + ' vé'}</h3>
            </div>
          </div>

          {/* Card 3: Top Movie */}
          <div className="glass-card" style={{ padding: '24px', display: 'flex', alignItems: 'center', gap: '20px' }}>
            <div style={{ backgroundColor: 'rgba(168,85,247,0.1)', padding: '16px', borderRadius: '50%', color: 'var(--secondary)' }}>
              <Film size={28} />
            </div>
            <div style={{ minWidth: 0, flexGrow: 1 }}>
              <p style={{ margin: 0, fontSize: '13px', color: 'var(--text-secondary)', fontWeight: 600 }}>PHIM ĂN KHÁCH NHẤT</p>
              <h3 style={{ margin: '4px 0 0 0', fontSize: '16px', fontWeight: 800, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                {loading ? '---' : topMovie ? topMovie.movieTitle : 'N/A'}
              </h3>
            </div>
          </div>
        </div>

        {/* Charts Section */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(450px, 1fr))', gap: '24px', marginBottom: '30px' }}>
          
          {/* Chart 1: Revenue Line Area Chart */}
          <div className="glass-card" style={{ padding: '24px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 700, margin: '0 0 20px 0' }}>Biểu đồ Doanh thu ({groupBy === 'DAY' ? 'Theo Ngày' : 'Theo Tháng'})</h3>
            {loading ? (
              <div style={{ height: '300px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><div className="spinner"></div></div>
            ) : cleanRevenueData.length === 0 ? (
              <div style={{ height: '300px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-muted)' }}>Chưa có dữ liệu thống kê doanh thu.</div>
            ) : (
              <div style={{ width: '100%', height: '300px' }}>
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={cleanRevenueData}>
                    <defs>
                      <linearGradient id="colorRev" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="var(--primary)" stopOpacity={0.4}/>
                        <stop offset="95%" stopColor="var(--primary)" stopOpacity={0.0}/>
                      </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" stroke="var(--border-color)" />
                    <XAxis dataKey="label" stroke="var(--text-muted)" fontSize={11} />
                    <YAxis stroke="var(--text-muted)" fontSize={11} />
                    <Tooltip contentStyle={{ backgroundColor: 'var(--bg-card)', borderColor: 'var(--border-color)', color: 'var(--text-primary)' }} />
                    <Area type="monotone" dataKey="Doanh thu" stroke="var(--primary)" fillOpacity={1} fill="url(#colorRev)" strokeWidth={2} />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            )}
          </div>

          {/* Chart 2: Tickets Bar Chart */}
          <div className="glass-card" style={{ padding: '24px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 700, margin: '0 0 20px 0' }}>Biểu đồ Số lượng Vé bán ({groupBy === 'DAY' ? 'Theo Ngày' : 'Theo Tháng'})</h3>
            {loading ? (
              <div style={{ height: '300px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><div className="spinner"></div></div>
            ) : cleanTicketData.length === 0 ? (
              <div style={{ height: '300px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-muted)' }}>Chưa có dữ liệu thống kê vé.</div>
            ) : (
              <div style={{ width: '100%', height: '300px' }}>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={cleanTicketData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="var(--border-color)" />
                    <XAxis dataKey="label" stroke="var(--text-muted)" fontSize={11} />
                    <YAxis stroke="var(--text-muted)" fontSize={11} />
                    <Tooltip contentStyle={{ backgroundColor: 'var(--bg-card)', borderColor: 'var(--border-color)', color: 'var(--text-primary)' }} />
                    <Bar dataKey="Số vé" fill="var(--success)" radius={[4, 4, 0, 0]} barSize={25} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            )}
          </div>
        </div>

        {/* Movies Rankings Table */}
        <div className="glass-card" style={{ padding: '24px', marginBottom: '40px' }}>
          <h3 style={{ fontSize: '16px', fontWeight: 700, margin: '0 0 20px 0' }}>Hiệu suất doanh thu của các Phim</h3>
          {loading ? (
            <div style={{ display: 'flex', justifyContent: 'center', padding: '30px 0' }}><div className="spinner"></div></div>
          ) : movieData.length === 0 ? (
            <div style={{ padding: '30px', textAlign: 'center', color: 'var(--text-muted)' }}>Không có dữ liệu xếp hạng phim.</div>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
                    <th style={{ padding: '12px 8px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Xếp hạng</th>
                    <th style={{ padding: '12px 8px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700 }}>Tên phim</th>
                    <th style={{ padding: '12px 8px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700, textAlign: 'right' }}>Số vé đã bán</th>
                    <th style={{ padding: '12px 8px', color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 700, textAlign: 'right' }}>Doanh thu thu về</th>
                  </tr>
                </thead>
                <tbody>
                  {movieData.map((m, index) => (
                    <tr key={index} style={{ borderBottom: '1px solid var(--border-color)' }} className="table-row-hover">
                      <td style={{ padding: '12px 8px', fontSize: '14px', fontWeight: 700, color: index === 0 ? 'gold' : index === 1 ? 'silver' : index === 2 ? 'bronze' : 'inherit' }}>
                        #{index + 1}
                      </td>
                      <td style={{ padding: '12px 8px', fontSize: '14px', fontWeight: 600 }}>{m.movieTitle}</td>
                      <td style={{ padding: '12px 8px', fontSize: '14px', textAlign: 'right' }}>{(m.ticketsSold || 0).toLocaleString('vi-VN')} vé</td>
                      <td style={{ padding: '12px 8px', fontSize: '14px', fontWeight: 700, textAlign: 'right', color: 'var(--primary)' }}>{formatVND(m.revenue || 0)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

      </div>
    </AdminLayout>
  );
};

export default AdminDashboard;
