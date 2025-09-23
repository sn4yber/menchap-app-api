import React from 'react';

interface HeaderProps {
  onReloadDashboard?: () => void;
  onNavigateToLogin?: () => void;
}

const Header: React.FC<HeaderProps> = ({ onReloadDashboard, onNavigateToLogin }) => (
  <header className="dashboard-header">
    <div className="header-content">
      <div className="logo-section">
        <h1 className="logo clickable-logo" onClick={onReloadDashboard}>Livo</h1>
        <p className="subtitle">Dashboard Principal</p>
      </div>
      <div className="user-section" style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        <button className="user-avatar" title="Usuario">U</button>
        <button className="logout-btn" onClick={onNavigateToLogin}>Cerrar sesión</button>
      </div>
    </div>
  </header>
);

export default Header;
