import React, { useState, useEffect } from 'react';
import { DashboardStats } from '@/types';
import { productosApi, ventasApi, comprasApi } from '@/services/api';

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats>({
    totalVentas: 0,
    totalCompras: 0,
    ganancias: 0,
    productosEnStock: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      
      // Obtener datos en paralelo
      const [productosRes, ventasRes, comprasRes] = await Promise.all([
        productosApi.getAll(),
        ventasApi.getAll(),
        comprasApi.getAll(),
      ]);

      const productos = productosRes.data;
      const ventas = ventasRes.data;
      const compras = comprasRes.data;

      // Calcular estadísticas
      const totalVentas = ventas.reduce((sum, venta) => sum + (venta.precioTotal || 0), 0);
      const totalCompras = compras.reduce((sum, compra) => sum + (compra.costoTotal || 0), 0);
      const ganancias = ventas.reduce((sum, venta) => sum + (venta.ganancia || 0), 0);
      const productosEnStock = productos.filter(p => p.cantidad > 0).length;

      setStats({
        totalVentas,
        totalCompras,
        ganancias,
        productosEnStock,
      });
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  const StatCard: React.FC<{ title: string; value: string | number; icon: string; color: string }> = 
    ({ title, value, icon, color }) => (
    <div className={`card ${color}`}>
      <div className="flex items-center">
        <div className="flex-1">
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="text-2xl font-semibold text-gray-900">
            {typeof value === 'number' && title.includes('$') ? 
              `$${value.toLocaleString('es-ES', { minimumFractionDigits: 2 })}` : 
              value
            }
          </p>
        </div>
        <div className="ml-4">
          <span className="text-3xl">{icon}</span>
        </div>
      </div>
    </div>
  );

  return (
    <div>
      <div className="mb-8">
        <h2 className="text-2xl font-bold text-gray-900">Dashboard</h2>
        <p className="text-gray-600">Resumen de las actividades de hoy</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <StatCard
          title="Total Ventas"
          value={stats.totalVentas}
          icon="💰"
          color="bg-green-50 border-green-200"
        />
        <StatCard
          title="Total Compras"
          value={stats.totalCompras}
          icon="🛒"
          color="bg-blue-50 border-blue-200"
        />
        <StatCard
          title="Ganancias"
          value={stats.ganancias}
          icon="📈"
          color="bg-purple-50 border-purple-200"
        />
        <StatCard
          title="Productos en Stock"
          value={stats.productosEnStock}
          icon="📦"
          color="bg-yellow-50 border-yellow-200"
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Resumen Financiero</h3>
          <div className="space-y-4">
            <div className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
              <span className="text-gray-600">Ingresos (Ventas)</span>
              <span className="font-semibold text-green-600">
                ${stats.totalVentas.toLocaleString('es-ES', { minimumFractionDigits: 2 })}
              </span>
            </div>
            <div className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
              <span className="text-gray-600">Egresos (Compras)</span>
              <span className="font-semibold text-red-600">
                ${stats.totalCompras.toLocaleString('es-ES', { minimumFractionDigits: 2 })}
              </span>
            </div>
            <div className="flex justify-between items-center p-3 bg-primary-50 rounded-lg border-2 border-primary-200">
              <span className="text-primary-800 font-medium">Balance</span>
              <span className="font-bold text-primary-700">
                ${(stats.totalVentas - stats.totalCompras).toLocaleString('es-ES', { minimumFractionDigits: 2 })}
              </span>
            </div>
          </div>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Acciones Rápidas</h3>
          <div className="space-y-3">
            <button className="w-full btn-primary flex items-center justify-center">
              <span className="mr-2">➕</span>
              Nueva Venta
            </button>
            <button className="w-full btn-secondary flex items-center justify-center">
              <span className="mr-2">📦</span>
              Agregar Producto
            </button>
            <button className="w-full btn-secondary flex items-center justify-center">
              <span className="mr-2">🛒</span>
              Registrar Compra
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
