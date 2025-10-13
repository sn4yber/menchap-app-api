const API_BASE_URL = 'http://localhost:8080/api';

export interface Venta {
  id?: number;
  productoId: number;
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  precioTotal: number;
  costoUnitario?: number;
  ganancia?: number;
  cliente?: string;
  metodoPago?: string;
  fechaVenta?: string;
  observaciones?: string;
}

export interface VentaItem {
  productoId: number;
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export const getAllVentas = async (): Promise<Venta[]> => {
  const response = await fetch(`${API_BASE_URL}/ventas`);
  if (!response.ok) throw new Error('Error al obtener ventas');
  return response.json();
};

export const getVentasHoy = async (): Promise<Venta[]> => {
  const response = await fetch(`${API_BASE_URL}/ventas/hoy`);
  if (!response.ok) throw new Error('Error al obtener ventas de hoy');
  return response.json();
};

export const createVenta = async (venta: Partial<Venta>): Promise<Venta> => {
  const response = await fetch(`${API_BASE_URL}/ventas`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(venta),
  });
  if (!response.ok) {
    const error = await response.text();
    throw new Error(error || 'Error al crear venta');
  }
  return response.json();
};

export const updateVenta = async (id: number, venta: Partial<Venta>): Promise<Venta> => {
  const response = await fetch(`${API_BASE_URL}/ventas/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(venta),
  });
  if (!response.ok) {
    const error = await response.text();
    throw new Error(error || 'Error al actualizar venta');
  }
  return response.json();
};

export const deleteVenta = async (id: number): Promise<void> => {
  const response = await fetch(`${API_BASE_URL}/ventas/${id}`, {
    method: 'DELETE',
  });
  if (!response.ok) throw new Error('Error al eliminar venta');
};
