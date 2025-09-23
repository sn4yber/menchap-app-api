import React from 'react';

interface DashboardHeaderProps {
  onReloadDashboard?: () => void;
  onNavigateToLogin: () => void;
}

const DashboardHeader: React.FC<DashboardHeaderProps> = ({ onReloadDashboard, onNavigateToLogin }) => (
  <header className="dashboard-header">
    <div className="header-content">
      <div className="logo-section">
        <h1 className="logo clickable-logo" onClick={onReloadDashboard}>Livo</h1>
        <p className="subtitle">Dashboard Principal</p>
      </div>
      <div className="search-section">
        <input 
          type="text" 
          placeholder="Buscar..." 
          className="search-input"
        />
      </div>
      <div className="user-section">
        <button className="user-avatar" onClick={onNavigateToLogin}>U</button>
      </div>
    </div>
  </header>
);

export default DashboardHeader;
