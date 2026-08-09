import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Mail, Phone, MapPin, Clock, Send, CheckCircle2, ArrowLeft, Headphones, MessageSquare } from 'lucide-react';

const ContactSupport: React.FC = () => {
  const [fullname, setFullname] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [subject, setSubject] = useState<string>('');
  const [message, setMessage] = useState<string>('');
  const [submitted, setSubmitted] = useState<boolean>(false);
  const [submitting, setSubmitting] = useState<boolean>(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setTimeout(() => {
      setSubmitting(false);
      setSubmitted(true);
      setFullname('');
      setEmail('');
      setSubject('');
      setMessage('');
    }, 1000);
  };

  return (
    <div className="animate-fade-in" style={{ maxWidth: '1100px', margin: '40px auto', padding: '0 20px', textAlign: 'left' }}>
      
      {/* Back link */}
      <Link to="/" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', textDecoration: 'none', marginBottom: '24px', fontSize: '14px', fontWeight: 500 }}>
        <ArrowLeft size={16} /> Quay lại trang chủ
      </Link>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))', gap: '30px', alignItems: 'start' }}>
        
        {/* Left Column: Contact Cards */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          
          <div className="glass-card" style={{ padding: '30px', borderRadius: '20px' }}>
            <div style={{ display: 'inline-flex', alignItems: 'center', gap: '8px', padding: '6px 14px', borderRadius: '999px', backgroundColor: 'rgba(59, 130, 246, 0.15)', color: 'var(--info)', fontSize: '12px', fontWeight: 700, marginBottom: '16px' }}>
              <Headphones size={14} /> TỔNG ĐÀI HỖ TRỢ 24/7
            </div>

            <h1 style={{ fontSize: '26px', fontWeight: 800, margin: '0 0 10px 0', color: 'var(--text-primary)' }}>
              Liên Hệ & Hỗ Trợ Kỹ Thuật
            </h1>
            <p style={{ color: 'var(--text-secondary)', fontSize: '14px', margin: '0 0 24px 0', lineHeight: 1.6 }}>
              Đội ngũ tư vấn viên Movie Booking luôn sẵn sàng giải đáp thắc mắc về đặt vé, thanh toán và sự cố suất chiếu.
            </p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', fontSize: '14px' }}>
              <div style={{ display: 'flex', alignItems: 'flex-start', gap: '12px' }}>
                <div style={{ padding: '10px', borderRadius: '12px', backgroundColor: 'rgba(99, 102, 241, 0.1)', color: 'var(--primary)' }}>
                  <Phone size={20} />
                </div>
                <div>
                  <span style={{ fontSize: '12px', color: 'var(--text-muted)', display: 'block' }}>Hotline CSKH</span>
                  <strong style={{ fontSize: '16px', color: 'var(--text-primary)' }}>1900 6017 / 0988 123 456</strong>
                </div>
              </div>

              <div style={{ display: 'flex', alignItems: 'flex-start', gap: '12px' }}>
                <div style={{ padding: '10px', borderRadius: '12px', backgroundColor: 'rgba(168, 85, 247, 0.1)', color: 'var(--secondary)' }}>
                  <Mail size={20} />
                </div>
                <div>
                  <span style={{ fontSize: '12px', color: 'var(--text-muted)', display: 'block' }}>Email Hỗ trợ</span>
                  <strong style={{ fontSize: '15px', color: 'var(--text-primary)' }}>support@moviebooking.com</strong>
                </div>
              </div>

              <div style={{ display: 'flex', alignItems: 'flex-start', gap: '12px' }}>
                <div style={{ padding: '10px', borderRadius: '12px', backgroundColor: 'rgba(6, 182, 212, 0.1)', color: 'var(--accent)' }}>
                  <MapPin size={20} />
                </div>
                <div>
                  <span style={{ fontSize: '12px', color: 'var(--text-muted)', display: 'block' }}>Địa chỉ Trụ sở</span>
                  <strong style={{ fontSize: '14px', color: 'var(--text-primary)' }}>Tầng 12, Tòa nhà Vincom Center, 191 Bà Triệu, Q. Hai Bà Trưng, Hà Nội</strong>
                </div>
              </div>

              <div style={{ display: 'flex', alignItems: 'flex-start', gap: '12px' }}>
                <div style={{ padding: '10px', borderRadius: '12px', backgroundColor: 'rgba(16, 185, 129, 0.1)', color: 'var(--success)' }}>
                  <Clock size={20} />
                </div>
                <div>
                  <span style={{ fontSize: '12px', color: 'var(--text-muted)', display: 'block' }}>Thời gian làm việc</span>
                  <strong style={{ fontSize: '14px', color: 'var(--text-primary)' }}>08:00 - 22:00 (Từ Thứ Hai đến Chủ Nhật)</strong>
                </div>
              </div>
            </div>
          </div>

        </div>

        {/* Right Column: Contact Form */}
        <div className="glass-card" style={{ padding: '32px', borderRadius: '20px' }}>
          <h2 style={{ fontSize: '20px', fontWeight: 800, margin: '0 0 8px 0', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <MessageSquare size={20} style={{ color: 'var(--primary)' }} /> Gửi Yêu Cầu Hỗ Trợ
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '13px', marginBottom: '24px' }}>
            Vui lòng điền thông tin chi tiết, phản hồi của bạn sẽ được hỗ trợ trong vòng 15 phút.
          </p>

          {submitted ? (
            <div style={{ padding: '30px 20px', textAlign: 'center', backgroundColor: 'rgba(16, 185, 129, 0.1)', borderRadius: '16px', border: '1px solid rgba(16, 185, 129, 0.3)' }}>
              <CheckCircle2 size={48} style={{ color: 'var(--success)', marginBottom: '12px' }} />
              <h3 style={{ fontSize: '18px', fontWeight: 800, color: 'var(--success)', margin: '0 0 8px 0' }}>Đã Gửi Yêu Cầu Thành Công!</h3>
              <p style={{ fontSize: '14px', color: 'var(--text-secondary)', margin: '0 0 20px 0' }}>
                Cảm ơn bạn đã phản hồi. Nhân viên chăm sóc khách hàng sẽ liên hệ phản hồi qua Email/SĐT trong thời gian sớm nhất.
              </p>
              <button onClick={() => setSubmitted(false)} className="btn btn-secondary" style={{ fontSize: '13px', padding: '8px 16px' }}>
                Gửi thêm yêu cầu khác
              </button>
            </div>
          ) : (
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <div className="form-group" style={{ marginBottom: 0 }}>
                <label className="form-label">Họ và tên *</label>
                <input 
                  type="text" 
                  className="form-control" 
                  required 
                  value={fullname}
                  onChange={e => setFullname(e.target.value)}
                  placeholder="Nhập họ và tên của bạn"
                />
              </div>

              <div className="form-group" style={{ marginBottom: 0 }}>
                <label className="form-label">Địa chỉ Email *</label>
                <input 
                  type="email" 
                  className="form-control" 
                  required 
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  placeholder="customer@gmail.com"
                />
              </div>

              <div className="form-group" style={{ marginBottom: 0 }}>
                <label className="form-label">Chủ đề hỗ trợ *</label>
                <input 
                  type="text" 
                  className="form-control" 
                  required 
                  value={subject}
                  onChange={e => setSubject(e.target.value)}
                  placeholder="VD: Sự cố giữ chỗ / Chưa nhận được email vé..."
                />
              </div>

              <div className="form-group" style={{ marginBottom: 0 }}>
                <label className="form-label">Nội dung câu hỏi / Góp ý *</label>
                <textarea 
                  className="form-control" 
                  rows={4} 
                  required 
                  value={message}
                  onChange={e => setMessage(e.target.value)}
                  placeholder="Mô tả chi tiết nội dung cần hỗ trợ..."
                  style={{ resize: 'vertical' }}
                />
              </div>

              <button 
                type="submit" 
                className="btn btn-primary" 
                style={{ width: '100%', marginTop: '10px', height: '44px', fontWeight: 700 }}
                disabled={submitting}
              >
                {submitting ? 'Đang gửi yêu cầu...' : <><Send size={16} /> Gửi tin nhắn ngay</>}
              </button>
            </form>
          )}
        </div>

      </div>
    </div>
  );
};

export default ContactSupport;
