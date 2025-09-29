import type { Producto, ProductoFormData, ProductoStats } from '../types/Producto';

const API_BASE_URL = 'http://localhost:8080/api';

export const inventarioService = {
  // Obtener todos los productos
  async obtenerProductos(): Promise<Producto[]> {
    try {
      const response = await fetch(`${API_BASE_URL}/inventario`);
      if (!response.ok) {
        throw new Error('Error al obtener productos');
      }
      return await response.json();
    } catch (error) {
      console.error('Error obteniendo productos:', error);
      throw error;
    }
  },

  // Crear nuevo producto
  async crearProducto(producto: ProductoFormData): Promise<Producto> {
    try {
      const response = await fetch(`${API_BASE_URL}/inventario`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(producto),
      });
      if (!response.ok) {
        throw new Error('Error al crear producto');
      }
      return await response.json();
    } catch (error) {
      console.error('Error creando producto:', error);
      throw error;
    }
  },

  // Actualizar producto existente
  async actualizarProducto(id: number, producto: ProductoFormData): Promise<Producto> {
    try {
      const response = await fetch(`${API_BASE_URL}/inventario/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(producto),
      });
      if (!response.ok) {
        throw new Error('Error al actualizar producto');
      }
      return await response.json();
    } catch (error) {
      console.error('Error actualizando producto:', error);
      throw error;
    }
  },

  // Eliminar producto
  async eliminarProducto(id: number): Promise<void> {
    try {
      const response = await fetch(`${API_BASE_URL}/inventario/${id}`, {
        method: 'DELETE',
      });
      if (!response.ok) {
        throw new Error('Error al eliminar producto');
      }
    } catch (error) {
      console.error('Error eliminando producto:', error);
      throw error;
    }
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
