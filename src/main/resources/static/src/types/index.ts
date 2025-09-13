// Tipos para el modelo de datos

export interface Producto {
  id?: number;
  nombre: string;
  tipo: string;
  cantidad: number;
  precio: number;
  precioTotal?: number;
}

export interface Venta {
  id?: number;
  productoId: number;
  nombreProducto?: string;
  cantidad: number;
  precioUnitario: number;
  precioTotal?: number;
  cliente: string;
  metodoPago: string;
  fechaVenta?: string;
  ganancia?: number;
}

export interface Compra {
  id?: number;
  productoId: number;
  nombreProducto?: string;
  cantidad: number;
  costoUnitario: number;
  costoTotal?: number;
  proveedor: string;
  numeroFactura: string;
  metodoPago: string;
  fechaCompra?: string;
}

export interface DashboardStats {
  totalVentas: number;
  totalCompras: number;
  ganancias: number;
  productosEnStock: number;
}

export interface ApiResponse<T> {
  data?: T;
  error?: string;
  message?: string;
}

// Tipos para autenticación
export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  success: boolean;
  message: string;
  usuario?: UsuarioResponse;
}

export interface UsuarioResponse {
  id: number;
  username: string;
  email?: string;
  nombreCompleto?: string;
  rol?: string;
  activo: boolean;
  fechaCreacion?: string;
  fechaUltimoAcceso?: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email?: string;
  nombreCompleto?: string;
}
