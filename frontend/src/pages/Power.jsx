import { useState, useEffect } from 'react';
import { getPowerStress } from '../services/api';
import RiskBadge from '../components/RiskBadge';

const Power = () => {
  const [data, setData] = useState(null);

  useEffect(() => {
    getPowerStress().then(r => setData(r.data)).catch(console.error);
  }, []);

  const peakHours = [
    { label: '06:00–09:00', level: 65, color: '#EF9F27' },
    { label: '12:00–14:00', level: 30, color: '#1D9E75' },
    { label: '18:00–21:00', level: 90, color: '#E24B4A' },
    { label: '22:00–00:00', level: 20, color: '#1D9E75' },
  ];

  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <h2 style={{ color: '#fff', marginBottom: '20px' }}>⚡ Power Grid Stress</h2>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px', marginBottom: '16px' }}>
        {/* Stress Level */}
        <div style={{ background: '#1a1a2e', border: '1px solid #2a2a4a', borderRadius: '12px', padding: '24px', textAlign: 'center' }}>
          <div style={{ fontSize: '14px', color: '#888', marginBottom: '12px' }}>Grid Stress Level</div>
          <RiskBadge level={data?.level ?? 'LOW'} />
          <div style={{ fontSize: '12px', color: '#666', marginTop: '8px' }}>
            {data?.isPeakHourNow ? '⚠️ Peak hour active!' : '✅ Off-peak hours'}
          </div>
        </div>

        {/* Peak Hours */}
        <div style={{ background: '#1a1a2e', border: '1px solid #2a2a4a', borderRadius: '12px', padding: '24px' }}>
          <div style={{ fontSize: '14px', color: '#888', marginBottom: '12px' }}>Demand by Hour</div>
          {peakHours.map((h, i) => (
            <div key={i} style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
              <div style={{ width: '90px', fontSize: '11px', color: '#888' }}>{h.label}</div>
              <div style={{ flex: 1, background: '#2a2a4a', borderRadius: '4px', height: '8px' }}>
                <div style={{ width: `${h.level}%`, background: h.color, height: '100%', borderRadius: '4px' }} />
              </div>
            </div>
          ))}
        </div>

        {/* Stress Factors */}
        <div style={{ background: '#1a1a2e', border: '1px solid #2a2a4a', borderRadius: '12px', padding: '24px' }}>
          <div style={{ fontSize: '14px', color: '#888', marginBottom: '12px' }}>Stress Factors</div>
          <div style={{ fontSize: '13px', color: '#ccc', lineHeight: '2.2' }}>
            <div>🌡️ High temp → AC demand spike</div>
            <div>💧 Low rainfall → hydro stress</div>
            <div>📅 June dry season</div>
            <div>🏙️ Urban density: Colombo</div>
          </div>
        </div>
      </div>

      {/* Disclaimer */}
      <div style={{ background: '#1a1a1a', border: '1px solid #333', borderRadius: '12px', padding: '16px' }}>
        <div style={{ fontSize: '12px', color: '#666', lineHeight: '1.6' }}>
          ⚠️ This platform does <strong style={{ color: '#888' }}>not</strong> predict exact power cut schedules.
          Grid stress levels are estimates based on weather patterns and community reports.
          For official schedules, check the Ceylon Electricity Board.
        </div>
      </div>
    </div>
  );
};

export default Power;