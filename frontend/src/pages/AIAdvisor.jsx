import { useState } from 'react';
import { submitReport, getReports } from '../services/api';
import { useEffect } from 'react';

const districts = ['Colombo', 'Gampaha', 'Kandy', 'Galle', 'Matara', 'Kurunegala', 'Anuradhapura', 'Jaffna'];

const Report = () => {
  const [form, setForm]         = useState({ issueType: 'WATER', district: 'Colombo', area: '', description: '' });
  const [submitted, setSubmitted] = useState(false);
  const [reports, setReports]   = useState([]);

  useEffect(() => {
    getReports().then(r => setReports(r.data)).catch(console.error);
  }, []);

  const handleSubmit = async () => {
    try {
      await submitReport(form);
      setSubmitted(true);
      const r = await getReports();
      setReports(r.data);
      setTimeout(() => setSubmitted(false), 3000);
      setForm({ issueType: 'WATER', district: 'Colombo', area: '', description: '' });
    } catch (err) {
      console.error('Submit error:', err);
    }
  };

  const inputStyle = {
    width: '100%', padding: '8px 10px', fontSize: '13px',
    background: '#2a2a4a', border: '1px solid #3a3a5a',
    borderRadius: '8px', color: '#fff', fontFamily: 'inherit',
    boxSizing: 'border-box',
  };

  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <h2 style={{ color: '#fff', marginBottom: '20px' }}>📍 Submit Outage Report</h2>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
        {/* Form */}
        <div style={{ background: '#1a1a2e', border: '1px solid #2a2a4a', borderRadius: '12px', padding: '24px' }}>
          <div style={{ marginBottom: '12px' }}>
            <label style={{ fontSize: '12px', color: '#888', display: 'block', marginBottom: '4px' }}>Issue Type</label>
            <select style={inputStyle} value={form.issueType} onChange={e => setForm({...form, issueType: e.target.value})}>
              <option value="WATER">💧 Water Shortage</option>
              <option value="POWER">⚡ Power Outage</option>
            </select>
          </div>

          <div style={{ marginBottom: '12px' }}>
            <label style={{ fontSize: '12px', color: '#888', display: 'block', marginBottom: '4px' }}>District</label>
            <select style={inputStyle} value={form.district} onChange={e => setForm({...form, district: e.target.value})}>
              {districts.map(d => <option key={d}>{d}</option>)}
            </select>
          </div>

          <div style={{ marginBottom: '12px' }}>
            <label style={{ fontSize: '12px', color: '#888', display: 'block', marginBottom: '4px' }}>Area (optional)</label>
            <input style={inputStyle} placeholder="e.g. Maharagama, Nugegoda" value={form.area}
              onChange={e => setForm({...form, area: e.target.value})} />
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label style={{ fontSize: '12px', color: '#888', display: 'block', marginBottom: '4px' }}>Description</label>
            <textarea style={{...inputStyle, minHeight: '80px', resize: 'vertical'}}
              placeholder="Brief description..." value={form.description}
              onChange={e => setForm({...form, description: e.target.value})} />
          </div>

          <button onClick={handleSubmit} style={{
            background: '#1D9E75', color: '#fff', border: 'none',
            padding: '10px 20px', borderRadius: '8px', fontSize: '13px',
            cursor: 'pointer', fontWeight: '500', width: '100%',
          }}>
            📤 Submit Report
          </button>

          {submitted && (
            <div style={{ background: '#1a3a2a', border: '1px solid #1D9E75', borderRadius: '8px', padding: '10px', marginTop: '12px', fontSize: '13px', color: '#1D9E75' }}>
              ✅ Report submitted! Thank you for helping your community.
            </div>
          )}
        </div>

        {/* Recent Reports */}
        <div style={{ background: '#1a1a2e', border: '1px solid #2a2a4a', borderRadius: '12px', padding: '24px' }}>
          <div style={{ fontSize: '14px', color: '#888', marginBottom: '12px' }}>Recent Reports</div>
          {reports.length === 0 ? (
            <div style={{ color: '#666', fontSize: '13px' }}>No reports yet.</div>
          ) : (
            reports.slice(0, 8).map((r, i) => (
              <div key={i} style={{
                display: 'flex', justifyContent: 'space-between',
                padding: '8px 0', borderBottom: '1px solid #2a2a4a', fontSize: '13px',
              }}>
                <div style={{ color: '#ccc' }}>
                  {r.issueType === 'WATER' ? '💧' : '⚡'} {r.district}
                  {r.area ? ` — ${r.area}` : ''}
                </div>
                <div style={{ color: r.issueType === 'WATER' ? '#378ADD' : '#EF9F27', fontSize: '11px' }}>
                  {r.issueType}
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default Report;