import { useState, useEffect } from 'react'
import Header from './components/Header'
import SidebarMenu from './components/SidebarMenu'
import Dashboard from './components/pages/Dashboard'
import Inventario from './components/pages/Inventario'
import Compras from './components/pages/Compras'
import Ventas from './components/pages/Ventas'
import Reportes from './components/pages/Reportes'
import Login from './components/pages/Login'
import { authService } from './services/authService'
import type { ViewType } from './components/SidebarMenu'

// Tipos de vistas
const VIEWS = {
  DASHBOARD: 'DASHBOARD',
  INVENTARIO: 'INVENTARIO',
  COMPRAS: 'COMPRAS',
  VENTAS: 'VENTAS',
  REPORTES: 'REPORTES',
}

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false)
  const [loading, setLoading] = useState(true)
  const [view, setView] = useState<ViewType>('DASHBOARD')
  const [sidebarOpen, setSidebarOpen] = useState(false)

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

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen)
  }

  const handleSelectView = (newView: ViewType) => {
    setView(newView)
    setSidebarOpen(false) // Cerrar sidebar al seleccionar una vista
  }

  if (loading) return <div>Cargando...</div>

  if (!isAuthenticated) {
    // Solo renderiza el login si no está autenticado
    return <Login onLoginSuccess={handleLoginSuccess} />
  }

  let MainComponent = null;
  switch (view) {
    case 'DASHBOARD':
      MainComponent = <Dashboard />; break;
    case 'INVENTARIO':
      MainComponent = <Inventario />; break;
    case 'COMPRAS':
      MainComponent = <Compras />; break;
    case 'VENTAS':
      MainComponent = <Ventas />; break;
    case 'REPORTES':
      MainComponent = <Reportes />; break;
    default:
      MainComponent = <Dashboard />;
  }

  return (
    <div className="dashboard-container">
      <Header 
        onNavigateToLogin={handleLogout} 
        onToggleSidebar={toggleSidebar}
        onReloadDashboard={() => setView('DASHBOARD')}
      />
      <SidebarMenu 
        activeView={view} 
        onSelectView={handleSelectView}
        isOpen={sidebarOpen}
        onClose={() => setSidebarOpen(false)}
      />
      <div className={`main-content ${sidebarOpen ? 'sidebar-open' : 'sidebar-closed'}`}>
        <div className="content-area">
          {MainComponent}
        </div>
      </div>
    </div>
  )
}

export default App
