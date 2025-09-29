import React, { useState, useEffect } from 'react';
import type { Producto, ProductoFormData } from '../types/Producto';

interface ProductoFormProps {
  producto?: Producto | null;
  onSubmit: (data: ProductoFormData) => void;
  onCancel: () => void;
  isOpen: boolean;
}

interface FormErrors {
  nombre?: string;
  tipo?: string;
  cantidad?: string;
  precio?: string;
}

const ProductoForm: React.FC<ProductoFormProps> = ({
  producto,
  onSubmit,
  onCancel,
  isOpen
}) => {
  const [formData, setFormData] = useState<ProductoFormData>({
    nombre: '',
    tipo: '',
    cantidad: 0,
    precio: 0
  });

  const [errors, setErrors] = useState<FormErrors>({});

  useEffect(() => {
    if (producto) {
      setFormData({
        nombre: producto.nombre,
        tipo: producto.tipo,
        cantidad: producto.cantidad,
        precio: producto.precio
      });
    } else {
      setFormData({
        nombre: '',
        tipo: '',
        cantidad: 0,
        precio: 0
      });
    }
    setErrors({});
  }, [producto, isOpen]);

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    if (!formData.nombre.trim()) {
      newErrors.nombre = 'El nombre es requerido';
    }

    if (!formData.tipo.trim()) {
      newErrors.tipo = 'El tipo es requerido';
    }

    if (formData.cantidad < 0) {
      newErrors.cantidad = 'La cantidad no puede ser negativa';
    }

    if (formData.precio <= 0) {
      newErrors.precio = 'El precio debe ser mayor a 0';
    } else if (isNaN(formData.precio)) {
      newErrors.precio = 'Ingrese un precio válido';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validateForm()) {
      onSubmit(formData);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    
    if (name === 'precio') {
      // Permitir decimales y números grandes para el precio
      const numericValue = value === '' ? 0 : parseFloat(value);
      setFormData(prev => ({
        ...prev,
        [name]: isNaN(numericValue) ? 0 : numericValue
      }));
    } else if (name === 'cantidad') {
      // Solo números enteros para cantidad
      const numericValue = value === '' ? 0 : parseInt(value) || 0;
      setFormData(prev => ({
        ...prev,
        [name]: numericValue
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }));
    }

    // Limpiar error del campo si existe
    if (errors[name as keyof FormErrors]) {
      setErrors(prev => ({
        ...prev,
        [name]: undefined
      }));
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h2>{producto ? 'Editar Producto' : 'Nuevo Producto'}</h2>
          <button 
            className="close-btn"
            onClick={onCancel}
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
              className={`form-input ${errors.nombre ? 'error' : ''}`}
              placeholder="Ingrese el nombre del producto"
            />
            {errors.nombre && <span className="error-text">{errors.nombre}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="tipo" className="form-label">Categoría</label>
            <select
              id="tipo"
              name="tipo"
              value={formData.tipo}
              onChange={handleInputChange}
              className={`form-input ${errors.tipo ? 'error' : ''}`}
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
            {errors.tipo && <span className="error-text">{errors.tipo}</span>}
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
                className={`form-input ${errors.cantidad ? 'error' : ''}`}
                min="0"
                step="1"
                placeholder="0"
              />
              {errors.cantidad && <span className="error-text">{errors.cantidad}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="precio" className="form-label">Precio ($)</label>
              <input
                type="number"
                id="precio"
                name="precio"
                value={formData.precio}
                onChange={handleInputChange}
                className={`form-input ${errors.precio ? 'error' : ''}`}
                min="0"
                step="0.01"
                placeholder="599.900"
                lang="en"
              />
              {errors.precio && <span className="error-text">{errors.precio}</span>}
              <small className="form-helper">Ejemplo: 599.900 o 1250000.50</small>
            </div>
          </div>

          <div className="form-actions">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onCancel}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="btn btn-primary"
            >
              {producto ? 'Actualizar' : 'Crear'} Producto
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ProductoForm;
