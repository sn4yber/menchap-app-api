import React, { useState, useEffect } from 'react';
import { Producto } from '@/types';
import { productosApi } from '@/services/api';

const ProductosList: React.FC = () => {
  const [productos, setProductos] = useState<Producto[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Producto | null>(null);
  
  const [formData, setFormData] = useState<Omit<Producto, 'id'>>({
    nombre: '',
    tipo: '',
    cantidad: 0,
    precio: 0,
  });

  useEffect(() => {
    fetchProductos();
  }, []);

  const fetchProductos = async () => {
    try {
      setLoading(true);
      const response = await productosApi.getAll();
      setProductos(response.data);
    } catch (error) {
      console.error('Error fetching productos:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingProduct) {
        await productosApi.update(editingProduct.id!, { ...formData, id: editingProduct.id });
      } else {
        await productosApi.create(formData);
      }
      await fetchProductos();
      resetForm();
    } catch (error) {
      console.error('Error saving producto:', error);
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Está seguro de eliminar este producto?')) {
      try {
        await productosApi.delete(id);
        await fetchProductos();
      } catch (error) {
        console.error('Error deleting producto:', error);
      }
    }
  };

  const resetForm = () => {
    setFormData({ nombre: '', tipo: '', cantidad: 0, precio: 0 });
    setEditingProduct(null);
    setShowModal(false);
  };

  const handleEdit = (producto: Producto) => {
    setEditingProduct(producto);
    setFormData({
      nombre: producto.nombre,
      tipo: producto.tipo,
      cantidad: producto.cantidad,
      precio: producto.precio,
    });
    setShowModal(true);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Inventario de Productos</h2>
          <p className="text-gray-600">Gestiona tu inventario de productos</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="btn-primary flex items-center"
        >
          <span className="mr-2">➕</span>
          Nuevo Producto
        </button>
      </div>

      {/* Lista de productos */}
      <div className="card">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="table-header text-left">ID</th>
                <th className="table-header text-left">Nombre</th>
                <th className="table-header text-left">Tipo</th>
                <th className="table-header text-right">Cantidad</th>
                <th className="table-header text-right">Precio</th>
                <th className="table-header text-right">Valor Total</th>
                <th className="table-header text-center">Acciones</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {productos.map((producto) => (
                <tr key={producto.id} className="hover:bg-gray-50">
                  <td className="table-cell">{producto.id}</td>
                  <td className="table-cell font-medium">{producto.nombre}</td>
                  <td className="table-cell">
                    <span className="px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
                      {producto.tipo}
                    </span>
                  </td>
                  <td className="table-cell text-right">{producto.cantidad}</td>
                  <td className="table-cell text-right">
                    ${producto.precio.toLocaleString('es-ES', { minimumFractionDigits: 2 })}
                  </td>
                  <td className="table-cell text-right font-semibold">
                    ${(producto.cantidad * producto.precio).toLocaleString('es-ES', { minimumFractionDigits: 2 })}
                  </td>
                  <td className="table-cell text-center">
                    <div className="flex justify-center space-x-2">
                      <button
                        onClick={() => handleEdit(producto)}
                        className="text-primary-600 hover:text-primary-900 font-medium"
                      >
                        ✏️ Editar
                      </button>
                      <button
                        onClick={() => handleDelete(producto.id!)}
                        className="text-red-600 hover:text-red-900 font-medium"
                      >
                        🗑️ Eliminar
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          
          {productos.length === 0 && (
            <div className="text-center py-12">
              <div className="text-gray-400 text-6xl mb-4">📦</div>
              <h3 className="text-lg font-medium text-gray-900 mb-2">No hay productos</h3>
              <p className="text-gray-500">Comienza agregando tu primer producto al inventario.</p>
            </div>
          )}
        </div>
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg font-bold text-gray-900 mb-4">
                {editingProduct ? 'Editar Producto' : 'Nuevo Producto'}
              </h3>
              
              <form onSubmit={handleSubmit}>
                <div className="mb-4">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Nombre del Producto
                  </label>
                  <input
                    type="text"
                    value={formData.nombre}
                    onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                    className="input-field"
                    required
                  />
                </div>
                
                <div className="mb-4">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Tipo/Categoría
                  </label>
                  <input
                    type="text"
                    value={formData.tipo}
                    onChange={(e) => setFormData({ ...formData, tipo: e.target.value })}
                    className="input-field"
                    required
                  />
                </div>
                
                <div className="mb-4">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Cantidad
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.cantidad}
                    onChange={(e) => setFormData({ ...formData, cantidad: parseFloat(e.target.value) || 0 })}
                    className="input-field"
                    required
                  />
                </div>
                
                <div className="mb-6">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Precio Unitario
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.precio}
                    onChange={(e) => setFormData({ ...formData, precio: parseFloat(e.target.value) || 0 })}
                    className="input-field"
                    required
                  />
                </div>
                
                <div className="flex justify-end space-x-3">
                  <button
                    type="button"
                    onClick={resetForm}
                    className="btn-secondary"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="btn-primary"
                  >
                    {editingProduct ? 'Actualizar' : 'Crear'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProductosList;
