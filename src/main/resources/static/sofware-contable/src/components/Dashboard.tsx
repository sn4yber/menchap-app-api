import React from 'react';
import SidebarMenu from './SidebarMenu';
import type { ViewType } from './SidebarMenu';

interface DashboardProps {
  activeView: ViewType;
  onSelectView: (view: ViewType) => void;
}

const Dashboard: React.FC<DashboardProps> = ({ activeView, onSelectView }) => {
  return (
    <div className="dashboard-container">
      <div className="main-content">
        <SidebarMenu activeView={activeView} onSelectView={onSelectView} />
        {/* Content Area */}
        <main className="content-area">
          <div className="welcome-section">
            <h2>Bienvenido de vuelta</h2>
            <p>Aquí tienes un resumen de tu negocio hoy</p>
          </div>

          {/* Stats Cards */}
          <div className="stats-grid">
            <div className="stat-card">
              <h3>Ventas de Hoy</h3>
              <div className="stat-value">$2,450</div>
              <div className="stat-change positive">+12% vs ayer</div>
              <div className="stat-detail">12 transacciones</div>
            </div>

            <div className="stat-card">
              <h3>Productos en Stock</h3>
              <div className="stat-value">1,234</div>
              <div className="stat-change negative">5 productos bajos</div>
              <div className="stat-detail">Valor: $156,340</div>
            </div>

            <div className="stat-card">
              <h3>Ganancias del Mes</h3>
              <div className="stat-value">$18,340</div>
              <div className="stat-change positive">+8% vs mes anterior</div>
              <div className="stat-detail">Margen: 34%</div>
            </div>
          </div>

          {/* Charts and Tables */}
          <div className="dashboard-grid">
            <div className="chart-section">
              <h3>Ventas de los Últimos 7 Días</h3>
              <div className="chart-placeholder">
                <div className="bar-chart">
                  <div className="bar" style={{height: '60%'}}><span>Lun</span></div>
                  <div className="bar" style={{height: '80%'}}><span>Mar</span></div>
                  <div className="bar" style={{height: '70%'}}><span>Mié</span></div>
                  <div className="bar" style={{height: '85%'}}><span>Jue</span></div>
                  <div className="bar" style={{height: '90%'}}><span>Vie</span></div>
                  <div className="bar" style={{height: '95%'}}><span>Sáb</span></div>
                  <div className="bar" style={{height: '100%'}}><span>Dom</span></div>
                </div>
              </div>
            </div>

            <div className="products-section">
              <h3>Productos Más Vendidos</h3>
              <div className="product-list">
                <div className="product-item">
                  <span className="product-name">tableta samsung </span>
                  <span className="product-sales">25 vendidos</span>
                  <span className="product-revenue">$30,000</span>
                </div>
                <div className="product-item">
                  <span className="product-name">MacBook Air M2</span>
                  <span className="product-sales">12 vendidos</span>
                  <span className="product-revenue">$21,600</span>
                </div>
                <div className="product-item">
                  <span className="product-name">AirPods Pro 2</span>
                  <span className="product-sales">18 vendidos</span>
                  <span className="product-revenue">$5,040</span>
                </div>
              </div>
            </div>
          </div>

          {/* Quick Actions */}
          <div className="quick-actions">
            <h3>Acciones Rápidas</h3>
            <div className="action-buttons">
              <button className="action-btn primary">
                <span className="btn-icon">💰</span>
                <div>
                  <span className="btn-title">Nueva Venta</span>
                  <span className="btn-subtitle">Registrar venta</span>
                </div>
              </button>

              <button className="action-btn">
                <span className="btn-icon">📦</span>
                <div>
                  <span className="btn-title">Producto</span>
                  <span className="btn-subtitle">Agregar al inventario</span>
                </div>
              </button>

              <button className="action-btn">
                <span className="btn-icon">📊</span>
                <div>
                  <span className="btn-title">Reportes</span>
                  <span className="btn-subtitle">Ver análisis completo</span>
                </div>
              </button>

              <button className="action-btn">
                <span className="btn-icon">⚙️</span>
                <div>
                  <span className="btn-title">Configuración</span>
                  <span className="btn-subtitle">Ajustes del sistema</span>
                </div>
              </button>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default Dashboard;
