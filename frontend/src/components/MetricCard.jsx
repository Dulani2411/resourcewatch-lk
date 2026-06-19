const MetricCard = ({ title, value, subtitle, color }) => {
  return (
    <div style={{
      background: '#1a1a2e',
      border: '1px solid #2a2a4a',
      borderRadius: '12px',
      padding: '16px',
    }}>
      <div style={{ fontSize: '12px', color: '#888', marginBottom: '6px' }}>{title}</div>
      <div style={{ fontSize: '24px', fontWeight: '600', color: color || '#fff' }}>{value}</div>
      {subtitle && <div style={{ fontSize: '11px', color: '#666', marginTop: '4px' }}>{subtitle}</div>}
    </div>
  );
};

export default MetricCard;