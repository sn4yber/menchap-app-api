import { useState, useEffect } from 'react';
import { authService } from '../services/authService';
import type { Usuario } from '../types/api';

export interface UseAuthReturn {
  user: Usuario | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  loading: boolean;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
}

export const useAuth = (): UseAuthReturn => {
  const [user, setUser] = useState<Usuario | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Verificar si hay un usuario guardado al cargar
    const currentUser = authService.getCurrentUser();
    setUser(currentUser);
    setLoading(false);
  }, []);

  const login = async (username: string, password: string): Promise<boolean> => {
    try {
      const response = await authService.login({ username, password });
      
      if (response.success && response.usuario) {
        setUser(response.usuario);
        return true;
      }
      
      return false;
    } catch (error) {
      console.error('Error en login:', error);
      return false;
    }
  };

  const logout = (): void => {
    authService.logout();
    setUser(null);
  };

  return {
    user,
    isAuthenticated: user !== null,
    isAdmin: authService.isAdmin(),
    loading,
    login,
    logout,
  };
};
