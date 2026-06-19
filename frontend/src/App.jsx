import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Overview from './pages/Overview';
import Water from './pages/Water';
import Power from './pages/Power';
import Report from './pages/Report';
import AIAdvisor from './pages/AIAdvisor';

function App() {
  return (
    <BrowserRouter>
      <div style={{ minHeight: '100vh', background: '#0f0f1a', color: '#fff', fontFamily: 'system-ui, sans-serif' }}>
        <Navbar />
        <Routes>
          <Route path="/"       element={<Overview />} />
          <Route path="/water"  element={<Water />} />
          <Route path="/power"  element={<Power />} />
          <Route path="/report" element={<Report />} />
          <Route path="/ai"     element={<AIAdvisor />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;