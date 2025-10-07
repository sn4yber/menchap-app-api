import React, { useEffect, useState } from 'react'
import { getAllPurchases, deletePurchase } from '../../services/purchasesService'

interface Purchase {
  id: number
  productoId?: number
  nombreProducto?: string
  cantidad?: number
  costoUnitario?: number
  costoTotal?: number
  proveedor?: string
  metodoPago?: string
  fechaCompra?: string
  numeroFactura?: string
  observaciones?: string
}

const PurchasesList: React.FC<{ onEdit?: (p: Purchase) => void, purchases?: Purchase[], loading?: boolean }> = ({ onEdit, purchases: propsPurchases, loading: propsLoading }) => {
  // Hooks: always declared in the same order
  const [purchases, setPurchases] = useState<Purchase[]>(propsPurchases || [])
  const [loading, setLoading] = useState<boolean>(propsLoading ?? true)
  const [error, setError] = useState<string | null>(null)
  const [page, setPage] = useState(1)

  useEffect(() => {
    if (propsPurchases) {
      setPurchases(propsPurchases)
      setLoading(propsLoading ?? false)
    } else {
      fetchPurchases()
    }
    // reset page when purchases prop changes
    setPage(1)
  }, [propsPurchases])

  const fetchPurchases = async () => {
    try {
      setLoading(true)
      const data = await getAllPurchases()
      setPurchases(data)
      setError(null)
    } catch (err) {
      setError('Error cargando compras')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id?: number) => {
    if (!id) return
    if (!window.confirm('¿Eliminar esta compra?')) return
    try {
      await deletePurchase(id)
      fetchPurchases()
    } catch (err) {
      setError('Error eliminando compra')
      console.error(err)
    }
  }

  const pageSize = 10
  const total = purchases.length
  const totalPages = Math.max(1, Math.ceil(total / pageSize))
  const pageItems = purchases.slice((page - 1) * pageSize, page * pageSize)

  if (loading) return <div className="loading-container"><div className="loading-spinner"></div><p>Cargando compras...</p></div>

  return (
    <div className="table-container">
      {error && <div className="error-banner">{error}</div>}
      <table className="products-table">
        <thead>
          <tr>
            <th>Fecha</th>
            <th>Producto</th>
            <th>Cantidad</th>
            <th>Costo unit.</th>
            <th>Costo total</th>
            <th>Proveedor</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {purchases.length === 0 && (
            <tr><td colSpan={7} className="empty-state-row">No hay compras</td></tr>
          )}
          {pageItems.map(p => (
            <tr key={p.id}>
              <td>{p.fechaCompra ? new Date(p.fechaCompra).toLocaleString() : ''}</td>
              <td>{p.nombreProducto || `#${p.productoId}`}</td>
              <td>{p.cantidad ?? ''}</td>
              <td>{p.costoUnitario != null ? `$${Number(p.costoUnitario).toFixed(2)}` : ''}</td>
              <td>{p.costoTotal != null ? `$${Number(p.costoTotal).toFixed(2)}` : ''}</td>
              <td>{p.proveedor || ''}</td>
              <td>
                <div className="action-buttons">
                  <button className="btn-icon edit" onClick={() => onEdit && onEdit(p)} title="Editar">
                    ✎
                  </button>
                  <button className="btn-icon delete" onClick={() => handleDelete(p.id)} title="Eliminar">
                    🗑
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: 12}}>
        <div style={{color: '#718096'}}>Mostrando {Math.min((page-1)*pageSize+1, total)}-{Math.min(page*pageSize, total)} de {total} resultados</div>
        <div style={{display: 'flex', gap: 8, alignItems: 'center'}}>
          <button className="btn-secondary" onClick={() => setPage(Math.max(1, page-1))} disabled={page===1}>‹</button>
          {[...Array(totalPages)].map((_, i) => (
            <button key={i} className={`btn ${i+1===page ? 'btn-primary' : 'btn-secondary'}`} onClick={() => setPage(i+1)}>{i+1}</button>
          ))}
          <button className="btn-secondary" onClick={() => setPage(Math.min(totalPages, page+1))} disabled={page===totalPages}>›</button>
        </div>
      </div>
    </div>
  )
}

export default PurchasesList
