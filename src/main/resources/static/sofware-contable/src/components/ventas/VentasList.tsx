import React, { useState, useEffect } from 'react';
import { getAllVentas, deleteVenta, type Venta } from '../../services/ventasService';

interface Props {
  ventas?: Venta[];
  loading?: boolean;
  onRefresh?: () => void;
}

const VentasList: React.FC<Props> = ({ ventas: propsVentas, loading: propsLoading, onRefresh }) => {
  const [ventas, setVentas] = useState<Venta[]>(propsVentas || []);
  const [loading, setLoading] = useState(propsLoading ?? true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);

  useEffect(() => {
    if (propsVentas) {
      setVentas(propsVentas);
      setLoading(propsLoading ?? false);
    } else {
      fetchVentas();
    }
    setPage(1);
  }, [propsVentas]);

  const fetchVentas = async () => {
    try {
      setLoading(true);
      const data = await getAllVentas();
      setVentas(data);
      setError(null);
    } catch (err) {
      setError('Error cargando ventas');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id?: number) => {
    if (!id) return;
    if (!window.confirm('¿Eliminar esta venta? Esto restaurará el stock del producto.')) return;
    
    try {
      await deleteVenta(id);
      if (onRefresh) {
        onRefresh();
      } else {
        fetchVentas();
      }
      window.dispatchEvent(new CustomEvent('inventario:updated'));
    } catch (err) {
      setError('Error eliminando venta');
      console.error(err);
    }
  };

  const formatPrice = (price: number): string => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
      maximumFractionDigits: 2
    }).format(price);
  };

  const formatDate = (date: string): string => {
    return new Date(date).toLocaleString('es-CO', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const pageSize = 10;
  const total = ventas.length;
  const totalPages = Math.max(1, Math.ceil(total / pageSize));
  const pageItems = ventas.slice((page - 1) * pageSize, page * pageSize);

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Cargando ventas...</p>
      </div>
    );
  }

  return (
    <div className="table-container">
      {error && (
        <div className="error-banner" style={{ marginBottom: 16 }}>
          {error}
        </div>
      )}

      <table className="products-table">
        <thead>
          <tr>
            <th>Fecha</th>
            <th>Ticket</th>
            <th>Producto</th>
            <th>Cliente</th>
            <th>Cantidad</th>
            <th>Precio Unit.</th>
            <th>Total</th>
            <th>Método Pago</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {ventas.length === 0 && (
            <tr>
              <td colSpan={9} className="empty-state-row">
                <div style={{ textAlign: 'center', padding: 40 }}>
                  <div style={{ fontSize: 48, marginBottom: 12 }}>🛒</div>
                  <p>No hay ventas registradas</p>
                  <small>Comienza a vender productos para ver el historial aquí</small>
                </div>
              </td>
            </tr>
          )}
          {pageItems.map(v => (
            <tr key={v.id}>
              <td>{v.fechaVenta ? formatDate(v.fechaVenta) : ''}</td>
              <td style={{ fontFamily: 'monospace', fontWeight: 600 }}>
                #{v.id?.toString().padStart(6, '0')}
              </td>
              <td>{v.nombreProducto}</td>
              <td>{v.cliente || 'Cliente General'}</td>
              <td style={{ textAlign: 'center' }}>{v.cantidad}</td>
              <td style={{ textAlign: 'right' }}>
                {formatPrice(Number(v.precioUnitario))}
              </td>
              <td style={{ textAlign: 'right', fontWeight: 600, color: '#059669' }}>
                {formatPrice(Number(v.precioTotal))}
              </td>
              <td>
                <span style={{
                  padding: '4px 8px',
                  borderRadius: 4,
                  fontSize: 12,
                  backgroundColor: v.metodoPago === 'Efectivo' ? '#dbeafe' : 
                                  v.metodoPago === 'Tarjeta' ? '#fce7f3' : '#e0e7ff',
                  color: v.metodoPago === 'Efectivo' ? '#1e40af' : 
                         v.metodoPago === 'Tarjeta' ? '#9f1239' : '#3730a3'
                }}>
                  {v.metodoPago || 'N/A'}
                </span>
              </td>
              <td>
                <div className="action-buttons">
                  <button
                    className="btn-icon delete"
                    onClick={() => handleDelete(v.id)}
                    title="Eliminar"
                  >
                    🗑️
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {total > pageSize && (
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          padding: 12
        }}>
          <div style={{ color: '#718096' }}>
            Mostrando {Math.min((page - 1) * pageSize + 1, total)}-{Math.min(page * pageSize, total)} de {total} resultados
          </div>
          <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
            <button
              className="btn-secondary"
              onClick={() => setPage(Math.max(1, page - 1))}
              disabled={page === 1}
            >
              ‹
            </button>
            {[...Array(totalPages)].map((_, i) => (
              <button
                key={i}
                className={`btn ${i + 1 === page ? 'btn-primary' : 'btn-secondary'}`}
                onClick={() => setPage(i + 1)}
              >
                {i + 1}
              </button>
            ))}
            <button
              className="btn-secondary"
              onClick={() => setPage(Math.min(totalPages, page + 1))}
              disabled={page === totalPages}
            >
              ›
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default VentasList;
