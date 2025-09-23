import { useState, useEffect } from 'react'
import Header from './components/Header'
import Dashboard from './components/Dashboard'
import Inventario from './components/Inventario'
import Compras from './components/Compras'
import Ventas from './components/Ventas'
import Reportes from './components/Reportes'
import Configuracion from './components/Configuracion'
import Login from './components/Login'
import { authService } from './services/authService'
import type { ViewType } from './components/SidebarMenu'

// Tipos de vistas
const VIEWS = {
  DASHBOARD: 'DASHBOARD',
  INVENTARIO: 'INVENTARIO',
  COMPRAS: 'COMPRAS',
  VENTAS: 'VENTAS',
  REPORTES: 'REPORTES',
  CONFIGURACION: 'CONFIGURACION',
}

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false)
  const [loading, setLoading] = useState(true)
  const [view, setView] = useState<ViewType>('DASHBOARD')

  useEffect(() => {
    // Verifica autenticación antes de mostrar cualquier contenido
    setIsAuthenticated(authService.isAuthenticated())
    setLoading(false)
  }, [])

  const handleLoginSuccess = () => {
    setIsAuthenticated(true)
  }

  const handleLogout = () => {
    authService.logout()
    setIsAuthenticated(false)
  }

  if (loading) return <div>Cargando...</div>

  if (!isAuthenticated) {
    // Solo renderiza el login si no está autenticado
    return <Login onLoginSuccess={handleLoginSuccess} />
  }

  let MainComponent = null;
  switch (view) {
    case 'DASHBOARD':
      MainComponent = <Dashboard activeView={view} onSelectView={setView} />; break;
    case 'INVENTARIO':
      MainComponent = <Inventario />; break;
    case 'COMPRAS':
      MainComponent = <Compras />; break;
    case 'VENTAS':
      MainComponent = <Ventas />; break;
    case 'REPORTES':
      MainComponent = <Reportes />; break;
    case 'CONFIGURACION':
      MainComponent = <Configuracion />; break;
    default:
      MainComponent = <Dashboard activeView={view} onSelectView={setView} />;
  }

  return (
    <>
      <Header onNavigateToLogin={handleLogout} />
      {MainComponent}
    </>
  )
}

export default App
