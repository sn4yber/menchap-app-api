import React, { useRef } from 'react';
import html2canvas from 'html2canvas';

interface TicketItem {
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

interface TicketModalProps {
  show: boolean;
  onClose: () => void;
  ventaData: {
    numeroTicket: string;
    fecha: string;
    cliente?: string;
    identificacion?: string;
    telefono?: string;
    metodoPago?: string;
    items: TicketItem[];
    subtotal: number;
    total: number;
  };
}

const TicketModal: React.FC<TicketModalProps> = ({ show, onClose, ventaData }) => {
  const ticketRef = useRef<HTMLDivElement>(null);

  if (!show) return null;

  const handleDownload = async () => {
    if (!ticketRef.current) return;

    try {
      const canvas = await html2canvas(ticketRef.current, {
        backgroundColor: '#ffffff',
        scale: 2,
      });
      
      const link = document.createElement('a');
      link.download = `ticket-${ventaData.numeroTicket}.png`;
      link.href = canvas.toDataURL('image/png');
      link.click();
    } catch (error) {
      console.error('Error al descargar ticket:', error);
      alert('Error al generar la imagen del ticket');
    }
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="modal-overlay" style={{ zIndex: 2000 }}>
      <div className="modal-content" style={{ maxWidth: 500, padding: 0 }}>
        <div className="modal-header" style={{ padding: '20px 24px', borderBottom: '1px solid #e2e8f0' }}>
          <h2>Venta Exitosa</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <div style={{ padding: 24 }}>
          {/* Ticket */}
          <div 
            ref={ticketRef}
            style={{
              backgroundColor: 'white',
              border: '2px dashed #cbd5e0',
              borderRadius: 8,
              padding: 24,
              fontFamily: 'monospace',
            }}
          >
            {/* Header */}
            <div style={{ textAlign: 'center', marginBottom: 20, borderBottom: '2px solid #000', paddingBottom: 12 }}>
              <h3 style={{ margin: 0, fontSize: 20, fontWeight: 'bold' }}>LIVO</h3>
              <p style={{ margin: '4px 0', fontSize: 12 }}>Sistema de Inventario</p>
              <p style={{ margin: '4px 0', fontSize: 11, color: '#666' }}>NIT: 900.123.456-7</p>
            </div>

            {/* Info del ticket */}
            <div style={{ fontSize: 12, marginBottom: 16 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                <span style={{ fontWeight: 'bold' }}>Ticket:</span>
                <span>#{ventaData.numeroTicket}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                <span style={{ fontWeight: 'bold' }}>Fecha:</span>
                <span>{ventaData.fecha}</span>
              </div>
              {ventaData.cliente && (
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                  <span style={{ fontWeight: 'bold' }}>Cliente:</span>
                  <span>{ventaData.cliente}</span>
                </div>
              )}
              {ventaData.identificacion && (
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                  <span style={{ fontWeight: 'bold' }}>Identificación:</span>
                  <span>{ventaData.identificacion}</span>
                </div>
              )}
              {ventaData.telefono && (
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                  <span style={{ fontWeight: 'bold' }}>Teléfono:</span>
                  <span>{ventaData.telefono}</span>
                </div>
              )}
              {ventaData.metodoPago && (
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span style={{ fontWeight: 'bold' }}>Método Pago:</span>
                  <span>{ventaData.metodoPago}</span>
                </div>
              )}
            </div>

            {/* Línea separadora */}
            <div style={{ borderTop: '1px dashed #000', margin: '12px 0' }}></div>

            {/* Items */}
            <div style={{ marginBottom: 16 }}>
              <table style={{ width: '100%', fontSize: 11, borderCollapse: 'collapse' }}>
                <thead>
                  <tr style={{ borderBottom: '1px solid #000' }}>
                    <th style={{ textAlign: 'left', padding: '6px 0' }}>Producto</th>
                    <th style={{ textAlign: 'center', padding: '6px 4px' }}>Cant</th>
                    <th style={{ textAlign: 'right', padding: '6px 0' }}>P.Unit</th>
                    <th style={{ textAlign: 'right', padding: '6px 0' }}>Total</th>
                  </tr>
                </thead>
                <tbody>
                  {ventaData.items.map((item, index) => (
                    <tr key={index} style={{ borderBottom: '1px dotted #ccc' }}>
                      <td style={{ padding: '8px 0', maxWidth: 150, overflow: 'hidden', textOverflow: 'ellipsis' }}>
                        {item.nombreProducto}
                      </td>
                      <td style={{ textAlign: 'center', padding: '8px 4px' }}>{item.cantidad}</td>
                      <td style={{ textAlign: 'right', padding: '8px 0' }}>
                        ${Number(item.precioUnitario).toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 2 })}
                      </td>
                      <td style={{ textAlign: 'right', padding: '8px 0', fontWeight: 'bold' }}>
                        ${Number(item.subtotal).toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 2 })}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Línea separadora */}
            <div style={{ borderTop: '2px solid #000', margin: '12px 0' }}></div>

            {/* Total */}
            <div style={{ fontSize: 14, marginBottom: 16 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                <span style={{ fontWeight: 'bold' }}>SUBTOTAL:</span>
                <span>${Number(ventaData.subtotal).toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 2 })}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 16, fontWeight: 'bold' }}>
                <span>TOTAL:</span>
                <span>${Number(ventaData.total).toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 2 })}</span>
              </div>
            </div>

            {/* Línea separadora */}
            <div style={{ borderTop: '2px solid #000', margin: '12px 0' }}></div>

            {/* Footer */}
            <div style={{ textAlign: 'center', fontSize: 10, color: '#666', marginTop: 16 }}>
              <p style={{ margin: '4px 0' }}>¡Gracias por su compra!</p>
              <p style={{ margin: '4px 0' }}>www.livo.com</p>
              <p style={{ margin: '4px 0' }}>Tel: (123) 456-7890</p>
            </div>
          </div>

          {/* Botones de acción */}
          <div style={{ display: 'flex', gap: 12, marginTop: 20 }}>
            <button 
              className="btn secondary" 
              onClick={handlePrint}
              style={{ flex: 1 }}
            >
              Imprimir
            </button>
            <button 
              className="btn primary" 
              onClick={handleDownload}
              style={{ flex: 1 }}
            >
              Descargar PNG
            </button>
          </div>

          <button 
            className="btn" 
            onClick={onClose}
            style={{ width: '100%', marginTop: 12, backgroundColor: '#f1f5f9', color: '#475569' }}
          >
            Cerrar
          </button>
        </div>
      </div>

      <style>{`
        @media print {
          body * {
            visibility: hidden;
          }
          .modal-overlay, .modal-overlay * {
            visibility: visible;
          }
          .modal-overlay {
            position: absolute;
            left: 0;
            top: 0;
            width: 100%;
          }
          button {
            display: none !important;
          }
        }
      `}</style>
    </div>
  );
};

export default TicketModal;
