import React, { useEffect, useState } from 'react'
import { createPurchase, updatePurchase } from '../../services/purchasesService'
import { inventarioService } from '../../services/inventarioService'

const emptyForm = {
  productoId: '',
  nombreProducto: '',
  cantidad: 1,
  costoUnitario: 0,
  proveedor: '',
  metodoPago: '',
  numeroFactura: '',
  observaciones: ''
}

type Props = {
  onClose: () => void
  editing?: any
  onSaved?: () => void
}

const PurchaseForm: React.FC<Props> = ({ onClose, editing, onSaved }) => {
  const [form, setForm] = useState<any>(emptyForm)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [products, setProducts] = useState<any[]>([])
  const [productQuery, setProductQuery] = useState('')
  const [showDropdown, setShowDropdown] = useState(false)

  useEffect(() => {
    if (editing) {
      setForm({ ...editing })
      setProductQuery(editing.nombreProducto || '')
    } else {
      setForm(emptyForm)
      setProductQuery('')
    }
    // load products for selector
    inventarioService.obtenerProductos().then(p => setProducts(p)).catch(e => console.error('Error loading products', e))
  }, [editing])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setForm((s: any) => ({ ...s, [name]: name === 'cantidad' || name === 'costoUnitario' ? Number(value) : value }))
  }

  const filteredProducts = products.filter(p => {
    if (!productQuery) return true
    return (p.nombre || '').toLowerCase().includes(productQuery.toLowerCase()) || String(p.id).includes(productQuery)
  }).slice(0, 10)

  const handleSelectProduct = (p: any) => {
    setForm((s: any) => ({ ...s, productoId: p.id, nombreProducto: p.nombre }))
    setProductQuery(p.nombre)
    setShowDropdown(false)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setSubmitting(true)
    try {
      // Basic client-side validation
      if (!form.productoId || !form.nombreProducto) {
        setError('Producto (ID) y nombre son obligatorios')
        setSubmitting(false)
        return
      }

      const productoIdNum = Number(form.productoId)
      const cantidadNum = Number(form.cantidad)
      const costoUnitarioNum = Number(form.costoUnitario)

      if (isNaN(productoIdNum) || productoIdNum <= 0) {
        setError('Producto ID inválido. Debe ser un número mayor a 0')
        setSubmitting(false)
        return
      }

      if (isNaN(cantidadNum) || cantidadNum <= 0) {
        setError('Cantidad inválida. Debe ser un número mayor a 0')
        setSubmitting(false)
        return
      }

      if (isNaN(costoUnitarioNum) || costoUnitarioNum < 0) {
        setError('Costo unitario inválido')
        setSubmitting(false)
        return
      }

      // Convert fields to expected types
      const payload = {
        ...form,
        productoId: productoIdNum,
        cantidad: cantidadNum,
        costoUnitario: costoUnitarioNum,
        costoTotal: form.costoTotal != null ? Number(form.costoTotal) : undefined
      }

      if (editing && editing.id) {
        await updatePurchase(editing.id, payload)
      } else {
        await createPurchase(payload)
      }
      // Notify other parts of the app (inventory) that purchases changed
      try {
        window.dispatchEvent(new CustomEvent('inventario:updated'))
      } catch (e) {
        // ignore if environment doesn't support CustomEvent
      }
      setError(null)
      onClose()
      if (onSaved) onSaved()
    } catch (err: any) {
      const msg = err?.message || 'Error guardando compra'
      setError(msg)
      console.error('Error creating purchase', err)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h2>{editing ? 'Editar compra' : 'Nueva compra'}</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        {error && <div className="error-message" style={{margin: '0 24px 12px'}}>{error}</div>}

        <div className="producto-form">
          <form onSubmit={handleSubmit}>
            <div style={{position: 'relative', marginBottom: 16}}>
              <label className="form-label">Producto *</label>
              <input 
                className="form-input" 
                name="productoId" 
                value={productQuery || form.nombreProducto || form.productoId} 
                onChange={e => { 
                  setProductQuery(e.target.value); 
                  setShowDropdown(true); 
                }} 
                onBlur={() => setTimeout(() => setShowDropdown(false), 150)} 
                onFocus={() => setShowDropdown(true)} 
                placeholder="Buscar producto por nombre o id" 
              />
              {showDropdown && filteredProducts.length > 0 && (
                <div style={{position: 'absolute', background: 'white', border: '1px solid #e2e8f0', borderRadius: 8, marginTop: 4, zIndex: 1200, left: 0, right: 0, maxHeight: 200, overflow: 'auto', boxShadow: '0 4px 6px rgba(0,0,0,0.1)'}}>
                  {filteredProducts.map(p => (
                    <div 
                      key={p.id} 
                      style={{padding: '12px', cursor: 'pointer', borderBottom: '1px solid #f1f5f9'}} 
                      onMouseDown={() => handleSelectProduct(p)}
                      onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#f8fafc'}
                      onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'white'}
                    >
                      <div style={{fontWeight: 600, color: '#1e293b'}}>{p.nombre}</div>
                      <div style={{fontSize: 12, color: '#64748b', marginTop: 2}}>ID: {p.id} • Categoría: {p.tipo} • Precio: ${Number(p.precio).toFixed(2)}</div>
                    </div>
                  ))}
                </div>
              )}
              {form.productoId && (
                <div style={{fontSize: 12, color: '#64748b', marginTop: 4}}>
                  Producto seleccionado: {form.nombreProducto} (ID: {form.productoId})
                </div>
              )}
            </div>

            <div className="form-row" style={{marginBottom: 16}}>
              <div>
                <label className="form-label">Cantidad *</label>
                <input 
                  className="form-input" 
                  type="number" 
                  name="cantidad" 
                  value={form.cantidad} 
                  onChange={handleChange}
                  min="1"
                  required
                />
              </div>
              <div>
                <label className="form-label">Costo unitario *</label>
                <input 
                  className="form-input" 
                  type="number" 
                  step="0.01" 
                  name="costoUnitario" 
                  value={form.costoUnitario} 
                  onChange={handleChange}
                  min="0"
                  required
                />
              </div>
            </div>

            <div style={{marginBottom: 16}}>
              <label className="form-label">Costo total</label>
              <div className="form-input" style={{backgroundColor: '#f8fafc', color: '#475569', fontWeight: 600}}>
                ${(Number(form.cantidad) * Number(form.costoUnitario)).toFixed(2)}
              </div>
              <div style={{fontSize: 12, color: '#64748b', marginTop: 4}}>
                Calculado automáticamente: Cantidad × Costo unitario
              </div>
            </div>

            <div style={{marginBottom: 16}}>
              <label className="form-label">Proveedor</label>
              <input 
                className="form-input" 
                name="proveedor" 
                value={form.proveedor} 
                onChange={handleChange}
                placeholder="Nombre del proveedor"
              />
            </div>

            <div className="form-row" style={{marginBottom: 16}}>
              <div>
                <label className="form-label">Método de pago</label>
                <select 
                  className="form-input" 
                  name="metodoPago" 
                  value={form.metodoPago} 
                  onChange={handleChange}
                >
                  <option value="">Seleccionar...</option>
                  <option value="Efectivo">Efectivo</option>
                  <option value="Transferencia">Transferencia</option>
                  <option value="Tarjeta">Tarjeta</option>
                  <option value="Crédito">Crédito</option>
                  <option value="Pagado">Pagado</option>
                  <option value="Pendiente">Pendiente</option>
                </select>
              </div>
              <div>
                <label className="form-label">Número de factura</label>
                <input 
                  className="form-input" 
                  name="numeroFactura" 
                  value={form.numeroFactura} 
                  onChange={handleChange}
                  placeholder="Ej: FAC-001"
                />
              </div>
            </div>

            <div style={{marginBottom: 16}}>
              <label className="form-label">Observaciones</label>
              <textarea 
                className="form-input" 
                style={{height: 80, resize: 'vertical'}} 
                name="observaciones" 
                value={form.observaciones} 
                onChange={handleChange}
                placeholder="Notas adicionales sobre la compra..."
              />
            </div>

            <div className="modal-actions" style={{display: 'flex', justifyContent: 'flex-end', gap: 12, padding: '16px 24px', borderTop: '1px solid #e2e8f0', marginTop: 20}}>
              <button type="button" className="btn secondary" onClick={onClose}>Cancelar</button>
              <button type="submit" className="btn primary" disabled={submitting}>
                {submitting ? 'Guardando...' : (editing ? 'Actualizar compra' : 'Registrar compra')}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default PurchaseForm
