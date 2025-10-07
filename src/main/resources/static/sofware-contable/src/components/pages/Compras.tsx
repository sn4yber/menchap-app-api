import React, { useState, useEffect } from 'react'
import PurchasesList from '../compras/PurchasesList'
import PurchaseForm from '../compras/PurchaseForm'
import { getAllPurchases } from '../../services/purchasesService'

const Compras: React.FC = () => {
		const [showForm, setShowForm] = useState(false)
		const [editing, setEditing] = useState<any>(null)
		const [purchases, setPurchases] = useState<any[]>([])
		const [loading, setLoading] = useState(true)
		const [search, setSearch] = useState('')

		useEffect(() => {
			loadPurchases()
		}, [])

		const loadPurchases = async () => {
			try {
				setLoading(true)
				const data = await getAllPurchases()
				setPurchases(data)
			} catch (err) {
				console.error('Error cargando compras', err)
			} finally {
				setLoading(false)
			}
		}

	const openCreate = () => {
		setEditing(null)
		setShowForm(true)
	}

	const openEdit = (item: any) => {
		setEditing(item)
		setShowForm(true)
	}

	const closeForm = () => {
		setShowForm(false)
		setEditing(null)
	}

		// Estadísticas simples a partir de compras cargadas
		const totalMes = purchases.reduce((sum, p) => {
			try {
				const fecha = p.fechaCompra ? new Date(p.fechaCompra) : null
				if (!fecha) return sum
				const now = new Date()
				if (fecha.getMonth() === now.getMonth() && fecha.getFullYear() === now.getFullYear()) {
					return sum + (Number(p.costoTotal) || 0)
				}
				return sum
			} catch (e) {
				return sum
			}
		}, 0)

		const facturasPendientes = purchases.filter(p => (p.metodoPago || '').toLowerCase().includes('pend')).length
		const facturasPagadas = purchases.filter(p => (p.metodoPago || '').toLowerCase().includes('pag')).length
		const nuevosProveedores = new Set(purchases.map(p => p.proveedor).filter(Boolean)).size

		const filtered = purchases.filter(p => {
			if (!search) return true
			return (p.nombreProducto || '').toLowerCase().includes(search.toLowerCase()) || (p.proveedor || '').toLowerCase().includes(search.toLowerCase())
		})

		return (
			<div className="content-area">
				<div className="page-header">
					<div>
						<h1 className="page-title">Compras</h1>
						<p className="page-subtitle">Registra y consulta las compras realizadas</p>
					</div>

					<div style={{display: 'flex', gap: 12, alignItems: 'center'}}>
						<div style={{minWidth: 320}} className="search-box">
							<svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
								<circle cx="11" cy="11" r="8" stroke="currentColor" strokeWidth="2"/>
								<path d="m21 21-4.35-4.35" stroke="currentColor" strokeWidth="2"/>
							</svg>
							<input className="search-input" placeholder="Buscar compra..." value={search} onChange={e => setSearch(e.target.value)} />
						</div>

						<button className="transparent-add-btn" onClick={openCreate} title="Agregar compra">
							<svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
								<rect x="3" y="3" width="18" height="18" rx="2" stroke="currentColor" strokeWidth="2" fill="none"/>
								<path d="M9 12h6" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
								<path d="M12 9v6" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
							</svg>
							<span className="btn-label">Nueva compra</span>
						</button>
					</div>
				</div>

				<div className="stats-grid" style={{marginBottom: 24}}>
					<div className="stat-card">
						<h3>Total Compras (Mes)</h3>
						<div className="stat-value">${totalMes.toFixed(2)}</div>
						<div className="stat-detail">Valor del mes actual</div>
					</div>
					<div className="stat-card">
						<h3>Facturas Pendientes</h3>
						<div className="stat-value">{facturasPendientes}</div>
						<div className="stat-detail">Por pagar</div>
					</div>
					<div className="stat-card">
						<h3>Facturas Pagadas</h3>
						<div className="stat-value">{facturasPagadas}</div>
						<div className="stat-detail">Pagadas</div>
					</div>
					<div className="stat-card">
						<h3>Nuevos Proveedores</h3>
						<div className="stat-value">{nuevosProveedores}</div>
						<div className="stat-detail">Proveedores únicos</div>
					</div>
				</div>

				<div className="filters-section">
					<div style={{flex: 1}} />
					<button className="filter-btn btn-secondary">Filtros</button>
				</div>

				<PurchasesList onEdit={openEdit} purchases={filtered} loading={loading} />

				{showForm && <PurchaseForm onClose={closeForm} editing={editing} onSaved={() => { loadPurchases(); }} />}
			</div>
		)
}

export default Compras
