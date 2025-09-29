import React from 'react';

interface HeaderProps {
  onReloadDashboard?: () => void;
  onNavigateToLogin?: () => void;
  onToggleSidebar?: () => void;
}

const Header: React.FC<HeaderProps> = ({ onReloadDashboard, onNavigateToLogin, onToggleSidebar }) => (
  <header className="dashboard-header">
    <div className="header-content">
      <div className="left-section" style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
        <button className="hamburger-btn" onClick={onToggleSidebar} title="Abrir menú">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 12H21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            <path d="M3 6H21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            <path d="M3 18H21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </button>
        <div className="logo-section">
          <h1 className="logo clickable-logo" onClick={onReloadDashboard}>Livo</h1>
          <p className="subtitle">Dashboard Principal</p>
        </div>
      </div>
      <div className="user-section" style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        <button className="user-avatar" title="Usuario">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 21C20 19.6044 20 18.9067 19.8278 18.3389C19.44 17.0605 18.4395 16.06 17.1611 15.6722C16.5933 15.5 15.8956 15.5 14.5 15.5H9.5C8.10444 15.5 7.40665 15.5 6.83886 15.6722C5.56045 16.06 4.56004 17.0605 4.17224 18.3389C4 18.9067 4 19.6044 4 21M16.5 7.5C16.5 10.5376 14.0376 13 11 13C7.96243 13 5.5 10.5376 5.5 7.5C5.5 4.46243 7.96243 2 11 2C14.0376 2 16.5 4.46243 16.5 7.5Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </button>
        <button className="logout-btn" onClick={onNavigateToLogin} title="Cerrar sesión">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            <path d="M16 17L21 12L16 7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            <path d="M21 12H9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        </button>
      </div>
    </div>
  </header>
);

export default Header;
