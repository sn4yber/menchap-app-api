import React, { useState, useEffect } from 'react';
import { inventarioService } from '../../services/inventarioService';
import { createVenta, type VentaItem } from '../../services/ventasService';
import TicketModal from './TicketModal';

interface Producto {
  id: number;
  nombre: string;
  tipo: string;
  cantidad: number;
  precio: number;
}

interface Props {
  onClose: () => void;
  onVentaCompleted: () => void;
}

const VentaForm: React.FC<Props> = ({ onClose, onVentaCompleted }) => {
  const [productos, setProductos] = useState<Producto[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [carrito, setCarrito] = useState<VentaItem[]>([]);
  const [cliente, setCliente] = useState('');
  const [identificacion, setIdentificacion] = useState('');
  const [telefono, setTelefono] = useState('');
  const [metodoPago, setMetodoPago] = useState('Efectivo');
  const [observaciones, setObservaciones] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showTicket, setShowTicket] = useState(false);
  const [ticketData, setTicketData] = useState<any>(null);

  useEffect(() => {
    cargarProductos();
  }, []);

  const cargarProductos = async () => {
    try {
      const data = await inventarioService.obtenerProductos();
      // Filtrar y mapear al tipo Producto del componente
      const productosConStock = data
        .filter((p: any) => Number(p.cantidad) > 0)
        .map((p: any) => ({
          id: p.id || 0,
          nombre: p.nombre || '',
          tipo: p.tipo || '',
          cantidad: Number(p.cantidad) || 0,
          precio: Number(p.precio) || 0
        }));
      setProductos(productosConStock);
    } catch (err) {
      console.error('Error cargando productos', err);
      setError('Error al cargar productos');
    }
  };

  const productosFiltrados = productos.filter(p =>
    p.nombre.toLowerCase().includes(searchQuery.toLowerCase()) ||
    p.id.toString().includes(searchQuery)
  ).slice(0, 8);

  const agregarAlCarrito = (producto: Producto) => {
    const existente = carrito.find(item => item.productoId === producto.id);
    
    if (existente) {
      // Verificar stock disponible
      if (existente.cantidad + 1 > Number(producto.cantidad)) {
        setError(`Stock insuficiente. Solo hay ${producto.cantidad} unidades disponibles`);
        return;
      }
      setCarrito(carrito.map(item =>
        item.productoId === producto.id
          ? { ...item, cantidad: item.cantidad + 1, subtotal: (item.cantidad + 1) * item.precioUnitario }
          : item
      ));
    } else {
      setCarrito([...carrito, {
        productoId: producto.id,
        nombreProducto: producto.nombre,
        cantidad: 1,
        precioUnitario: Number(producto.precio),
        subtotal: Number(producto.precio)
      }]);
    }
    setSearchQuery('');
    setError(null);
  };

  const actualizarCantidad = (productoId: number, nuevaCantidad: number) => {
    const producto = productos.find(p => p.id === productoId);
    if (!producto) return;

    if (nuevaCantidad < 1) {
      eliminarDelCarrito(productoId);
      return;
    }

    if (nuevaCantidad > Number(producto.cantidad)) {
      setError(`Stock insuficiente. Solo hay ${producto.cantidad} unidades disponibles`);
      return;
    }

    setCarrito(carrito.map(item =>
      item.productoId === productoId
        ? { ...item, cantidad: nuevaCantidad, subtotal: nuevaCantidad * item.precioUnitario }
        : item
    ));
    setError(null);
  };

  const eliminarDelCarrito = (productoId: number) => {
    setCarrito(carrito.filter(item => item.productoId !== productoId));
  };

  const calcularTotal = () => {
    return carrito.reduce((total, item) => total + item.subtotal, 0);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (carrito.length === 0) {
      setError('Debe agregar al menos un producto al carrito');
      return;
    }

    setSubmitting(true);
    setError(null);

    try {
      // Procesar cada item del carrito como una venta
      const ventas = [];
      for (const item of carrito) {
        const venta = await createVenta({
          productoId: item.productoId,
          nombreProducto: item.nombreProducto,
          cantidad: item.cantidad,
          precioUnitario: item.precioUnitario,
          precioTotal: item.subtotal,
          cliente: cliente || undefined,
          metodoPago: metodoPago,
          observaciones: observaciones || undefined,
        });
        ventas.push(venta);
      }

      // Disparar evento para actualizar inventario
      window.dispatchEvent(new CustomEvent('inventario:updated'));

      // Preparar datos del ticket
      const numeroTicket = ventas[0].id?.toString().padStart(6, '0') || '000000';
      const fecha = new Date().toLocaleString('es-CO', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });

      setTicketData({
        numeroTicket,
        fecha,
        cliente: cliente || 'Cliente General',
        identificacion: identificacion || undefined,
        telefono: telefono || undefined,
        metodoPago,
        items: carrito,
        subtotal: calcularTotal(),
        total: calcularTotal()
      });

      setShowTicket(true);
      
    } catch (err: any) {
      setError(err.message || 'Error al procesar la venta');
      console.error('Error:', err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleCloseTicket = () => {
    setShowTicket(false);
    setCarrito([]);
    setCliente('');
    setIdentificacion('');
    setTelefono('');
    setObservaciones('');
    onVentaCompleted();
    onClose();
  };

  return (
    <>
      <div className="modal-overlay">
        <div className="modal-content venta-modal-responsive" style={{ maxWidth: 1200, maxHeight: '90vh', overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
          <div className="modal-header" style={{ 
            background: 'linear-gradient(135deg, #5b79ff 0%, #4285f4 100%)',
            color: 'white',
            padding: '20px 24px'
          }}>
            <div>
              <h2 style={{ margin: 0 }}>
                Punto de Venta
              </h2>
              <p style={{ margin: '4px 0 0 0', fontSize: 14, opacity: 0.9 }}>
                Selecciona productos y procesa la venta
              </p>
            </div>
            <button className="close-btn" onClick={onClose} style={{ color: 'white', fontSize: 24 }}>×</button>
          </div>

          {error && (
            <div style={{ 
              margin: '16px 24px 0',
              padding: '12px 16px',
              backgroundColor: '#fee2e2',
              border: '1px solid #fecaca',
              borderRadius: 8,
              color: '#991b1b',
              fontSize: 14,
              fontWeight: 500
            }}>
              {error}
            </div>
          )}

          <div style={{ flex: 1, overflow: 'auto', padding: 24 }}>
            <form onSubmit={handleSubmit} style={{ height: '100%' }}>
              {/* Layout de dos columnas */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 400px', gap: 24, height: '100%' }}>
                
                {/* Columna izquierda: Búsqueda y productos */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
                  {/* Búsqueda */}
                  <div>
                    <div style={{ 
                      position: 'relative',
                      backgroundColor: 'white',
                      borderRadius: 12,
                      border: '2px solid #e2e8f0',
                      transition: 'all 0.3s'
                    }}>
                      <input
                        style={{
                          width: '100%',
                          padding: '16px',
                          border: 'none',
                          borderRadius: 12,
                          fontSize: 16,
                          outline: 'none'
                        }}
                        value={searchQuery}
                        onChange={e => setSearchQuery(e.target.value)}
                        placeholder="Buscar productos por nombre o ID..."
                        autoComplete="off"
                      />
                    </div>
                  </div>

                  {/* Grid de productos */}
                  <div style={{ 
                    flex: 1,
                    overflowY: 'auto',
                    backgroundColor: '#f8fafc',
                    borderRadius: 12,
                    padding: 16
                  }}>
                    {!searchQuery ? (
                      <div style={{ 
                        height: '100%',
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: '#94a3b8',
                        textAlign: 'center'
                      }}>
                        <h3 style={{ margin: '0 0 8px 0', color: '#64748b', fontSize: 18 }}>Buscar Productos</h3>
                        <p style={{ margin: 0, fontSize: 14 }}>Escribe el nombre o ID del producto para comenzar</p>
                      </div>
                    ) : productosFiltrados.length === 0 ? (
                      <div style={{ 
                        height: '100%',
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: '#94a3b8',
                        textAlign: 'center'
                      }}>
                        <h3 style={{ margin: '0 0 8px 0', color: '#64748b', fontSize: 18 }}>No se encontraron productos</h3>
                        <p style={{ margin: 0, fontSize: 14 }}>Intenta con otro término de búsqueda</p>
                      </div>
                    ) : (
                      <div style={{ 
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
                        gap: 12
                      }}>
                        {productosFiltrados.map(p => (
                          <div
                            key={p.id}
                            onClick={() => agregarAlCarrito(p)}
                            style={{
                              backgroundColor: 'white',
                              borderRadius: 12,
                              padding: 16,
                              cursor: 'pointer',
                              border: '2px solid #e2e8f0',
                              transition: 'all 0.2s',
                              position: 'relative'
                            }}
                            onMouseEnter={(e) => {
                              e.currentTarget.style.transform = 'translateY(-2px)';
                              e.currentTarget.style.boxShadow = '0 8px 16px rgba(66,133,244,0.2)';
                              e.currentTarget.style.borderColor = '#4285f4';
                            }}
                            onMouseLeave={(e) => {
                              e.currentTarget.style.transform = 'translateY(0)';
                              e.currentTarget.style.boxShadow = 'none';
                              e.currentTarget.style.borderColor = '#e2e8f0';
                            }}
                          >
                            <div style={{ 
                              position: 'absolute',
                              top: 8,
                              right: 8,
                              backgroundColor: '#f0fdf4',
                              color: '#166534',
                              padding: '4px 8px',
                              borderRadius: 6,
                              fontSize: 11,
                              fontWeight: 600
                            }}>
                              Stock: {p.cantidad}
                            </div>
                            <h4 style={{ 
                              margin: '0 0 4px 0',
                              fontSize: 14,
                              fontWeight: 600,
                              color: '#1e293b',
                              overflow: 'hidden',
                              textOverflow: 'ellipsis',
                              whiteSpace: 'nowrap'
                            }}>
                              {p.nombre}
                            </h4>
                            <div style={{ 
                              fontSize: 11,
                              color: '#64748b',
                              marginBottom: 12
                            }}>
                              {p.tipo}
                            </div>
                            <div style={{ 
                              fontSize: 18,
                              fontWeight: 700,
                              color: '#4285f4'
                            }}>
                              ${Number(p.precio).toLocaleString('es-CO')}
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>

                {/* Columna derecha: Carrito */}
                <div style={{ 
                  display: 'flex',
                  flexDirection: 'column',
                  backgroundColor: 'white',
                  borderRadius: 12,
                  border: '2px solid #e2e8f0',
                  overflow: 'hidden'
                }}>
                  {/* Header del carrito */}
                  <div style={{ 
                    padding: 16,
                    background: 'linear-gradient(135deg, #5b79ff 0%, #4285f4 100%)',
                    color: 'white'
                  }}>
                    <h3 style={{ margin: 0, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                      <span>Carrito de Compras</span>
                      {carrito.length > 0 && (
                        <span style={{ 
                          backgroundColor: 'rgba(255,255,255,0.3)',
                          padding: '4px 12px',
                          borderRadius: 12,
                          fontSize: 14
                        }}>
                          {carrito.length} {carrito.length === 1 ? 'item' : 'items'}
                        </span>
                      )}
                    </h3>
                  </div>

                  {/* Items del carrito */}
                  <div style={{ 
                    flex: 1,
                    overflowY: 'auto',
                    padding: 16
                  }}>
                    {carrito.length === 0 ? (
                      <div style={{
                        height: '100%',
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: '#94a3b8',
                        textAlign: 'center'
                      }}>
                        <h4 style={{ margin: '0 0 8px 0', color: '#64748b', fontSize: 16 }}>Carrito Vacío</h4>
                        <p style={{ fontSize: 14, margin: 0 }}>Agrega productos para comenzar</p>
                      </div>
                    ) : (
                      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                        {carrito.map(item => (
                          <div
                            key={item.productoId}
                            style={{
                              backgroundColor: '#f8fafc',
                              borderRadius: 8,
                              padding: 12,
                              border: '1px solid #e2e8f0'
                            }}
                          >
                            <div style={{ 
                              display: 'flex',
                              justifyContent: 'space-between',
                              marginBottom: 8
                            }}>
                              <h4 style={{ 
                                margin: 0,
                                fontSize: 14,
                                fontWeight: 600,
                                color: '#1e293b',
                                flex: 1
                              }}>
                                {item.nombreProducto}
                              </h4>
                              <button
                                type="button"
                                onClick={() => eliminarDelCarrito(item.productoId)}
                                style={{
                                  background: 'none',
                                  border: 'none',
                                  cursor: 'pointer',
                                  fontSize: 14,
                                  padding: 4,
                                  color: '#ef4444',
                                  fontWeight: 600
                                }}
                                title="Eliminar"
                              >
                                ✕
                              </button>
                            </div>
                            <div style={{ 
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'space-between',
                              gap: 12
                            }}>
                              <div style={{ 
                                display: 'flex',
                                alignItems: 'center',
                                gap: 8,
                                backgroundColor: 'white',
                                borderRadius: 6,
                                padding: '4px 8px',
                                border: '1px solid #e2e8f0'
                              }}>
                                <button
                                  type="button"
                                  onClick={() => actualizarCantidad(item.productoId, item.cantidad - 1)}
                                  style={{
                                    width: 24,
                                    height: 24,
                                    border: 'none',
                                    borderRadius: 4,
                                    backgroundColor: '#f1f5f9',
                                    cursor: 'pointer',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    fontWeight: 600,
                                    color: '#64748b'
                                  }}
                                >
                                  −
                                </button>
                                <span style={{ 
                                  minWidth: 24,
                                  textAlign: 'center',
                                  fontWeight: 600,
                                  fontSize: 14
                                }}>
                                  {item.cantidad}
                                </span>
                                <button
                                  type="button"
                                  onClick={() => actualizarCantidad(item.productoId, item.cantidad + 1)}
                                  style={{
                                    width: 24,
                                    height: 24,
                                    border: 'none',
                                    borderRadius: 4,
                                    backgroundColor: '#f1f5f9',
                                    cursor: 'pointer',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    fontWeight: 600,
                                    color: '#64748b'
                                  }}
                                >
                                  +
                                </button>
                              </div>
                              <div style={{ textAlign: 'right' }}>
                                <div style={{ 
                                  fontSize: 11,
                                  color: '#64748b',
                                  marginBottom: 2
                                }}>
                                  ${item.precioUnitario.toLocaleString('es-CO')} c/u
                                </div>
                                <div style={{ 
                                  fontSize: 16,
                                  fontWeight: 700,
                                  color: '#4285f4'
                                }}>
                                  ${item.subtotal.toLocaleString('es-CO')}
                                </div>
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>

                  {/* Footer del carrito - Información adicional */}
                  {carrito.length > 0 && (
                    <div style={{ 
                      padding: 16,
                      borderTop: '2px solid #e2e8f0',
                      backgroundColor: '#fafafa'
                    }}>
                      <div style={{ marginBottom: 12 }}>
                        <label style={{ 
                          display: 'block',
                          fontSize: 12,
                          fontWeight: 600,
                          color: '#64748b',
                          marginBottom: 6
                        }}>
                          Cliente
                        </label>
                        <input
                          style={{
                            width: '100%',
                            padding: '8px 12px',
                            border: '1px solid #e2e8f0',
                            borderRadius: 6,
                            fontSize: 14
                          }}
                          value={cliente}
                          onChange={e => setCliente(e.target.value)}
                          placeholder="Nombre del cliente"
                        />
                      </div>

                      <div style={{ marginBottom: 12 }}>
                        <label style={{ 
                          display: 'block',
                          fontSize: 12,
                          fontWeight: 600,
                          color: '#64748b',
                          marginBottom: 6
                        }}>
                          Identificación
                        </label>
                        <input
                          style={{
                            width: '100%',
                            padding: '8px 12px',
                            border: '1px solid #e2e8f0',
                            borderRadius: 6,
                            fontSize: 14
                          }}
                          value={identificacion}
                          onChange={e => setIdentificacion(e.target.value)}
                          placeholder="CC o NIT"
                        />
                      </div>

                      <div style={{ marginBottom: 12 }}>
                        <label style={{ 
                          display: 'block',
                          fontSize: 12,
                          fontWeight: 600,
                          color: '#64748b',
                          marginBottom: 6
                        }}>
                          Teléfono
                        </label>
                        <input
                          type="tel"
                          style={{
                            width: '100%',
                            padding: '8px 12px',
                            border: '1px solid #e2e8f0',
                            borderRadius: 6,
                            fontSize: 14
                          }}
                          value={telefono}
                          onChange={e => setTelefono(e.target.value)}
                          placeholder="Número de contacto"
                        />
                      </div>

                      <div style={{ marginBottom: 12 }}>
                        <label style={{ 
                          display: 'block',
                          fontSize: 12,
                          fontWeight: 600,
                          color: '#64748b',
                          marginBottom: 6
                        }}>
                          Método de pago *
                        </label>
                        <select
                          style={{
                            width: '100%',
                            padding: '8px 12px',
                            border: '1px solid #e2e8f0',
                            borderRadius: 6,
                            fontSize: 14,
                            backgroundColor: 'white'
                          }}
                          value={metodoPago}
                          onChange={e => setMetodoPago(e.target.value)}
                          required
                        >
                          <option value="Efectivo">Efectivo</option>
                          <option value="Tarjeta">Tarjeta</option>
                          <option value="Transferencia">Transferencia</option>
                          <option value="Nequi">Nequi</option>
                          <option value="Daviplata">Daviplata</option>
                        </select>
                      </div>

                      <div style={{ marginBottom: 16 }}>
                        <label style={{ 
                          display: 'block',
                          fontSize: 12,
                          fontWeight: 600,
                          color: '#64748b',
                          marginBottom: 6
                        }}>
                          Notas
                        </label>
                        <textarea
                          style={{
                            width: '100%',
                            padding: '8px 12px',
                            border: '1px solid #e2e8f0',
                            borderRadius: 6,
                            fontSize: 13,
                            resize: 'none',
                            height: 60
                          }}
                          value={observaciones}
                          onChange={e => setObservaciones(e.target.value)}
                          placeholder="Observaciones..."
                        />
                      </div>

                      {/* Total */}
                      <div style={{ 
                        padding: '16px 0',
                        borderTop: '2px dashed #cbd5e1',
                        marginBottom: 12
                      }}>
                        <div style={{ 
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'center'
                        }}>
                          <span style={{ 
                            fontSize: 14,
                            fontWeight: 600,
                            color: '#64748b'
                          }}>
                            TOTAL A PAGAR
                          </span>
                          <span style={{ 
                            fontSize: 24,
                            fontWeight: 700,
                            color: '#4285f4'
                          }}>
                            ${calcularTotal().toLocaleString('es-CO')}
                          </span>
                        </div>
                      </div>

                      {/* Botón de procesar */}
                      <button
                        type="submit"
                        disabled={submitting || carrito.length === 0}
                        style={{
                          width: '100%',
                          padding: '14px 20px',
                          background: submitting ? '#94a3b8' : 'linear-gradient(135deg, #5b79ff 0%, #4285f4 100%)',
                          color: 'white',
                          border: 'none',
                          borderRadius: 8,
                          fontSize: 16,
                          fontWeight: 600,
                          cursor: submitting ? 'not-allowed' : 'pointer',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          gap: 8,
                          transition: 'all 0.2s',
                          boxShadow: submitting ? 'none' : '0 4px 15px rgba(66, 133, 244, 0.4)'
                        }}
                        onMouseEnter={(e) => {
                          if (!submitting) {
                            e.currentTarget.style.transform = 'translateY(-2px)';
                            e.currentTarget.style.boxShadow = '0 8px 25px rgba(66, 133, 244, 0.5)';
                          }
                        }}
                        onMouseLeave={(e) => {
                          e.currentTarget.style.transform = 'translateY(0)';
                          e.currentTarget.style.boxShadow = '0 4px 15px rgba(66, 133, 244, 0.4)';
                        }}
                      >
                        {submitting ? 'Procesando...' : 'Procesar Venta'}
                      </button>
                    </div>
                  )}
                </div>

              </div>
            </form>
          </div>
        </div>
      </div>

      {showTicket && ticketData && (
        <TicketModal
          show={showTicket}
          onClose={handleCloseTicket}
          ventaData={ticketData}
        />
      )}
    </>
  );
};

export default VentaForm;
