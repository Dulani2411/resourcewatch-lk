import { useState, useEffect } from 'react';
import { getLatestWeather, getWaterRisk, getPowerStress, getReports } from '../services/api';
import MetricCard from '../components/MetricCard';
import RiskBadge from '../components/RiskBadge';

const districts = [
  'Colombo', 'Gampaha', 'Kandy', 'Galle',
  'Matara', 'Kurunegala', 'Anuradhapura', 'Jaffna'
];

const Overview = () => {
  const [weather, setWeather]     = useState(null);
  const [waterRisk, setWaterRisk] = useState(null);
  const [powerStress, setPowerStress] = useState(null);
  const [reports, setReports]     = useState([]);
  const [loading, setLoading]     = useState(true);

  useEffect(() => {
    const fetchAll = async () => {
      try {
        const [w, wr, ps, r] = await Promise.all([
          getLatestWeather(),
          getWaterRisk(),
          getPowerStress(),
          getReports(),
        ]);
        setWeather(w.data);
        setWaterRisk(wr.data);
        setPowerStress(ps.data);
        setReports(r.data);
      } catch (err) {
        console.error('Error fetching data:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchAll();
  }, []);

  if (loading) return (
    <div style={{ padding: '40px', textAlign: 'center', color: '#888' }}>
      Loading ResourceWatch data...
    </div>
  );

  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <h2 style={{ color: '#fff', marginBottom: '20px' }}>
        🌍 Overview — Colombo, Sri Lanka
      </h2>

      {/* Metric Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '12px', marginBottom: '20px' }}>
        <MetricCard
          title="🌡️ Temperature"
          value={weather ? `${weather.temperature}°C` : 'N/A'}
          subtitle="Current conditions"
          color="#EF9F27"
        />
        <MetricCard
          title="💧 Humidity"
          value={weather ? `${weather.humidity}%` : 'N/A'}
          subtitle="Relative humidity"
          color="#378ADD"
        />
        <MetricCard
          title="🌧️ Rainfall"
          value={weather ? `${weather.rainfallMm}mm` : 'N/A'}
          subtitle="Last reading"
          color="#1D9E75"
        />
        <MetricCard
          title="📊 Reports Today"
          value={reports.length}
          subtitle="Community reports"
          color="#7F77DD"
        />
      </div>

      {/* Risk Summary */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '20px' }}>
        <div style={{ background: '#1a1a2e', border: '1px solid #2a2a4a', borderRadius: '12px', padding: '16px' }}>
          <div style={{ fontSize: '14px', color: '#888', marginBottom: '12px' }}>💧 Water Risk Index</div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <div style={{ fontSize: '36px', fontWeight: '700', color: '#fff' }}>
              {waterRisk?.score ?? 0}
            </div>
            <div>
              <RiskBadge level={waterRisk?.level ?? 'LOW'} />
              <div style={{ fontSize: '11px', color: '#666', marginTop: '4px' }}>out of 100</div>
            </div>
          </div>
        </div>

        <div style={{ background: '#1a1a2e', border: '1px solid #2a2a4a', borderRadius: '12px', padding: '16px' }}>
          <div style={{ fontSize: '14px', color: '#888', marginBottom: '12px' }}>⚡ Grid Stress Level</div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <RiskBadge level={powerStress?.level ?? 'LOW'} />
            <div style={{ fontSize: '12px', color: '#666' }}>
              {powerStress?.isPeakHourNow ? '⚠️ Peak hour now!' : 'Not peak hour'}
            </div>
          </div>
          <div style={{ fontSize: '11px', color: '#666', marginTop: '8px' }}>
            Peak: {powerStress?.peakHours?.join(', ')}
          </div>
        </div>
      </div>

      {/* Recent Reports */}
      <div style={{ background: '#1a1a2e', border: '1px solid #2a2a4a', borderRadius: '12px', padding: '16px' }}>
        <div style={{ fontSize: '14px', color: '#888', marginBottom: '12px' }}>📍 Recent Community Reports</div>
        {reports.length === 0 ? (
          <div style={{ color: '#666', fontSize: '13px' }}>No reports yet. Be the first to report!</div>
        ) : (
          reports.slice(0, 5).map((r, i) => (
            <div key={i} style={{
              display: 'flex', justifyContent: 'space-between',
              padding: '8px 0', borderBottom: '1px solid #2a2a4a',
              fontSize: '13px', color: '#ccc'
            }}>
              <div>
                <span style={{ color: r.issueType === 'WATER' ? '#378ADD' : '#EF9F27' }}>
                  {r.issueType === 'WATER' ? '💧' : '⚡'}
                </span>
                {' '}{r.district} — {r.area || 'Unknown area'}
              </div>
              <div style={{ color: '#666' }}>{r.issueType}</div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default Overview;