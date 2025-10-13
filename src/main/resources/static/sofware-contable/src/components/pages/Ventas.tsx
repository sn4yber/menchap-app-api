import React, { useState, useEffect } from 'react';
import VentasList from '../ventas/VentasList';
import VentaForm from '../ventas/VentaForm';
import { getAllVentas, getVentasHoy, type Venta } from '../../services/ventasService';

const Ventas: React.FC = () => {
  const [showForm, setShowForm] = useState(false);
  const [ventas, setVentas] = useState<Venta[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [filter, setFilter] = useState<'todas' | 'hoy'>('todas');

  useEffect(() => {
    loadVentas();
  }, [filter]);

  const loadVentas = async () => {
    try {
      setLoading(true);
      const data = filter === 'hoy' ? await getVentasHoy() : await getAllVentas();
      setVentas(data);
    } catch (err) {
      console.error('Error cargando ventas', err);
    } finally {
      setLoading(false);
    }
  };

  const openForm = () => {
    setShowForm(true);
  };

  const closeForm = () => {
    setShowForm(false);
  };

  const handleVentaCompleted = () => {
    loadVentas();
  };

  // Calcular estadísticas
  const totalVentas = ventas.length;
  const totalIngresos = ventas.reduce((total, v) => total + Number(v.precioTotal || 0), 0);
  const totalGanancias = ventas.reduce((total, v) => total + Number(v.ganancia || 0), 0);
  const ventasHoy = ventas.filter(v => {
    if (!v.fechaVenta) return false;
    const fecha = new Date(v.fechaVenta);
    const hoy = new Date();
    return fecha.toDateString() === hoy.toDateString();
  }).length;

  const formatPrice = (price: number): string => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
      maximumFractionDigits: 2
    }).format(price);
  };

  // Filtrar ventas
  const filteredVentas = ventas.filter(v => {
    if (!search) return true;
    return (
      (v.nombreProducto || '').toLowerCase().includes(search.toLowerCase()) ||
      (v.cliente || '').toLowerCase().includes(search.toLowerCase()) ||
      v.id?.toString().includes(search)
    );
  });

  return (
    <div className="content-area">
      <div className="page-header">
        <div>
          <h1 className="page-title">Ventas</h1>
          <p className="page-subtitle">Gestiona las ventas y genera tickets de facturación</p>
        </div>

        <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
          <div style={{ minWidth: 320 }} className="search-box">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="11" cy="11" r="8" stroke="currentColor" strokeWidth="2" />
              <path d="m21 21-4.35-4.35" stroke="currentColor" strokeWidth="2" />
            </svg>
            <input
              className="search-input"
              placeholder="Buscar venta..."
              value={search}
              onChange={e => setSearch(e.target.value)}
            />
          </div>

          <button className="transparent-add-btn" onClick={openForm} title="Nueva venta">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="3" y="3" width="18" height="18" rx="2" stroke="currentColor" strokeWidth="2" fill="none" />
              <path d="M9 12h6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
              <path d="M12 9v6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
            </svg>
            <span className="btn-label">Nueva venta</span>
          </button>
        </div>
      </div>

      {/* Estadísticas */}
      <div className="stats-grid" style={{ marginBottom: 24 }}>
        <div className="stat-card">
          <h3>Total Ventas {filter === 'hoy' ? '(Hoy)' : ''}</h3>
          <div className="stat-value">{totalVentas}</div>
          <div className="stat-detail">Transacciones registradas</div>
        </div>

        <div className="stat-card">
          <h3>Ingresos {filter === 'hoy' ? '(Hoy)' : ''}</h3>
          <div className="stat-value">{formatPrice(totalIngresos)}</div>
          <div className="stat-detail">Total recaudado</div>
        </div>

        <div className="stat-card">
          <h3>Ganancias {filter === 'hoy' ? '(Hoy)' : ''}</h3>
          <div className="stat-value">{formatPrice(totalGanancias)}</div>
          <div className="stat-detail">Margen de ganancia</div>
        </div>

        <div className="stat-card">
          <h3>Ventas Hoy</h3>
          <div className="stat-value">{filter === 'todas' ? ventasHoy : totalVentas}</div>
          <div className="stat-detail">Transacciones del día</div>
        </div>
      </div>

      {/* Filtros */}
      <div className="filters-section" style={{ marginBottom: 16 }}>
        <div style={{ display: 'flex', gap: 8 }}>
          <button
            className={`filter-btn ${filter === 'todas' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setFilter('todas')}
          >
            Todas las ventas
          </button>
          <button
            className={`filter-btn ${filter === 'hoy' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setFilter('hoy')}
          >
            Ventas de hoy
          </button>
        </div>
      </div>

      {/* Lista de ventas */}
      <VentasList ventas={filteredVentas} loading={loading} onRefresh={loadVentas} />

      {/* Modal de nueva venta */}
      {showForm && (
        <VentaForm onClose={closeForm} onVentaCompleted={handleVentaCompleted} />
      )}
    </div>
  );
};

export default Ventas;
