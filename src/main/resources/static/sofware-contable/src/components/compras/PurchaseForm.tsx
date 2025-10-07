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
    } else {
      setForm(emptyForm)
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
            <div className="form-row">
              <div style={{position: 'relative'}}>
                <label className="form-label">Producto</label>
                <input className="form-input" name="productoId" value={productQuery || form.productoId} onChange={e => { setProductQuery(e.target.value); setShowDropdown(true); }} onBlur={() => setTimeout(() => setShowDropdown(false), 150)} onFocus={() => setShowDropdown(true)} placeholder="Buscar producto por nombre o id" />
                {showDropdown && filteredProducts.length > 0 && (
                  <div style={{position: 'absolute', background: 'white', border: '1px solid #e2e8f0', borderRadius: 8, marginTop: 8, zIndex: 1200, left: 0, right: 0}}>
                    {filteredProducts.map(p => (
                      <div key={p.id} style={{padding: 8, cursor: 'pointer'}} onMouseDown={() => handleSelectProduct(p)}>
                        <div style={{fontWeight: 600}}>{p.nombre}</div>
                        <div style={{fontSize: 12, color: '#718096'}}>#{p.id} • {p.tipo}</div>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              <div>
                <label className="form-label">Nombre producto</label>
                <input className="form-input" name="nombreProducto" value={form.nombreProducto} onChange={handleChange} />
              </div>
            </div>

            <div className="form-row" style={{marginTop: 12}}>
              <div>
                <label className="form-label">Cantidad</label>
                <input className="form-input" type="number" name="cantidad" value={form.cantidad} onChange={handleChange} />
              </div>
              <div>
                <label className="form-label">Costo unitario</label>
                <input className="form-input" type="number" step="0.01" name="costoUnitario" value={form.costoUnitario} onChange={handleChange} />
              </div>
            </div>

            <div className="form-row" style={{marginTop: 12}}>
              <div>
                <label className="form-label">Proveedor</label>
                <input className="form-input" name="proveedor" value={form.proveedor} onChange={handleChange} />
              </div>
              <div>
                <label className="form-label">Método pago</label>
                <input className="form-input" name="metodoPago" value={form.metodoPago} onChange={handleChange} />
              </div>
            </div>

            <div style={{marginTop: 12}}>
              <label className="form-label">Número factura</label>
              <input className="form-input" name="numeroFactura" value={form.numeroFactura} onChange={handleChange} />
            </div>

            <div style={{marginTop: 12}}>
              <label className="form-label">Observaciones</label>
              <textarea className="form-input" style={{height: 100}} name="observaciones" value={form.observaciones} onChange={handleChange} />
            </div>

            <div className="modal-actions" style={{display: 'flex', justifyContent: 'flex-end', gap: 12, padding: '16px 24px'}}>
              <button type="button" className="btn secondary" onClick={onClose}>Cancelar</button>
              <button type="submit" className="btn primary" disabled={submitting}>{submitting ? 'Guardando...' : 'Guardar'}</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default PurchaseForm
