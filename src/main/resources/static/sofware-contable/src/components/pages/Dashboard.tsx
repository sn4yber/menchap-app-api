import React, { useState, useEffect } from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
} from 'chart.js';
import { Bar, Doughnut } from 'react-chartjs-2';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
);

interface Producto {
  id?: number;
  nombre: string;
  tipo: string;
  cantidad: number;
  precio: number;
}

const Dashboard: React.FC = () => {
  const [productos, setProductos] = useState<Producto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const API_BASE_URL = 'http://localhost:8080/api';

  // Función para formatear precios con decimales
  const formatPrice = (price: number): string => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
      maximumFractionDigits: 2
    }).format(price);
  };

  // Cargar productos al montar el componente
  useEffect(() => {
    cargarProductos();
    // Escuchar eventos de actualización del inventario
    const onInventarioUpdate = () => cargarProductos();
    window.addEventListener('inventario:updated', onInventarioUpdate);
    return () => {
      window.removeEventListener('inventario:updated', onInventarioUpdate);
    };
  }, []);

  const cargarProductos = async () => {
    try {
      setLoading(true);
      const response = await fetch(`${API_BASE_URL}/inventario`);
      if (!response.ok) {
        throw new Error('Error al cargar productos');
      }
      const data = await response.json();
      console.log('Productos cargados en Dashboard:', data);
      setProductos(data);
      setError(null);
    } catch (err) {
      setError('Error al cargar los productos');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  // Calcular estadísticas reales
  const stats = {
    totalProductos: productos.length,
    valorInventario: productos.reduce((total, p) => {
      const cantidad = Number(p.cantidad) || 0;
      const precio = Number(p.precio) || 0;
      return total + (cantidad * precio);
    }, 0),
    productosAgotados: productos.filter(p => Number(p.cantidad) === 0).length,
    productosConBajoStock: productos.filter(p => {
      const cantidad = Number(p.cantidad);
      return cantidad > 0 && cantidad <= 5;
    }).length,
    categorias: [...new Set(productos.map(p => p.tipo))].length,
    ventasEstimadas: productos.reduce((total, p) => {
      const cantidad = Number(p.cantidad) || 0;
      const precio = Number(p.precio) || 0;
      return total + (precio * Math.min(cantidad, 10));
    }, 0)
  };

  // Obtener productos más vendidos (simulado basado en precio y stock)
  const productosPopulares = productos
    .filter(p => Number(p.cantidad) > 0)
    .sort((a, b) => {
      const valorA = Number(a.precio) * (100 - Number(a.cantidad));
      const valorB = Number(b.precio) * (100 - Number(b.cantidad));
      return valorB - valorA;
    })
    .slice(0, 3);

  // Preparar datos para gráfico de barras (categorías)
  const categorias = [...new Set(productos.map(p => p.tipo))];
  const datosCategoriasValor = categorias.map(categoria => {
    return productos
      .filter(p => p.tipo === categoria)
      .reduce((total, p) => {
        const cantidad = Number(p.cantidad) || 0;
        const precio = Number(p.precio) || 0;
        return total + (cantidad * precio);
      }, 0);
  });

  const barChartData = {
    labels: categorias,
    datasets: [
      {
        label: 'Valor por Categoría ($)',
        data: datosCategoriasValor,
        backgroundColor: [
          'rgba(54, 162, 235, 0.8)',
          'rgba(255, 99, 132, 0.8)',
          'rgba(255, 205, 86, 0.8)',
          'rgba(75, 192, 192, 0.8)',
          'rgba(153, 102, 255, 0.8)',
          'rgba(255, 159, 64, 0.8)',
          'rgba(199, 199, 199, 0.8)',
        ],
        borderColor: [
          'rgba(54, 162, 235, 1)',
          'rgba(255, 99, 132, 1)',
          'rgba(255, 205, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)',
          'rgba(255, 159, 64, 1)',
          'rgba(199, 199, 199, 1)',
        ],
        borderWidth: 2,
      },
    ],
  };

  // Preparar datos para gráfico circular (stock status)
  const doughnutData = {
    labels: ['Productos con Stock Normal', 'Productos con Bajo Stock', 'Productos Agotados'],
    datasets: [
      {
        data: [
          productos.filter(p => Number(p.cantidad) > 5).length,
          stats.productosConBajoStock,
          stats.productosAgotados,
        ],
        backgroundColor: [
          'rgba(34, 197, 94, 0.8)',
          'rgba(251, 146, 60, 0.8)',
          'rgba(239, 68, 68, 0.8)',
        ],
        borderColor: [
          'rgba(34, 197, 94, 1)',
          'rgba(251, 146, 60, 1)',
          'rgba(239, 68, 68, 1)',
        ],
        borderWidth: 2,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top' as const,
      },
      title: {
        display: false,
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: function(value: any) {
            return '$' + value.toLocaleString();
          }
        }
      },
    },
  };

  const doughnutOptions = {
    responsive: true,
    plugins: {
      legend: {
        position: 'bottom' as const,
      },
      title: {
        display: false,
      },
    },
  };

  if (loading) {
    return (
      <div className="content-area">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Cargando estadísticas...</p>
        </div>
      </div>
    );
  }
  return (
    <div className="content-area">
      <div className="welcome-section">
        <h2>Bienvenido de vuelta</h2>
        <p>Aquí tienes un resumen de tu negocio hoy</p>
      </div>

      {error && (
        <div className="error-banner">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
            <line x1="15" y1="9" x2="9" y2="15" stroke="currentColor" strokeWidth="2"/>
            <line x1="9" y1="9" x2="15" y2="15" stroke="currentColor" strokeWidth="2"/>
          </svg>
          {error}
          <button onClick={() => setError(null)} className="close-error">×</button>
        </div>
      )}

      {/* Stats Cards */}
      <div className="stats-grid">
            <div className="stat-card">
              <h3>Total Productos</h3>
              <div className="stat-value">{stats.totalProductos}</div>
              <div className="stat-change">{stats.categorias} categorías</div>
              <div className="stat-detail">En inventario</div>
            </div>

            <div className="stat-card">
              <h3>Valor del Inventario</h3>
              <div className="stat-value">{formatPrice(stats.valorInventario)}</div>
              <div className="stat-change negative">{stats.productosAgotados} sin stock</div>
              <div className="stat-detail">{stats.productosConBajoStock} con bajo stock</div>
            </div>

            <div className="stat-card">
              <h3>Valor Estimado</h3>
              <div className="stat-value">{formatPrice(stats.ventasEstimadas)}</div>
              <div className="stat-change positive">Potencial de ventas</div>
              <div className="stat-detail">Basado en stock disponible</div>
            </div>
          </div>

          {/* Charts and Tables */}
          <div className="dashboard-grid">
            <div className="chart-section">
              <h3>Valor por Categorías</h3>
              <div className="chart-container">
                {productos.length > 0 ? (
                  <Bar data={barChartData} options={chartOptions} />
                ) : (
                  <div className="no-data-message">
                    <p>No hay datos para mostrar</p>
                    <small>Agrega productos al inventario para ver las estadísticas</small>
                  </div>
                )}
              </div>
            </div>

            <div className="chart-section">
              <h3>Estado del Stock</h3>
              <div className="chart-container doughnut-container">
                {productos.length > 0 ? (
                  <Doughnut data={doughnutData} options={doughnutOptions} />
                ) : (
                  <div className="no-data-message">
                    <p>No hay datos para mostrar</p>
                    <small>Agrega productos al inventario para ver el estado del stock</small>
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className="products-section">
            <h3>Productos con Mayor Valor</h3>
            <div className="product-list">
              {productosPopulares.length > 0 ? (
                productosPopulares.map(producto => {
                  const cantidad = Number(producto.cantidad) || 0;
                  const precio = Number(producto.precio) || 0;
                  return (
                    <div key={producto.id} className="product-item">
                      <span className="product-name">{producto.nombre}</span>
                      <span className="product-sales">{cantidad} en stock</span>
                      <span className="product-revenue">{formatPrice(cantidad * precio)}</span>
                    </div>
                  );
                })
              ) : (
                <div className="product-item">
                  <span className="product-name">Sin productos disponibles</span>
                  <span className="product-sales">-</span>
                  <span className="product-revenue">{formatPrice(0)}</span>
                </div>
              )}
            </div>
          </div>

          {/* Quick Actions */}
          <div className="quick-actions">
            <><h3>Acciones Rápidas</h3><div className="action-buttons">
              <button className="action-btn primary">
                <span className="btn-icon">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 2V22M17 5H9.5C8.67157 5 8 5.67157 8 6.5V6.5C8 7.32843 8.67157 8 9.5 8H14.5C15.3284 8 16 8.67157 16 9.5V9.5C16 10.3284 15.3284 11 14.5 11H7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                </span>
                <div>
                  <span className="btn-title">Nueva Venta</span>
                  <span className="btn-subtitle">Registrar venta</span>
                </div>
              </button>

              <button className="action-btn">
                <span className="btn-icon">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M21 8C21 8 21 6 19 6H5C3 6 3 8 3 8V16C3 18 5 18 5 18H19C21 18 21 16 21 16V8Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                    <path d="M8 6V4C8 2 10 2 10 2H14C16 2 16 4 16 4V6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                </span>
                <div>
                  <span className="btn-title">Producto</span>
                  <span className="btn-subtitle">Agregar al inventario</span>
                </div>
              </button>

              <button className="action-btn">
                <span className="btn-icon">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M3 3V21H21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                    <path d="M9 9L12 6L16 10L20 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                </span>
                <div>
                  <span className="btn-title">Reportes</span>
                  <span className="btn-subtitle">Ver análisis completo</span>
                </div>
              </button>

              <button className="action-btn">
                <span className="btn-icon">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 15C13.6569 15 15 13.6569 15 12C15 10.3431 13.6569 9 12 9C10.3431 9 9 10.3431 9 12C9 13.6569 10.3431 15 12 15Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                    <path d="M19.4 15C19.2669 15.3016 19.2272 15.6362 19.286 15.9606C19.3448 16.285 19.4995 16.5843 19.73 16.82L19.79 16.88C19.976 17.0657 20.1235 17.2863 20.2241 17.5291C20.3248 17.7719 20.3766 18.0322 20.3766 18.295C20.3766 18.5578 20.3248 18.8181 20.2241 19.0609C20.1235 19.3037 19.976 19.5243 19.79 19.71C19.6043 19.896 19.3837 20.0435 19.1409 20.1441C18.8981 20.2448 18.6378 20.2966 18.375 20.2966C18.1122 20.2966 17.8519 20.2448 17.6091 20.1441C17.3663 20.0435 17.1457 19.896 16.96 19.71L16.9 19.65C16.6643 19.4195 16.365 19.2648 16.0406 19.206C15.7162 19.1472 15.3816 19.1869 15.08 19.32C14.7842 19.4468 14.532 19.6572 14.3543 19.9255C14.1766 20.1938 14.0813 20.5082 14.08 20.83V21C14.08 21.5304 13.8693 22.0391 13.4942 22.4142C13.1191 22.7893 12.6104 23 12.08 23C11.5496 23 11.0409 22.7893 10.6658 22.4142C10.2907 22.0391 10.08 21.5304 10.08 21V20.91C10.0723 20.579 9.96512 20.2569 9.77251 19.9859C9.5799 19.7149 9.31074 19.5057 9 19.38C8.69838 19.2469 8.36381 19.2072 8.03941 19.266C7.71502 19.3248 7.41568 19.4795 7.18 19.71L7.12 19.77C6.93425 19.956 6.71368 20.1035 6.47088 20.2041C6.22808 20.3048 5.96783 20.3566 5.705 20.3566C5.44217 20.3566 5.18192 20.3048 4.93912 20.2041C4.69632 20.1035 4.47575 19.956 4.29 19.77C4.10405 19.5843 3.95653 19.3637 3.85588 19.1209C3.75523 18.8781 3.70343 18.6178 3.70343 18.355C3.70343 18.0922 3.75523 17.8319 3.85588 17.5891C3.95653 17.3463 4.10405 17.1257 4.29 16.94L4.35 16.88C4.58054 16.6443 4.73519 16.345 4.794 16.0206C4.85282 15.6962 4.81312 15.3616 4.68 15.06C4.55324 14.7642 4.34276 14.512 4.07447 14.3343C3.80618 14.1566 3.49179 14.0613 3.17 14.06H3C2.46957 14.06 1.96086 13.8493 1.58579 13.4742C1.21071 13.0991 1 12.5904 1 12.06C1 11.5296 1.21071 11.0209 1.58579 10.6458C1.96086 10.2707 2.46957 10.06 3 10.06H3.09C3.42099 10.0523 3.742 9.94512 4.013 9.75251C4.284 9.5599 4.49426 9.29074 4.62 8.98C4.75312 8.67838 4.79282 8.34381 4.734 8.01941C4.67519 7.69502 4.52054 7.39568 4.29 7.16L4.23 7.1C4.04405 6.91425 3.89653 6.69368 3.79588 6.45088C3.69523 6.20808 3.64343 5.94783 3.64343 5.685C3.64343 5.42217 3.69523 5.16192 3.79588 4.91912C3.89653 4.67632 4.04405 4.45575 4.23 4.27C4.41575 4.08405 4.63632 3.93653 4.87912 3.83588C5.12192 3.73523 5.38217 3.68343 5.645 3.68343C5.90783 3.68343 6.16808 3.73523 6.41088 3.83588C6.65368 3.93653 6.87425 4.08405 7.06 4.27L7.12 4.33C7.35568 4.56054 7.65502 4.71519 7.97941 4.774C8.30381 4.83282 8.63838 4.79312 8.94 4.66H9C9.29576 4.53324 9.54802 4.32276 9.72571 4.05447C9.90339 3.78618 9.99872 3.47179 10 3.15V3C10 2.46957 10.2107 1.96086 10.5858 1.58579C10.9609 1.21071 11.4696 1 12 1C12.5304 1 13.0391 1.21071 13.4142 1.58579C13.7893 1.96086 14 2.46957 14 3V3.09C14.0013 3.41179 14.0966 3.72618 14.2743 3.99447C14.452 4.26276 14.7042 4.47324 15 4.6C15.3016 4.73312 15.6362 4.77282 15.9606 4.714C16.285 4.65519 16.5843 4.50054 16.82 4.27L16.88 4.21C17.0657 4.02405 17.2863 3.87653 17.5291 3.77588C17.7719 3.67523 18.0322 3.62343 18.295 3.62343C18.5578 3.62343 18.8181 3.67523 19.0609 3.77588C19.3037 3.87653 19.5243 4.02405 19.71 4.21C19.896 4.39575 20.0435 4.61632 20.1441 4.85912C20.2448 5.10192 20.2966 5.36217 20.2966 5.625C20.2966 5.88783 20.2448 6.14808 20.1441 6.39088C20.0435 6.63368 19.896 6.85425 19.71 7.04L19.65 7.1C19.4195 7.33568 19.2648 7.63502 19.206 7.95941C19.1472 8.28381 19.1869 8.61838 19.32 8.92V9C19.4468 9.29576 19.6572 9.54802 19.9255 9.72571C20.1938 9.90339 20.5082 9.99872 20.83 10H21C21.5304 10 22.0391 10.2107 22.4142 10.5858C22.7893 10.9609 23 11.4696 23 12C23 12.5304 22.7893 13.0391 22.4142 13.4142C22.0391 13.7893 21.5304 14 21 14H20.91C20.5882 14.0013 20.2738 14.0966 20.0055 14.2743C19.7372 14.452 19.5268 14.7042 19.4 15Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                </span>
                <div>
                  <span className="btn-title">Configuración</span>
                  <span className="btn-subtitle">Ajustes del sistema</span>
                </div>
              </button>
            </div></>
          </div>
        </div>
  );
};

export default Dashboard;
