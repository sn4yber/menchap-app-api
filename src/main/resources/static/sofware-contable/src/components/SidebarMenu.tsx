import React from 'react';

export type ViewType = 'DASHBOARD' | 'INVENTARIO' | 'COMPRAS' | 'VENTAS' | 'REPORTES';

interface SidebarMenuProps {
  activeView: ViewType;
  onSelectView: (view: ViewType) => void;
  isOpen: boolean;
  onClose: () => void;
}

const SidebarMenu: React.FC<SidebarMenuProps> = ({ activeView, onSelectView, isOpen }) => {
  return (
    <aside className={`modern-sidebar ${isOpen ? 'sidebar-open' : 'sidebar-closed'}`}>
      <div className="sidebar-content">
        <div className="sidebar-brand">
          <div className="brand-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7V17L12 22L22 17V7L12 2Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              <path d="M12 22V12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              <path d="M2 7L12 12L22 7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          {isOpen && <span className="brand-text">Livo</span>}
        </div>

        <nav className="sidebar-nav">
          <div 
            className={`nav-item ${activeView === 'DASHBOARD' ? 'active' : ''}`} 
            onClick={() => onSelectView('DASHBOARD')}
          >
            <div className="nav-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="3" width="7" height="7" rx="1" stroke="currentColor" strokeWidth="2"/>
                <rect x="14" y="3" width="7" height="5" rx="1" stroke="currentColor" strokeWidth="2"/>
                <rect x="14" y="12" width="7" height="9" rx="1" stroke="currentColor" strokeWidth="2"/>
                <rect x="3" y="14" width="7" height="7" rx="1" stroke="currentColor" strokeWidth="2"/>
              </svg>
            </div>
            {isOpen && <span className="nav-text">Dashboard</span>}
          </div>

          <div 
            className={`nav-item ${activeView === 'INVENTARIO' ? 'active' : ''}`} 
            onClick={() => onSelectView('INVENTARIO')}
          >
            <div className="nav-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z" stroke="currentColor" strokeWidth="2"/>
                <path d="M3.3 7 12 12l8.7-5" stroke="currentColor" strokeWidth="2"/>
                <path d="M12 22V12" stroke="currentColor" strokeWidth="2"/>
              </svg>
            </div>
            {isOpen && <span className="nav-text">Inventario</span>}
          </div>

          <div 
            className={`nav-item ${activeView === 'VENTAS' ? 'active' : ''}`} 
            onClick={() => onSelectView('VENTAS')}
          >
            <div className="nav-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 3v18l18-9-18-9z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                <path d="M12 12h9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            {isOpen && <span className="nav-text">Ventas</span>}
          </div>

          <div 
            className={`nav-item ${activeView === 'COMPRAS' ? 'active' : ''}`} 
            onClick={() => onSelectView('COMPRAS')}
          >
            <div className="nav-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z" stroke="currentColor" strokeWidth="2"/>
                <line x1="3" y1="6" x2="21" y2="6" stroke="currentColor" strokeWidth="2"/>
                <path d="M16 10a4 4 0 0 1-8 0" stroke="currentColor" strokeWidth="2"/>
              </svg>
            </div>
            {isOpen && <span className="nav-text">Compras</span>}
          </div>

          <div 
            className={`nav-item ${activeView === 'REPORTES' ? 'active' : ''}`} 
            onClick={() => onSelectView('REPORTES')}
          >
            <div className="nav-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 3v18h18" stroke="currentColor" strokeWidth="2"/>
                <path d="M18.7 8a4 4 0 0 0-7.4 0" stroke="currentColor" strokeWidth="2"/>
                <path d="M7 12v4" stroke="currentColor" strokeWidth="2"/>
                <path d="M12 8v8" stroke="currentColor" strokeWidth="2"/>
                <path d="M17 4v12" stroke="currentColor" strokeWidth="2"/>
              </svg>
            </div>
            {isOpen && <span className="nav-text">Reportes</span>}
          </div>
        </nav>
      </div>
    </aside>
  );
};

export default SidebarMenu;