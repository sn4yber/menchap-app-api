import React, { useState, useEffect } from 'react';

interface Producto {
  id?: number;
  nombre: string;
  tipo: string;
  cantidad: number;
  precio: number;
}

interface ProductoFormData {
  nombre: string;
  tipo: string;
  cantidad: number;
  precio: number;
}

interface FormErrors {
  nombre?: string;
  tipo?: string;
  cantidad?: string;
  precio?: string;
}

const Inventario: React.FC = () => {
  const [productos, setProductos] = useState<Producto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Producto | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterCategory, setFilterCategory] = useState('');

  // Estados del formulario
  const [formData, setFormData] = useState<ProductoFormData>({
    nombre: '',
    tipo: '',
    cantidad: 0,
    precio: 0
  });
  const [formErrors, setFormErrors] = useState<FormErrors>({});
  const [submitting, setSubmitting] = useState(false);

  const API_BASE_URL = 'http://localhost:8080/api';

  // Función para formatear precios con decimales
  const formatPrice = (price: number): string => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
      maximumFractionDigits: 2
    }).format(price);
  };

  // Cargar productos al montar el componente
  useEffect(() => {
    cargarProductos();
  }, []);

  const cargarProductos = async () => {
    try {
      setLoading(true);
      const response = await fetch(`${API_BASE_URL}/inventario`);
      if (!response.ok) {
        throw new Error('Error al cargar productos');
      }
      const data = await response.json();
      setProductos(data);
      setError(null);
    } catch (err) {
      setError('Error al cargar los productos');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({
      nombre: '',
      tipo: '',
      cantidad: 0,
      precio: 0
    });
    setFormErrors({});
    setEditingProduct(null);
  };

  const openCreateForm = () => {
    resetForm();
    setShowForm(true);
  };

  const openEditForm = (producto: Producto) => {
    setFormData({
      nombre: producto.nombre,
      tipo: producto.tipo,
      cantidad: producto.cantidad,
      precio: producto.precio
    });
    setEditingProduct(producto);
    setFormErrors({});
    setShowForm(true);
  };

  const closeForm = () => {
    setShowForm(false);
    resetForm();
  };

  const validateForm = (): boolean => {
    const errors: FormErrors = {};

    if (!formData.nombre.trim()) {
      errors.nombre = 'El nombre es requerido';
    }

    if (!formData.tipo.trim()) {
      errors.tipo = 'La categoría es requerida';
    }

    if (formData.cantidad < 0) {
      errors.cantidad = 'La cantidad no puede ser negativa';
    }

    if (formData.precio <= 0) {
      errors.precio = 'El precio debe ser mayor a 0';
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setSubmitting(true);
    try {
      const url = editingProduct 
        ? `${API_BASE_URL}/inventario/${editingProduct.id}`
        : `${API_BASE_URL}/inventario`;
      
      const method = editingProduct ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        throw new Error(`Error al ${editingProduct ? 'actualizar' : 'crear'} producto`);
      }

      await cargarProductos();
      closeForm();
    } catch (err) {
      setError(`Error al ${editingProduct ? 'actualizar' : 'crear'} producto`);
      console.error('Error:', err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('¿Está seguro de eliminar este producto?')) {
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/inventario/${id}`, {
        method: 'DELETE',
      });

      if (!response.ok) {
        throw new Error('Error al eliminar producto');
      }

      await cargarProductos();
    } catch (err) {
      setError('Error al eliminar producto');
      console.error('Error:', err);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'cantidad' || name === 'precio' ? parseFloat(value) || 0 : value
    }));

    // Limpiar error del campo si existe
    if (formErrors[name as keyof FormErrors]) {
      setFormErrors(prev => ({
        ...prev,
        [name]: undefined
      }));
    }
  };

  // Filtrar productos
  const filteredProducts = productos.filter(producto => {
    const matchesSearch = producto.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         producto.tipo.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = !filterCategory || producto.tipo === filterCategory;
    return matchesSearch && matchesCategory;
  });

  // Obtener categorías únicas
  const categorias = [...new Set(productos.map(p => p.tipo))];

  // Calcular estadísticas
  const stats = {
    totalProductos: productos.length,
    valorInventario: productos.reduce((total, p) => total + (p.cantidad * p.precio), 0),
    productosAgotados: productos.filter(p => p.cantidad === 0).length,
    categorias: categorias.length
  };

  if (loading) {
    return (
      <div className="content-area">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Cargando inventario...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="content-area">
      {/* Header */}
      <div className="page-header">
        <div className="header-content">
          <div>
            <h1 className="page-title">Gestión de Inventario</h1>
            <p className="page-subtitle">Administra productos, stock y categorías</p>
          </div>
        </div>
      </div>

      {error && (
        <div className="error-banner">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
            <line x1="15" y1="9" x2="9" y2="15" stroke="currentColor" strokeWidth="2"/>
            <line x1="9" y1="9" x2="15" y2="15" stroke="currentColor" strokeWidth="2"/>
          </svg>
          {error}
          <button onClick={() => setError(null)} className="close-error">×</button>
        </div>
      )}

      {/* Estadísticas */}
      <div className="stats-grid">
        <div className="stat-card">
          <h3>Total Productos</h3>
          <div className="stat-value">{stats.totalProductos}</div>
          <div className="stat-detail">En inventario</div>
        </div>
        <div className="stat-card">
          <h3>Valor Total</h3>
          <div className="stat-value">{formatPrice(stats.valorInventario)}</div>
          <div className="stat-detail">Valor del inventario</div>
        </div>
        <div className="stat-card">
          <h3>Sin Stock</h3>
          <div className="stat-value">{stats.productosAgotados}</div>
          <div className="stat-detail">Productos agotados</div>
        </div>
        <div className="stat-card">
          <h3>Categorías</h3>
          <div className="stat-value">{stats.categorias}</div>
          <div className="stat-detail">Tipos de productos</div>
        </div>
      </div>

      {/* Filtros y búsqueda */}
      <div className="filters-section">
        <div className="search-box">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="11" cy="11" r="8" stroke="currentColor" strokeWidth="2"/>
            <path d="m21 21-4.35-4.35" stroke="currentColor" strokeWidth="2"/>
          </svg>
          <input
            type="text"
            placeholder="Buscar productos..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>
        <select
          value={filterCategory}
          onChange={(e) => setFilterCategory(e.target.value)}
          className="filter-select"
        >
          <option value="">Todas las categorías</option>
          {categorias.map(categoria => (
            <option key={categoria} value={categoria}>{categoria}</option>
          ))}
        </select>
        <button 
          className="transparent-add-btn"
          onClick={openCreateForm}
          title="Agregar nuevo producto"
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="3" y="3" width="18" height="18" rx="2" stroke="currentColor" strokeWidth="2" fill="none"/>
            <path d="M9 12h6" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            <path d="M12 9v6" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
          </svg>
          <span className="btn-label">Agregar</span>
        </button>
      </div>

      {/* Tabla de productos */}
      <div className="table-container">
        <table className="products-table">
          <thead>
            <tr>
              <th>Producto</th>
              <th>Categoría</th>
              <th>Stock</th>
              <th>Precio</th>
              <th>Valor Total</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filteredProducts.map(producto => (
              <tr key={producto.id} className={producto.cantidad === 0 ? 'out-of-stock' : ''}>
                <td>
                  <div className="product-info">
                    <span className="product-name">{producto.nombre}</span>
                    {producto.cantidad === 0 && (
                      <span className="stock-badge">Sin stock</span>
                    )}
                  </div>
                </td>
                <td>
                  <span className="category-badge">{producto.tipo}</span>
                </td>
                <td>
                  <span className={`stock-value ${producto.cantidad <= 5 ? 'low-stock' : ''}`}>
                    {producto.cantidad}
                  </span>
                </td>
                <td>{formatPrice(producto.precio)}</td>
                <td>{formatPrice(producto.cantidad * producto.precio)}</td>
                <td>
                  <div className="action-buttons">
                    <button
                      className="btn-icon edit"
                      onClick={() => openEditForm(producto)}
                      title="Editar"
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M11 4H4C3.46957 4 2.96086 4.21071 2.58579 4.58579C2.21071 4.96086 2 5.46957 2 6V20C2 20.5304 2.21071 21.0391 2.58579 21.4142C2.96086 21.7893 3.46957 22 4 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V13" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        <path d="M18.5 2.50023C18.8978 2.1024 19.4374 1.87891 20 1.87891C20.5626 1.87891 21.1022 2.1024 21.5 2.50023C21.8978 2.89805 22.1213 3.43762 22.1213 4.00023C22.1213 4.56284 21.8978 5.1024 21.5 5.50023L12 15.0002L8 16.0002L9 12.0002L18.5 2.50023Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                      </svg>
                    </button>
                    <button
                      className="btn-icon delete"
                      onClick={() => handleDelete(producto.id!)}
                      title="Eliminar"
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <polyline points="3,6 5,6 21,6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        <path d="M8 6V4C8 3.46957 8.21071 2.96086 8.58579 2.58579C8.96086 2.21071 9.46957 2 10 2H14C14.5304 2 15.0391 2.21071 15.4142 2.58579C15.7893 2.96086 16 3.46957 16 4V6M19 6V20C19 20.5304 18.7893 21.0391 18.4142 21.4142C18.0391 21.7893 17.5304 22 17 22H7C6.46957 22 5.96086 21.7893 5.58579 21.4142C5.21071 21.0391 5 20.5304 5 20V6H19Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        <line x1="10" y1="11" x2="10" y2="17" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                        <line x1="14" y1="11" x2="14" y2="17" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                      </svg>
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {filteredProducts.length === 0 && (
          <div className="empty-state">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20 7H4C2.89543 7 2 7.89543 2 9V19C2 20.1046 2.89543 21 4 21H20C21.1046 21 22 20.1046 22 19V9C22 7.89543 21.1046 7 20 7Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              <path d="M16 21V5C16 4.46957 15.7893 3.96086 15.4142 3.58579C15.0391 3.21071 14.5304 3 14 3H10C9.46957 3 8.96086 3.21071 8.58579 3.58579C8.21071 3.96086 8 4.46957 8 5V21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
            <h3>No se encontraron productos</h3>
            <p>No hay productos que coincidan con los filtros aplicados</p>
          </div>
        )}
      </div>

      {/* Modal del formulario */}
      {showForm && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h2>{editingProduct ? 'Editar Producto' : 'Nuevo Producto'}</h2>
              <button 
                className="close-btn"
                onClick={closeForm}
                type="button"
              >
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M18 6L6 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                  <path d="M6 6L18 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
              </button>
            </div>

            <form onSubmit={handleSubmit} className="producto-form">
              <div className="form-group">
                <label htmlFor="nombre" className="form-label">Nombre del Producto</label>
                <input
                  type="text"
                  id="nombre"
                  name="nombre"
                  value={formData.nombre}
                  onChange={handleInputChange}
                  className={`form-input ${formErrors.nombre ? 'error' : ''}`}
                  placeholder="Ingrese el nombre del producto"
                />
                {formErrors.nombre && <span className="error-text">{formErrors.nombre}</span>}
              </div>

              <div className="form-group">
                <label htmlFor="tipo" className="form-label">Categoría</label>
                <select
                  id="tipo"
                  name="tipo"
                  value={formData.tipo}
                  onChange={handleInputChange}
                  className={`form-input ${formErrors.tipo ? 'error' : ''}`}
                >
                  <option value="">Seleccione una categoría</option>
                  <option value="Electrónicos">Electrónicos</option>
                  <option value="Tecnología">Tecnología</option>
                  <option value="Ropa">Ropa</option>
                  <option value="Hogar">Hogar</option>
                  <option value="Deportes">Deportes</option>
                  <option value="Libros">Libros</option>
                  <option value="Juguetes">Juguetes</option>
                  <option value="Salud">Salud</option>
                  <option value="Automotive">Automotive</option>
                  <option value="Otros">Otros</option>
                </select>
                {formErrors.tipo && <span className="error-text">{formErrors.tipo}</span>}
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="cantidad" className="form-label">Cantidad</label>
                  <input
                    type="number"
                    id="cantidad"
                    name="cantidad"
                    value={formData.cantidad}
                    onChange={handleInputChange}
                    className={`form-input ${formErrors.cantidad ? 'error' : ''}`}
                    min="0"
                    step="1"
                    placeholder="0"
                  />
                  {formErrors.cantidad && <span className="error-text">{formErrors.cantidad}</span>}
                </div>

                <div className="form-group">
                  <label htmlFor="precio" className="form-label">Precio ($)</label>
                  <input
                    type="number"
                    id="precio"
                    name="precio"
                    value={formData.precio}
                    onChange={handleInputChange}
                    className={`form-input ${formErrors.precio ? 'error' : ''}`}
                    min="0"
                    step="0.01"
                    placeholder="0.00"
                  />
                  {formErrors.precio && <span className="error-text">{formErrors.precio}</span>}
                </div>
              </div>

              <div className="form-actions">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={closeForm}
                  disabled={submitting}
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={submitting}
                >
                  {submitting ? (
                    <>
                      <div className="loading-spinner"></div>
                      {editingProduct ? 'Actualizando...' : 'Creando...'}
                    </>
                  ) : (
                    <>
                      {editingProduct ? 'Actualizar' : 'Crear'} Producto
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Inventario;
