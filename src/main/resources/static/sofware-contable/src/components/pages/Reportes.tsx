import React, { useState, useEffect } from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
} from 'chart.js';
import { Line, Bar } from 'react-chartjs-2';

// Registrar componentes de Chart.js
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
);

interface DashboardStats {
  total_productos: number;
  productos_en_stock: number;
  productos_stock_bajo: number;
  total_ventas_hoy: number;
  total_ventas_mes: number;
  total_compras_mes: number;
  ganancias_hoy: number;
  ganancias_mes: number;
  numero_ventas_hoy: number;
  numero_ventas_mes: number;
  alertas_activas: number;
}

interface ProductoMasVendido {
  producto_id: number;
  nombre_producto: string;
  cantidad_total: number;
  veces_vendido: number;
  ingresos_totales: number;
}

interface AlertaInventario {
  id: number;
  productoId: number;
  tipoAlerta: string;
  mensaje: string;
  nivelSeveridad: string;
  fechaAlerta: string;
  resuelta: boolean;
}

interface MetricaDiaria {
  fecha: string;
  totalVentas: number;
  totalCompras: number;
  gananciaTotal: number;
  numeroVentas: number;
}

interface RentabilidadProducto {
  producto_id: number;
  nombre_producto: string;
  total_vendido: number;
  total_comprado: number;
  ganancia_neta: number;
  margen_porcentaje: number;
  roi_porcentaje: number;
}

const Reportes: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'dashboard' | 'ventas' | 'inventario' | 'financiero'>('dashboard');
  const [loading, setLoading] = useState(true);
  const [dashboardStats, setDashboardStats] = useState<DashboardStats | null>(null);
  const [productosMasVendidos, setProductosMasVendidos] = useState<ProductoMasVendido[]>([]);
  const [alertas, setAlertas] = useState<AlertaInventario[]>([]);
  const [tendencias, setTendencias] = useState<MetricaDiaria[]>([]);
  const [rentabilidad, setRentabilidad] = useState<RentabilidadProducto[]>([]);
  const [ultimaActualizacion, setUltimaActualizacion] = useState<Date>(new Date());

  const API_URL = 'http://localhost:8080/api';

  // ✅ Cargar TODO en una sola llamada
  const cargarDashboardCompleto = async (mostrarCargando = true) => {
    if (mostrarCargando) setLoading(true);
    
    try {
      console.log('� Cargando dashboard completo consolidado...');
      const response = await fetch(`${API_URL}/reportes/dashboard-completo`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      console.log('✅ Dashboard completo recibido:', data);
      
      // Actualizar todos los estados a la vez
      setDashboardStats(data.stats);
      setProductosMasVendidos(data.productos_mas_vendidos || []);
      setAlertas(data.alertas_data?.alertas || []);
      setTendencias(data.tendencias || []);
      setRentabilidad(data.rentabilidad || []);
      setUltimaActualizacion(new Date());
      
    } catch (error) {
      console.error('❌ Error cargando dashboard:', error);
      if (mostrarCargando) {
        alert('Error cargando reportes. Verifica que el backend esté corriendo en el puerto 8080');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // Cargar datos iniciales
    cargarDashboardCompleto(true);

    // Refrescar datos cada 30 segundos (antes era cada 10)
    // Y sin mostrar el loading para no interrumpir la UX
    const interval = setInterval(() => {
      cargarDashboardCompleto(false);
    }, 30000);

    // Escuchar eventos de actualización
    const handleUpdate = () => {
      cargarDashboardCompleto(false);
    };

    window.addEventListener('venta:created', handleUpdate);
    window.addEventListener('compra:created', handleUpdate);
    window.addEventListener('inventario:updated', handleUpdate);

    return () => {
      clearInterval(interval);
      window.removeEventListener('venta:created', handleUpdate);
      window.removeEventListener('compra:created', handleUpdate);
      window.removeEventListener('inventario:updated', handleUpdate);
    };
  }, []);

  // Configuración del gráfico de tendencias
  const tendenciasChartData = {
    labels: tendencias.map(t => new Date(t.fecha).toLocaleDateString('es-ES', { day: '2-digit', month: 'short' })),
    datasets: [
      {
        label: 'Ventas',
        data: tendencias.map(t => t.totalVentas),
        borderColor: 'rgb(75, 192, 192)',
        backgroundColor: 'rgba(75, 192, 192, 0.5)',
        tension: 0.4,
      },
      {
        label: 'Compras',
        data: tendencias.map(t => t.totalCompras),
        borderColor: 'rgb(255, 99, 132)',
        backgroundColor: 'rgba(255, 99, 132, 0.5)',
        tension: 0.4,
      },
      {
        label: 'Ganancia',
        data: tendencias.map(t => t.gananciaTotal),
        borderColor: 'rgb(53, 162, 235)',
        backgroundColor: 'rgba(53, 162, 235, 0.5)',
        tension: 0.4,
      },
    ],
  };

  // Configuración del gráfico de productos más vendidos
  const productosMasVendidosChartData = {
    labels: productosMasVendidos.slice(0, 5).map(p => p.nombre_producto),
    datasets: [
      {
        label: 'Ingresos',
        data: productosMasVendidos.slice(0, 5).map(p => p.ingresos_totales),
        backgroundColor: [
          'rgba(255, 99, 132, 0.8)',
          'rgba(54, 162, 235, 0.8)',
          'rgba(255, 206, 86, 0.8)',
          'rgba(75, 192, 192, 0.8)',
          'rgba(153, 102, 255, 0.8)',
        ],
      },
    ],
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  return (
    <div className="reportes-container">
      {/* Header */}
      <div className="reportes-header">
        <div>
          <h1 className="reportes-title" style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="32" height="32">
              <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
            </svg>
            Reportes y Análisis
          </h1>
          <p className="reportes-subtitle">Dashboard ejecutivo y reportes detallados del sistema</p>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <span style={{ fontSize: '12px', color: '#6b7280', display: 'flex', alignItems: 'center', gap: '4px' }}>
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="16" height="16">
              <path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/>
            </svg>
            Actualizado: {ultimaActualizacion.toLocaleTimeString('es-ES')}
          </span>
          <button 
            onClick={() => cargarDashboardCompleto(true)}
            disabled={loading}
            className="refresh-button"
            style={{
              padding: '10px 20px',
              backgroundColor: loading ? '#9ca3af' : '#4F46E5',
              color: 'white',
              border: 'none',
              borderRadius: '8px',
              cursor: loading ? 'not-allowed' : 'pointer',
              fontSize: '14px',
              fontWeight: '600',
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              transition: 'all 0.2s'
            }}
          >
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="18" height="18" style={{ animation: loading ? 'spin 1s linear infinite' : 'none' }}>
              <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/>
            </svg>
            Actualizar
          </button>
        </div>
      </div>

      {/* Tabs */}
      <div className="reportes-tabs">
        <button
          onClick={() => setActiveTab('dashboard')}
          className={`tab-button ${activeTab === 'dashboard' ? 'active' : ''}`}
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
            <path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/>
          </svg>
          Dashboard
        </button>
        <button
          onClick={() => setActiveTab('ventas')}
          className={`tab-button ${activeTab === 'ventas' ? 'active' : ''}`}
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
            <path d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z"/>
          </svg>
          Ventas
        </button>
        <button
          onClick={() => setActiveTab('inventario')}
          className={`tab-button ${activeTab === 'inventario' ? 'active' : ''}`}
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
            <path d="M20 2H4c-1 0-2 .9-2 2v3.01c0 .72.43 1.34 1 1.69V20c0 1.1 1.1 2 2 2h14c.9 0 2-.9 2-2V8.7c.57-.35 1-.97 1-1.69V4c0-1.1-1-2-2-2zm-5 12H9v-2h6v2zm5-7H4V4h16v3z"/>
          </svg>
          Inventario
        </button>
        <button
          onClick={() => setActiveTab('financiero')}
          className={`tab-button ${activeTab === 'financiero' ? 'active' : ''}`}
          style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
            <path d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/>
          </svg>
          Financiero
        </button>
      </div>

      {/* Dashboard Tab */}
      {activeTab === 'dashboard' && (
        <div className="reportes-content">
          {/* KPIs Cards */}
          {dashboardStats && (
            <div className="kpi-grid">
              <div className="kpi-card kpi-blue">
                <div className="kpi-content">
                  <div className="kpi-info">
                    <p className="kpi-label">Ventas del Mes</p>
                    <p className="kpi-value">{formatCurrency(dashboardStats.total_ventas_mes)}</p>
                    <p className="kpi-detail">{dashboardStats.numero_ventas_mes} transacciones</p>
                  </div>
                  <div className="kpi-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="48" height="48">
                      <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm.31-8.86c-1.77-.45-2.34-.94-2.34-1.67 0-.84.79-1.43 2.1-1.43 1.38 0 1.9.66 1.94 1.64h1.71c-.05-1.34-.87-2.57-2.49-2.97V5H10.9v1.69c-1.51.32-2.72 1.3-2.72 2.81 0 1.79 1.49 2.69 3.66 3.21 1.95.46 2.34 1.15 2.34 1.87 0 .53-.39 1.39-2.1 1.39-1.6 0-2.23-.72-2.32-1.64H8.04c.1 1.7 1.36 2.66 2.86 2.97V19h2.34v-1.67c1.52-.29 2.72-1.16 2.73-2.77-.01-2.2-1.9-2.96-3.66-3.42z"/>
                    </svg>
                  </div>
                </div>
              </div>

              <div className="kpi-card kpi-green">
                <div className="kpi-content">
                  <div className="kpi-info">
                    <p className="kpi-label">Ganancias del Mes</p>
                    <p className="kpi-value">{formatCurrency(dashboardStats.ganancias_mes)}</p>
                    <p className="kpi-detail">Hoy: {formatCurrency(dashboardStats.ganancias_hoy)}</p>
                  </div>
                  <div className="kpi-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="48" height="48">
                      <path d="M16 6l2.29 2.29-4.88 4.88-4-4L2 16.59 3.41 18l6-6 4 4 6.3-6.29L22 12V6h-6z"/>
                    </svg>
                  </div>
                </div>
              </div>

              <div className="kpi-card kpi-purple">
                <div className="kpi-content">
                  <div className="kpi-info">
                    <p className="kpi-label">Productos</p>
                    <p className="kpi-value">{dashboardStats.productos_en_stock}</p>
                    <p className="kpi-detail">En stock / {dashboardStats.total_productos} total</p>
                  </div>
                  <div className="kpi-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="48" height="48">
                      <path d="M20 8h-3V4H3c-1.1 0-2 .9-2 2v11h2c0 1.66 1.34 3 3 3s3-1.34 3-3h6c0 1.66 1.34 3 3 3s3-1.34 3-3h2v-5l-3-4zm-.5 1.5l1.96 2.5H17V9.5h2.5zM6 18c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm2.22-3c-.55-.61-1.33-1-2.22-1s-1.67.39-2.22 1H3V6h12v9H8.22zM18 18c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1z"/>
                    </svg>
                  </div>
                </div>
              </div>

              <div className="kpi-card kpi-red">
                <div className="kpi-content">
                  <div className="kpi-info">
                    <p className="kpi-label">Alertas Activas</p>
                    <p className="kpi-value">{dashboardStats.alertas_activas}</p>
                    <p className="kpi-detail">{dashboardStats.productos_stock_bajo} stock bajo</p>
                  </div>
                  <div className="kpi-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="48" height="48">
                      <path d="M12 5.99L19.53 19H4.47L12 5.99M12 2L1 21h22L12 2zm1 14h-2v2h2v-2zm0-6h-2v4h2v-4z"/>
                    </svg>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Gráficos */}
          <div className="charts-grid">
            {/* Tendencias */}
            <div className="chart-card">
              <h3 className="chart-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
                  <path d="M3.5 18.49l6-6.01 4 4L22 6.92l-1.41-1.41-7.09 7.97-4-4L2 16.99z"/>
                </svg>
                Tendencias (Últimos 30 días)
              </h3>
              {tendencias.length > 0 ? (
                <div className="chart-wrapper">
                  <Line data={tendenciasChartData} options={{ responsive: true, maintainAspectRatio: true }} />
                </div>
              ) : (
                <p className="empty-message">No hay datos disponibles</p>
              )}
            </div>

            {/* Productos Más Vendidos */}
            <div className="chart-card">
              <h3 className="chart-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                </svg>
                Top 5 Productos Más Vendidos
              </h3>
              {productosMasVendidos.length > 0 ? (
                <div className="chart-wrapper">
                  <Bar 
                    data={productosMasVendidosChartData} 
                    options={{ 
                      responsive: true, 
                      maintainAspectRatio: true,
                      plugins: {
                        legend: {
                          display: false
                        }
                      }
                    }} 
                  />
                </div>
              ) : (
                <p className="empty-message">No hay datos disponibles</p>
              )}
            </div>
          </div>

          {/* Alertas de Inventario */}
          <div className="alertas-section">
            <h3 className="section-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
                <path d="M12 5.99L19.53 19H4.47L12 5.99M12 2L1 21h22L12 2zm1 14h-2v2h2v-2zm0-6h-2v4h2v-4z"/>
              </svg>
              Alertas de Inventario
            </h3>
            {alertas.length > 0 ? (
              <div className="alertas-list">
                {alertas.slice(0, 10).map((alerta) => (
                  <div key={alerta.id} className={`alerta-item alerta-${alerta.nivelSeveridad.toLowerCase()}`}>
                    <div className="alerta-content">
                      <div className="alerta-body">
                        <p className="alerta-tipo">{alerta.tipoAlerta}</p>
                        <p className="alerta-mensaje">{alerta.mensaje}</p>
                        <p className="alerta-fecha">
                          {new Date(alerta.fechaAlerta).toLocaleString('es-ES')}
                        </p>
                      </div>
                      <span className={`alerta-badge badge-${alerta.nivelSeveridad.toLowerCase()}`}>
                        {alerta.nivelSeveridad}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p className="empty-message success" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
                  <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                </svg>
                No hay alertas activas
              </p>
            )}
          </div>
        </div>
      )}

      {/* Ventas Tab */}
      {activeTab === 'ventas' && (
        <div className="reportes-content">
            <div className="table-section">
            <h3 className="section-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
                <path d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z"/>
              </svg>
              Productos Más Vendidos
            </h3>
            <div className="table-responsive">
              <table className="reportes-table">
                <thead>
                  <tr>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>Veces Vendido</th>
                    <th>Ingresos</th>
                  </tr>
                </thead>
                <tbody>
                  {productosMasVendidos.map((producto, index) => (
                    <tr key={producto.producto_id}>
                      <td>
                        <div className="product-cell">
                          <span className="product-rank">#{index + 1}</span>
                          <span className="product-name">{producto.nombre_producto}</span>
                        </div>
                      </td>
                      <td>{producto.cantidad_total}</td>
                      <td>{producto.veces_vendido}</td>
                      <td className="value-highlight">{formatCurrency(producto.ingresos_totales)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* Inventario Tab */}
      {activeTab === 'inventario' && (
        <div className="reportes-content">
          <div className="alertas-section">
            <h3 className="section-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
                <path d="M20 2H4c-1 0-2 .9-2 2v3.01c0 .72.43 1.34 1 1.69V20c0 1.1 1.1 2 2 2h14c.9 0 2-.9 2-2V8.7c.57-.35 1-.97 1-1.69V4c0-1.1-1-2-2-2zm-5 12H9v-2h6v2zm5-7H4V4h16v3z"/>
              </svg>
              Alertas de Inventario
            </h3>
            <div className="alertas-list">
              {alertas.map((alerta) => (
                <div key={alerta.id} className={`alerta-item alerta-${alerta.nivelSeveridad.toLowerCase()}`}>
                  <div className="alerta-content">
                    <div className="alerta-body">
                      <div className="alerta-header-row">
                        <span className={`alerta-badge-inline badge-${alerta.nivelSeveridad.toLowerCase()}`}>
                          {alerta.nivelSeveridad}
                        </span>
                        <span className="alerta-tipo">{alerta.tipoAlerta}</span>
                      </div>
                      <p className="alerta-mensaje">{alerta.mensaje}</p>
                      <p className="alerta-fecha">
                        📅 {new Date(alerta.fechaAlerta).toLocaleString('es-ES')}
                      </p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Financiero Tab */}
      {activeTab === 'financiero' && (
        <div className="reportes-content">
          <div className="table-section">
            <h3 className="section-title" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
                <path d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/>
              </svg>
              Rentabilidad por Producto
            </h3>
            <div className="table-responsive">
              <table className="reportes-table">
                <thead>
                  <tr>
                    <th>Producto</th>
                    <th>Vendido</th>
                    <th>Comprado</th>
                    <th>Ganancia</th>
                    <th>Margen %</th>
                    <th>ROI %</th>
                  </tr>
                </thead>
                <tbody>
                  {rentabilidad.slice(0, 15).map((item) => (
                    <tr key={item.producto_id}>
                      <td className="product-name-cell">{item.nombre_producto}</td>
                      <td>{formatCurrency(item.total_vendido)}</td>
                      <td>{formatCurrency(item.total_comprado)}</td>
                      <td className="value-highlight">{formatCurrency(item.ganancia_neta)}</td>
                      <td>
                        <span className={`percentage-badge ${
                          item.margen_porcentaje > 30 ? 'badge-success' :
                          item.margen_porcentaje > 15 ? 'badge-warning' :
                          'badge-danger'
                        }`}>
                          {item.margen_porcentaje.toFixed(1)}%
                        </span>
                      </td>
                      <td>
                        <span className={`percentage-badge ${
                          item.roi_porcentaje > 50 ? 'badge-success' :
                          item.roi_porcentaje > 25 ? 'badge-warning' :
                          'badge-danger'
                        }`}>
                          {item.roi_porcentaje.toFixed(1)}%
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* Loading Overlay - Solo en carga inicial */}
      {loading && !dashboardStats && (
        <div className="loading-overlay">
          <div className="loading-card">
            <div className="spinner"></div>
            <p className="loading-text">Cargando reportes...</p>
            <p style={{ fontSize: '12px', color: '#6b7280', marginTop: '8px' }}>
              Obteniendo datos del servidor...
            </p>
          </div>
        </div>
      )}
    </div>
  );
};

export default Reportes;
