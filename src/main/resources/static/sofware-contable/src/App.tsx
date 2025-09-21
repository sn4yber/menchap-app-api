import { useState } from 'react'
import Dashboard from './components/Dashboard'
import Login from './components/Login'

function App() {
  const [currentView, setCurrentView] = useState<'dashboard' | 'login'>('dashboard')

  const handleNavigation = (view: 'dashboard' | 'login') => {
    setCurrentView(view)
  }

  const handleReloadDashboard = () => {
    // Forzar re-render del dashboard
    setCurrentView('login')
    setTimeout(() => setCurrentView('dashboard'), 0)
  }

  return (
    <>
      {currentView === 'dashboard' && (
        <Dashboard 
          onNavigateToLogin={() => handleNavigation('login')} 
          onReloadDashboard={handleReloadDashboard}
        />
      )}
      {currentView === 'login' && <Login />}
    </>
  )
}

export default App
