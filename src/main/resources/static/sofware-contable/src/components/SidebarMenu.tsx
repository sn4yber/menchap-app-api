import React from 'react';

export type ViewType = 'DASHBOARD' | 'INVENTARIO' | 'COMPRAS' | 'VENTAS' | 'REPORTES' | 'CONFIGURACION';

interface SidebarMenuProps {
  activeView: ViewType;
  onSelectView: (view: ViewType) => void;
}

const SidebarMenu: React.FC<SidebarMenuProps> = ({ activeView, onSelectView }) => (
  <aside className="sidebar">
    <nav className="nav-menu">
      <div className={`menu-item${activeView === 'DASHBOARD' ? ' active' : ''}`} onClick={() => onSelectView('DASHBOARD')}>
        <img src="/dashboard.png" alt="Dashboard" className="menu-icon" />
        <div className="menu-item-content">
          <span>Dashboard</span>
          <p className="menu-desc">Vista general del negocio</p>
        </div>
      </div>
      <div className={`menu-item${activeView === 'INVENTARIO' ? ' active' : ''}`} onClick={() => onSelectView('INVENTARIO')}>
        <img src="/inventario.png" alt="Inventario" className="menu-icon" />
        <div className="menu-item-content">
          <span>Inventario</span>
          <p className="menu-desc">Gestión de productos</p>
        </div>
      </div>
      <div className={`menu-item${activeView === 'VENTAS' ? ' active' : ''}`} onClick={() => onSelectView('VENTAS')}>
        <img src="/ventas.png" alt="Ventas" className="menu-icon" />
        <div className="menu-item-content">
          <span>Ventas</span>
          <p className="menu-desc">Registro de ventas</p>
        </div>
      </div>
      <div className={`menu-item${activeView === 'COMPRAS' ? ' active' : ''}`} onClick={() => onSelectView('COMPRAS')}>
        <img src="/carrito-de-compras.png" alt="Compras" className="menu-icon" />
        <div className="menu-item-content">
          <span>Compras</span>
          <p className="menu-desc">Control de proveedores</p>
        </div>
      </div>
      <div className={`menu-item${activeView === 'REPORTES' ? ' active' : ''}`} onClick={() => onSelectView('REPORTES')}>
        <img src="/reportes.png" alt="Reportes" className="menu-icon" />
        <div className="menu-item-content">
          <span>Reportes</span>
          <p className="menu-desc">Análisis y estadísticas</p>
        </div>
      </div>
      <div className={`menu-item${activeView === 'CONFIGURACION' ? ' active' : ''}`} onClick={() => onSelectView('CONFIGURACION')}>
        <img src="/config.png" alt="Configuración" className="menu-icon" />
        <div className="menu-item-content">
          <span>Configuración</span>
          <p className="menu-desc">Ajustes del sistema</p>
        </div>
      </div>
    </nav>
  </aside>
);

export default SidebarMenu;
