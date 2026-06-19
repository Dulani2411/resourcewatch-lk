import { useState, useEffect } from 'react';
import { getWaterRisk } from '../services/api';
import RiskBadge from '../components/RiskBadge';

const Water = () => {
  const [data, setData] = useState(null);

  useEffect(() => {
    getWaterRisk().then(r => setData(r.data)).catch(console.error);
  }, []);

  const factors = data?.factors || {};

  const bars = [
    { label: 'Dry Days Risk',    value: factors.dryDays > 10 ? 85 : 30,  color: '#E24B4A' },
    { label: 'Temperature',      value: Math.min(100, (factors.temperature || 0) * 2), color: '#EF9F27' },
    { label: 'Humidity',         value: factors.humidity || 0,            color: '#378ADD' },
    { label: 'Rainfall',         value: Math.min(100, (factors.rainfall || 0) * 5), color: '#1D9E75' },
    { label: 'Community Reports',value: Math.min(100, (factors.communityReports || 0) * 3), color: '#7F77DD' },
  ];

  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <h2 style={{ color: '#fff', marginBottom: '20px' }}>💧 Water Risk Index</h2>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
        {/* Score */}
        <div style={{ background: '#1a1a2e', border: '1px solid #2a2a4a', borderRadius: '12px', padding: '24px', textAlign: 'center' }}>
          <div style={{ fontSize: '64px', fontWeight: '700', color: '#fff' }}>
            {data?.score ?? 0}
          </div>
          <div style={{ fontSize: '14px', color: '#888', marginBottom: '12px' }}>out of 100</div>
          <RiskBadge level={data?.level ?? 'LOW'} />
          <div style={{ fontSize: '12px', color: '#666', marginTop: '12px' }}>
            Calculated from weather + community data
          </div>
        </div>

        {/* Factors */}
        <div style={{ background: '#1a1a2e', border: '1px solid #2a2a4a', borderRadius: '12px', padding: '24px' }}>
          <div style={{ fontSize: '14px', color: '#888', marginBottom: '16px' }}>Risk Factors</div>
          {bars.map((bar, i) => (
            <div key={i} style={{ marginBottom: '12px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', color: '#888', marginBottom: '4px' }}>
                <span>{bar.label}</span>
                <span>{bar.value}%</span>
              </div>
              <div style={{ background: '#2a2a4a', borderRadius: '4px', height: '8px' }}>
                <div style={{ width: `${bar.value}%`, background: bar.color, height: '100%', borderRadius: '4px', transition: 'width 0.5s' }} />
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* How it works */}
      <div style={{ background: '#1a1a2e', border: '1px solid #2a2a4a', borderRadius: '12px', padding: '16px', marginTop: '16px' }}>
        <div style={{ fontSize: '14px', color: '#888', marginBottom: '8px' }}>How the index works</div>
        <div style={{ fontSize: '12px', color: '#666', lineHeight: '2' }}>
          <span style={{ background: '#2a2a4a', padding: '2px 8px', borderRadius: '12px', margin: '2px' }}>Rainfall &lt; 10mm → +30</span>
          <span style={{ background: '#2a2a4a', padding: '2px 8px', borderRadius: '12px', margin: '2px' }}>Temp &gt; 34°C → +20</span>
          <span style={{ background: '#2a2a4a', padding: '2px 8px', borderRadius: '12px', margin: '2px' }}>Dry days &gt; 10 → +25</span>
          <span style={{ background: '#2a2a4a', padding: '2px 8px', borderRadius: '12px', margin: '2px' }}>Reports &gt; 20 → +15</span>
          <span style={{ background: '#2a2a4a', padding: '2px 8px', borderRadius: '12px', margin: '2px' }}>Low humidity → +10</span>
        </div>
      </div>
    </div>
  );
};

export default Water;