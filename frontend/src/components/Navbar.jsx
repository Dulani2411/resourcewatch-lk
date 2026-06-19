import { Link, useLocation } from 'react-router-dom';

const Navbar = () => {
  const location = useLocation();

  const tabs = [
    { path: '/',         label: '🏠 Overview' },
    { path: '/water',    label: '💧 Water' },
    { path: '/power',    label: '⚡ Power' },
    { path: '/report',   label: '📍 Report' },
    { path: '/ai',       label: '🤖 AI Advisor' },
  ];

  return (
    <nav style={{
      background: '#0f0f1a',
      borderBottom: '1px solid #2a2a4a',
      padding: '0 20px',
      display: 'flex',
      alignItems: 'center',
      gap: '8px',
    }}>
      <div style={{
        color: '#1D9E75',
        fontWeight: '700',
        fontSize: '16px',
        marginRight: '16px',
        padding: '12px 0',
      }}>
        ResourceWatch LK
      </div>
      {tabs.map(tab => (
        <Link
          key={tab.path}
          to={tab.path}
          style={{
            padding: '12px 14px',
            fontSize: '13px',
            color: location.pathname === tab.path ? '#fff' : '#888',
            textDecoration: 'none',
            borderBottom: location.pathname === tab.path ? '2px solid #1D9E75' : '2px solid transparent',
            fontWeight: location.pathname === tab.path ? '500' : '400',
          }}
        >
          {tab.label}
        </Link>
      ))}
    </nav>
  );
};

export default Navbar;