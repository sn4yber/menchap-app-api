import type { Producto, ProductoFormData, ProductoStats } from '../types/Producto';

const API_BASE_URL = 'http://localhost:8080/api';

const RETRY_CONFIG = {
  maxRetries: 3,
  baseDelay: 1000, // 1 segundo
  maxDelay: 5000   // 5 segundos
};

class InventarioServiceError extends Error {
  constructor(message: string, public status?: number) {
    super(message);
    this.name = 'InventarioServiceError';
  }
}

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

const exponentialBackoff = (retry: number): number => {
  const backoff = Math.min(
    RETRY_CONFIG.maxDelay,
    RETRY_CONFIG.baseDelay * Math.pow(2, retry)
  );
  return backoff * (0.75 + Math.random() * 0.5); // Añade un jitter del 75-125%
};

export const inventarioService = {
  async fetchWithRetry<T>(url: string, options: RequestInit = {}): Promise<T> {
    let lastError: Error | undefined;

    for (let retry = 0; retry < RETRY_CONFIG.maxRetries; retry++) {
      try {
        const response = await fetch(url, {
          ...options,
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json',
            ...options.headers,
          },
        });

        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}));
          throw new Error(errorData.message || 'Error en la operación');
        }

        return await response.json();
      } catch (error) {
        lastError = error as Error;
        console.error(`Intento ${retry + 1} fallido:`, error);
        
        if (retry < RETRY_CONFIG.maxRetries - 1) {
          const backoff = Math.min(
            RETRY_CONFIG.maxDelay,
            RETRY_CONFIG.baseDelay * Math.pow(2, retry)
          ) * (0.75 + Math.random() * 0.5);
          
          await new Promise(resolve => setTimeout(resolve, backoff));
        }
      }
    }

    throw lastError || new Error('Error desconocido en el servicio de inventario');
  },

  // Obtener todos los productos
  async obtenerProductos(): Promise<Producto[]> {
    return this.fetchWithRetry<Producto[]>(`${API_BASE_URL}/inventario`);
  },

  // Crear nuevo producto
  async crearProducto(producto: ProductoFormData): Promise<Producto> {
    return this.fetchWithRetry<Producto>(`${API_BASE_URL}/inventario`, {
      method: 'POST',
      body: JSON.stringify(producto),
    });
  },

  // Actualizar producto existente
  async actualizarProducto(id: number, producto: ProductoFormData): Promise<Producto> {
    return this.fetchWithRetry<Producto>(`${API_BASE_URL}/inventario/${id}`, {
      method: 'PUT',
      body: JSON.stringify(producto),
    });
  },

  // Eliminar producto
  async eliminarProducto(id: number): Promise<void> {
    await this.fetchWithRetry<void>(`${API_BASE_URL}/inventario/${id}`, {
      method: 'DELETE',
    });
  },

  // Calcular estadísticas del inventario
  calcularEstadisticas(productos: Producto[]): ProductoStats {
    const totalProductos = productos.length;
    const valorInventario = productos.reduce((total, producto) => 
      total + (producto.cantidad * producto.precio), 0);
    const productosAgotados = productos.filter(producto => producto.cantidad === 0).length;
    const categorias = new Set(productos.map(producto => producto.tipo)).size;

    return {
      totalProductos,
      valorInventario,
      productosAgotados,
      categorias
    };
  }
};
