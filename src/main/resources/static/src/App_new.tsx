import React, { useState } from 'react';
import { AuthProvider } from '@/contexts/AuthContext';
import ProtectedRoute from '@/components/Auth/ProtectedRoute';
import Navbar from '@/components/Layout/Navbar';
import ProductosList from '@/components/Products/ProductosList';
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
        return <div className="p-6">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Ventas</h2>
          <p className="text-gray-600">Módulo de ventas en desarrollo...</p>
        </div>;
      case 'compras':
        return <div className="p-6">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Compras</h2>
          <p className="text-gray-600">Módulo de compras en desarrollo...</p>
        </div>;
      case 'reportes':
        return <div className="p-6">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Reportes</h2>
          <p className="text-gray-600">Módulo de reportes en desarrollo...</p>
        </div>;
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
