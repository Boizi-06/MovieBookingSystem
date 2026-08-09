import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import ConfirmModal from '../components/ConfirmModal';
import { Clock, ShieldAlert, CheckCircle2, QrCode, ArrowLeft, Building2, CreditCard, AlertCircle, XCircle, ExternalLink } from 'lucide-react';

const PaymentCheckout: React.FC = () => {
  const { bookingId } = useParams<{ bookingId: string }>();
  const navigate = useNavigate();

  const [paymentDetails, setPaymentDetails] = useState<any | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [paying, setPaying] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  // 5-minute countdown state (in seconds)
  const [timeLeft, setTimeLeft] = useState<number>(300); // 5 mins default = 300s
  const [isExpired, setIsExpired] = useState<boolean>(false);

  const fetchPaymentInfo = async () => {
    if (!bookingId) return;
    setLoading(true);
    setError('');
    try {
      const response = await api.get(`/api/v1/bookings/${bookingId}/payment`);
      if (response.data?.success) {
        const data = response.data.data;
        setPaymentDetails(data);
        if (data.remainingSeconds !== undefined) {
          const rem = Math.max(0, data.remainingSeconds);
          setTimeLeft(rem);
          if (rem <= 0) {
            setIsExpired(true);
          }
        }
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Không thể tải thông tin thanh toán.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPaymentInfo();
  }, [bookingId]);

  // Countdown Timer Logic
  useEffect(() => {
    if (timeLeft <= 0) {
      setIsExpired(true);
      return;
    }

    const timer = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          clearInterval(timer);
          setIsExpired(true);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [timeLeft]);

  const formatTimer = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const formatVND = (value: number) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
  };

  // Auto Poll Payment Status Every 2.5 seconds
  useEffect(() => {
    if (!bookingId || isExpired) return;

    const interval = setInterval(async () => {
      try {
        const res = await api.get(`/api/v1/bookings/${bookingId}/status`);
        if (res.data?.success && res.data.data === 'PAID') {
          clearInterval(interval);
          if (paymentDetails?.bookingCode) {
            navigate(`/booking/success/${paymentDetails.bookingCode}`);
          }
        }
      } catch (err) {
        console.error('Error polling booking status:', err);
      }
    }, 2500);

    return () => clearInterval(interval);
  }, [bookingId, paymentDetails, isExpired, navigate]);

  const [cancelling, setCancelling] = useState<boolean>(false);
  const [isConfirmModalOpen, setIsConfirmModalOpen] = useState<boolean>(false);

  const promptCancelBooking = () => {
    setIsConfirmModalOpen(true);
  };

  const executeCancelBooking = async () => {
    if (!bookingId) return;
    setCancelling(true);
    try {
      await api.post(`/api/v1/bookings/${bookingId}/cancel`);
      if (paymentDetails?.showtimeId) {
        navigate(`/booking/seats/${paymentDetails.showtimeId}`);
      } else {
        navigate(-1);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Không thể hủy giữ ghế.');
    } finally {
      setCancelling(false);
      setIsConfirmModalOpen(false);
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <div className="spinner"></div>
      </div>
    );
  }

  if (error && !paymentDetails) {
    return (
      <div className="animate-fade-in" style={{ maxWidth: '600px', margin: '40px auto', padding: '0 20px', textAlign: 'center' }}>
        <div className="glass-card" style={{ padding: '30px' }}>
          <ShieldAlert size={48} style={{ color: 'var(--danger)', marginBottom: '16px' }} />
          <h3 style={{ fontSize: '20px', fontWeight: 700, color: 'var(--danger)', marginBottom: '8px' }}>Không thể thanh toán</h3>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '24px' }}>{error}</p>
          <button onClick={() => navigate('/')} className="btn-primary" style={{ padding: '10px 20px', borderRadius: 'var(--radius-sm)' }}>
            Quay lại trang chủ
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="animate-fade-in" style={{ maxWidth: '900px', margin: '0 auto', padding: '20px', textAlign: 'left' }}>
      
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <button 
          onClick={() => navigate(-1)} 
          style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '6px', fontSize: '14px', fontWeight: 600 }}
        >
          <ArrowLeft size={18} /> Quay lại chọn ghế
        </button>

        {/* 5-Minute Timer Display */}
        <div style={{
          display: 'flex', alignItems: 'center', gap: '8px',
          padding: '8px 16px', borderRadius: '20px',
          backgroundColor: isExpired ? 'rgba(239, 68, 68, 0.15)' : 'rgba(234, 179, 8, 0.15)',
          border: isExpired ? '1px solid rgba(239, 68, 68, 0.4)' : '1px solid rgba(234, 179, 8, 0.4)',
          color: isExpired ? '#f87171' : '#facc15',
          fontWeight: 800, fontSize: '16px'
        }}>
          <Clock size={18} />
          {isExpired ? 'Đơn giữ ghế đã hết hạn!' : `Thời gian giữ ghế: ${formatTimer(timeLeft)}`}
        </div>
      </div>

      {/* Error Alert */}
      {error && (
        <div style={{ padding: '12px 16px', borderRadius: 'var(--radius-sm)', backgroundColor: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.3)', color: '#f87171', display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '20px' }}>
          <AlertCircle size={18} /> {error}
        </div>
      )}

      {/* Payment Main Container */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px', flexWrap: 'wrap' }}>
        
        {/* Left Side: VietQR QR Code */}
        <div className="glass-card" style={{ padding: '24px', textAlign: 'center', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <h3 style={{ fontSize: '18px', fontWeight: 800, marginBottom: '6px', color: 'var(--primary)', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <QrCode size={22} /> Thanh toán qua VietQR
          </h3>
          <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginBottom: '20px' }}>
            Quét mã QR bằng ứng dụng ngân hàng hoặc Momo để hoàn tất thanh toán.
          </p>

          <div style={{ padding: '16px', backgroundColor: '#ffffff', borderRadius: 'var(--radius-md)', display: 'inline-block', boxShadow: '0 4px 20px rgba(0,0,0,0.3)', marginBottom: '16px' }}>
            <img 
              src={paymentDetails?.qrCodeUrl} 
              alt="VietQR Payment Code" 
              style={{ width: '240px', height: '240px', objectFit: 'contain' }}
            />
          </div>

          <p style={{ fontSize: '12px', color: 'var(--text-muted)', margin: 0 }}>
            Mã giao dịch: <strong style={{ color: 'var(--primary)', fontFamily: 'monospace' }}>{paymentDetails?.bookingCode}</strong>
          </p>
        </div>

        {/* Right Side: Account Details & Mock Payment Action */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div className="glass-card" style={{ padding: '24px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', height: '100%' }}>
            
            <div>
              <h3 style={{ fontSize: '18px', fontWeight: 800, marginBottom: '16px', color: 'var(--text-main)', display: 'flex', alignItems: 'center', gap: '8px' }}>
                <CreditCard size={20} /> Thông tin Chuyển khoản
              </h3>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', fontSize: '14px', marginBottom: '24px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '8px' }}>
                  <span style={{ color: 'var(--text-secondary)' }}>Ngân hàng:</span>
                  <strong>{paymentDetails?.bankId} (MBBank)</strong>
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '8px' }}>
                  <span style={{ color: 'var(--text-secondary)' }}>Số tài khoản:</span>
                  <strong style={{ fontFamily: 'monospace', fontSize: '15px', color: 'var(--primary)' }}>{paymentDetails?.accountNo}</strong>
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '8px' }}>
                  <span style={{ color: 'var(--text-secondary)' }}>Tên chủ tài khoản:</span>
                  <strong>{paymentDetails?.accountName}</strong>
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '8px' }}>
                  <span style={{ color: 'var(--text-secondary)' }}>Nội dung chuyển khoản:</span>
                  <strong style={{ fontFamily: 'monospace', color: '#ec4899' }}>{paymentDetails?.transferMemo}</strong>
                </div>

                {paymentDetails?.comboItems && paymentDetails.comboItems.length > 0 && (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '6px', borderBottom: '1px solid var(--border-color)', paddingBottom: '8px' }}>
                    <span style={{ color: 'var(--text-secondary)' }}>Bỏng nước đã chọn:</span>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '4px', paddingLeft: '8px' }}>
                      {paymentDetails.comboItems.map((c: any, idx: number) => (
                        <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px' }}>
                          <span style={{ color: '#cbd5e1' }}>• {c.comboName} (x{c.quantity})</span>
                          <span style={{ color: '#f59e0b', fontWeight: 600 }}>{formatVND(c.price * c.quantity)}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-color)', paddingBottom: '8px' }}>
                  <span style={{ color: 'var(--text-secondary)' }}>Tổng thanh toán:</span>
                  <strong style={{ fontSize: '18px', color: '#22c55e' }}>{formatVND(paymentDetails?.amount || 0)}</strong>
                </div>
              </div>
            </div>

            {/* Auto Listening Card & Action Buttons */}
            <div>
              {paymentDetails?.checkoutUrl && (
                <a
                  href={paymentDetails.checkoutUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="btn-primary"
                  style={{
                    width: '100%',
                    padding: '14px',
                    borderRadius: 'var(--radius-sm)',
                    fontWeight: 800,
                    fontSize: '15px',
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    gap: '10px',
                    textDecoration: 'none',
                    marginBottom: '12px',
                    boxShadow: '0 4px 15px rgba(99, 102, 241, 0.4)'
                  }}
                >
                  <ExternalLink size={20} /> Thanh toán qua Cổng PayOS (Mở trang QR)
                </a>
              )}

              {!isExpired ? (
                <div style={{ padding: '16px', borderRadius: 'var(--radius-sm)', backgroundColor: 'rgba(34, 197, 94, 0.12)', border: '1px solid rgba(34, 197, 94, 0.3)', color: '#4ade80', textAlign: 'center', marginBottom: '12px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', fontWeight: 700, fontSize: '14px', marginBottom: '6px' }}>
                    <span className="spinner" style={{ width: '16px', height: '16px', borderWidth: '2px' }} />
                    Đang tự động kiểm tra trạng thái thanh toán...
                  </div>
                  <p style={{ fontSize: '12px', color: 'var(--text-secondary)', margin: 0, lineHeight: '1.4' }}>
                    Hệ thống sẽ tự động xác nhận và xuất vé điện tử ngay sau khi nhận được chuyển khoản thành công!
                  </p>
                </div>
              ) : (
                <div style={{ padding: '12px 16px', borderRadius: 'var(--radius-sm)', backgroundColor: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.3)', color: '#f87171', textAlign: 'center', marginBottom: '12px', fontSize: '13px', fontWeight: 600 }}>
                  Đơn giữ ghế này đã hết hạn 5 phút. Vui lòng hủy để chọn lại ghế mới.
                </div>
              )}

              <button
                onClick={promptCancelBooking}
                disabled={cancelling}
                className="btn-secondary"
                style={{ width: '100%', padding: '12px', borderRadius: 'var(--radius-sm)', fontWeight: 700, fontSize: '14px', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '8px', color: '#f87171', borderColor: 'rgba(239, 68, 68, 0.4)', cursor: 'pointer' }}
              >
                <XCircle size={18} />
                {cancelling ? 'Đang giải phóng ghế...' : 'Hủy đơn thanh toán & Chọn lại ghế'}
              </button>
            </div>

          </div>
        </div>

        {/* Cancel Confirmation Modal */}
        <ConfirmModal
          isOpen={isConfirmModalOpen}
          title="Xác nhận Hủy Đơn giữ ghế"
          message="Bạn có chắc muốn hủy đơn này để quay lại chọn ghế mới? Các ghế vừa giữ chỗ sẽ được giải phóng ngay lập tức cho người khác chọn."
          confirmText="Hủy đơn & Chọn lại ghế"
          cancelText="Bỏ qua"
          variant="warning"
          loading={cancelling}
          onConfirm={executeCancelBooking}
          onClose={() => setIsConfirmModalOpen(false)}
        />

      </div>

    </div>
  );
};

export default PaymentCheckout;
