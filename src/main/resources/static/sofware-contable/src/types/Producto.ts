export interface Producto {
  id?: number;
  nombre: string;
  tipo: string;
  cantidad: number;
  precio: number;
}

export interface ProductoFormData {
  nombre: string;
  tipo: string;
  cantidad: number;
  precio: number;
}

export interface ProductoStats {
  totalProductos: number;
  valorInventario: number;
  productosAgotados: number;
  categorias: number;
}
