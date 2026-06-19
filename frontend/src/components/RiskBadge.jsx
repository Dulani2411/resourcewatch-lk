const RiskBadge = ({ level }) => {
  const styles = {
    HIGH:   { background: '#FCEBEB', color: '#A32D2D', border: '1px solid #E24B4A' },
    MEDIUM: { background: '#FAEEDA', color: '#854F0B', border: '1px solid #EF9F27' },
    LOW:    { background: '#EAF3DE', color: '#3B6D11', border: '1px solid #6AAF20' },
  };

  const style = styles[level] || styles.LOW;

  return (
    <span style={{
      ...style,
      padding: '4px 12px',
      borderRadius: '20px',
      fontSize: '13px',
      fontWeight: '600',
    }}>
      {level}
    </span>
  );
};

export default RiskBadge;