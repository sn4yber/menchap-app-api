import React, { useState, useEffect } from 'react';
import { reportesApi } from '@/services/api';
import { Venta, Compra } from '@/types';

const ReportesList: React.FC = () => {
  const [ventas, setVentas] = useState<Venta[]>([]);
  const [compras, setCompras] = useState<Compra[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');
  const [activeTab, setActiveTab] = useState<'ventas' | 'compras' | 'resumen'>('resumen');

  // Helper functions
  const formatCurrency = (amount?: number): string => {
    if (amount == null) return '$0.00';
    return `$${amount.toFixed(2)}`;
  };

  const formatDate = (dateString?: string): string => {
    if (!dateString) return 'Sin fecha';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  };

  // Set default dates (last 30 days)
  useEffect(() => {
    const today = new Date();
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(today.getDate() - 30);

    setFechaFin(today.toISOString().split('T')[0]);
    setFechaInicio(thirtyDaysAgo.toISOString().split('T')[0]);
  }, []);

  const generateReport = async () => {
    if (!fechaInicio || !fechaFin) {
      setError('Por favor selecciona ambas fechas');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const [ventasResponse, comprasResponse] = await Promise.all([
        reportesApi.getVentas(fechaInicio, fechaFin),
        reportesApi.getCompras(fechaInicio, fechaFin)
      ]);

      setVentas(ventasResponse.data || []);
      setCompras(comprasResponse.data || []);
    } catch (err) {
      setError('Error al generar el reporte');
      console.error('Error generating report:', err);
    } finally {
      setLoading(false);
    }
  };

  // Calculate totals
  const totalVentas = ventas.reduce((sum, venta) => sum + (venta.precioTotal || 0), 0);
  const totalCompras = compras.reduce((sum, compra) => sum + (compra.costoTotal || 0), 0);
  const gananciaBruta = totalVentas - totalCompras;

  const tabs = [
    { key: 'resumen', label: 'Resumen', icon: '📊' },
    { key: 'ventas', label: 'Ventas', icon: '💰' },
    { key: 'compras', label: 'Compras', icon: '🛒' }
  ];

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-900">Reportes</h2>
      </div>

      {/* Filtros de fecha */}
      <div className="bg-white p-6 rounded-lg shadow">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Filtros de Fecha</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Fecha Inicio
            </label>
            <input
              type="date"
              value={fechaInicio}
              onChange={(e) => setFechaInicio(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Fecha Fin
            </label>
            <input
              type="date"
              value={fechaFin}
              onChange={(e) => setFechaFin(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
            />
          </div>
          
          <div className="flex items-end">
            <button
              onClick={generateReport}
              disabled={loading}
              className="w-full bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Generando...' : 'Generar Reporte'}
            </button>
          </div>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-800">{error}</p>
        </div>
      )}

      {/* Tabs */}
      <div className="bg-white rounded-lg shadow">
        <div className="border-b border-gray-200">
          <nav className="-mb-px flex space-x-8 px-6">
            {tabs.map((tab) => (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key as any)}
                className={`whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm ${
                  activeTab === tab.key
                    ? 'border-indigo-500 text-indigo-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                <span className="mr-2">{tab.icon}</span>
                {tab.label}
              </button>
            ))}
          </nav>
        </div>

        <div className="p-6">
          {activeTab === 'resumen' && (
            <div className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="bg-green-50 p-6 rounded-lg border border-green-200">
                  <div className="flex items-center">
                    <div className="flex-shrink-0">
                      <div className="flex items-center justify-center h-8 w-8 rounded-md bg-green-500 text-white">
                        💰
                      </div>
                    </div>
                    <div className="ml-4">
                      <p className="text-sm font-medium text-green-600">Total Ventas</p>
                      <p className="text-2xl font-bold text-green-900">{formatCurrency(totalVentas)}</p>
                    </div>
                  </div>
                </div>

                <div className="bg-red-50 p-6 rounded-lg border border-red-200">
                  <div className="flex items-center">
                    <div className="flex-shrink-0">
                      <div className="flex items-center justify-center h-8 w-8 rounded-md bg-red-500 text-white">
                        🛒
                      </div>
                    </div>
                    <div className="ml-4">
                      <p className="text-sm font-medium text-red-600">Total Compras</p>
                      <p className="text-2xl font-bold text-red-900">{formatCurrency(totalCompras)}</p>
                    </div>
                  </div>
                </div>

                <div className="bg-blue-50 p-6 rounded-lg border border-blue-200">
                  <div className="flex items-center">
                    <div className="flex-shrink-0">
                      <div className="flex items-center justify-center h-8 w-8 rounded-md bg-blue-500 text-white">
                        📈
                      </div>
                    </div>
                    <div className="ml-4">
                      <p className="text-sm font-medium text-blue-600">Ganancia Bruta</p>
                      <p className={`text-2xl font-bold ${gananciaBruta >= 0 ? 'text-green-900' : 'text-red-900'}`}>
                        {formatCurrency(gananciaBruta)}
                      </p>
                    </div>
                  </div>
                </div>
              </div>

              {(ventas.length > 0 || compras.length > 0) && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="bg-gray-50 p-4 rounded-lg">
                    <h4 className="text-lg font-medium text-gray-900 mb-3">Resumen de Ventas</h4>
                    <p className="text-sm text-gray-600">Total de transacciones: {ventas.length}</p>
                    <p className="text-sm text-gray-600">Promedio por venta: {formatCurrency(ventas.length > 0 ? totalVentas / ventas.length : 0)}</p>
                  </div>

                  <div className="bg-gray-50 p-4 rounded-lg">
                    <h4 className="text-lg font-medium text-gray-900 mb-3">Resumen de Compras</h4>
                    <p className="text-sm text-gray-600">Total de transacciones: {compras.length}</p>
                    <p className="text-sm text-gray-600">Promedio por compra: {formatCurrency(compras.length > 0 ? totalCompras / compras.length : 0)}</p>
                  </div>
                </div>
              )}
            </div>
          )}

          {activeTab === 'ventas' && (
            <div className="space-y-4">
              <h3 className="text-lg font-medium text-gray-900">Reporte de Ventas</h3>
              {ventas.length === 0 ? (
                <p className="text-gray-500 text-center py-8">No hay ventas en el período seleccionado</p>
              ) : (
                <div className="overflow-x-auto">
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Producto</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Cliente</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Cantidad</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Precio Unit.</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha</th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {ventas.map((venta, index) => (
                        <tr key={venta.id || index} className="hover:bg-gray-50">
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                            {venta.nombreProducto || `Producto ID: ${venta.productoId}`}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {venta.cliente || 'Cliente no especificado'}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {venta.cantidad}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {formatCurrency(venta.precioUnitario)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-green-600">
                            {formatCurrency(venta.precioTotal)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {formatDate(venta.fechaVenta)}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}

          {activeTab === 'compras' && (
            <div className="space-y-4">
              <h3 className="text-lg font-medium text-gray-900">Reporte de Compras</h3>
              {compras.length === 0 ? (
                <p className="text-gray-500 text-center py-8">No hay compras en el período seleccionado</p>
              ) : (
                <div className="overflow-x-auto">
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Producto</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Proveedor</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Cantidad</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Costo Unit.</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha</th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {compras.map((compra, index) => (
                        <tr key={compra.id || index} className="hover:bg-gray-50">
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                            {compra.nombreProducto || `Producto ID: ${compra.productoId}`}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {compra.proveedor}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {compra.cantidad}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {formatCurrency(compra.costoUnitario)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-red-600">
                            {formatCurrency(compra.costoTotal)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {formatDate(compra.fechaCompra)}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ReportesList;
