import React, { useState } from 'react';
import { AuthProvider } from '@/contexts/AuthContext';
import ProtectedRoute from '@/components/Auth/ProtectedRoute';
import Navbar from '@/components/Layout/Navbar';
import ProductosList from '@/components/Products/ProductosList';
import VentasList from '@/components/Sales/VentasList';
import ComprasList from '@/components/Purchases/ComprasList';
import ReportesList from '@/components/Reports/ReportesList';
import Dashboard from '@/components/Dashboard/Dashboard';
import '@/styles/globals.css';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <div className="min-h-screen bg-gray-50">
        <ProtectedRoute>
          <MainApp />
        </ProtectedRoute>
      </div>
    </AuthProvider>
  );
};

const MainApp: React.FC = () => {
  const [activeView, setActiveView] = useState('dashboard');

  const renderView = () => {
    switch (activeView) {
      case 'dashboard':
        return <Dashboard />;
      case 'productos':
        return <ProductosList />;
      case 'ventas':
        return <VentasList />;
      case 'compras':
        return <ComprasList />;
      case 'reportes':
        return <ReportesList />;
      default:
        return <Dashboard />;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar activeView={activeView} setActiveView={setActiveView} />
      <main className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        {renderView()}
      </main>
    </div>
  );
};

export default App;
