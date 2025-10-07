const API_BASE = 'http://localhost:8080/api'

export async function getAllPurchases() {
  const res = await fetch(`${API_BASE}/compras`)
  if (!res.ok) throw new Error('Error fetching purchases')
  return res.json()
}

export async function createPurchase(payload: any) {
  const res = await fetch(`${API_BASE}/compras`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
  if (!res.ok) {
    // Try to surface server error message if available
    let body = ''
    try {
      body = await res.text()
    } catch (e) {
      /* ignore */
    }
    const msg = body || `Error creating purchase (${res.status})`
    throw new Error(msg)
  }
  return res.json()
}

export async function deletePurchase(id: number) {
  const res = await fetch(`${API_BASE}/compras/${id}`, { method: 'DELETE' })
  if (!res.ok) throw new Error('Error deleting purchase')
  return true
}

export async function updatePurchase(id: number, payload: any) {
  const res = await fetch(`${API_BASE}/compras/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
  if (!res.ok) throw new Error('Error updating purchase')
  return res.json()
}

export default { getAllPurchases, createPurchase, deletePurchase, updatePurchase }
