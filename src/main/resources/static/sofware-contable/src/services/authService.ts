import { httpClient } from './httpClient';
import type { LoginRequest, LoginResponse, Usuario } from '../types/api';

class AuthService {
  private readonly AUTH_ENDPOINTS = {
    LOGIN: '/auth/login',
    LOGOUT: '/auth/logout',
    VERIFY: '/auth/verify'
  };

  private currentUser: Usuario | null = null;
  private token: string | null = null;

  /**
   * Realiza el login del usuario
   */
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    try {
      const response = await httpClient.post<LoginResponse>(
        this.AUTH_ENDPOINTS.LOGIN,
        credentials
      );

      if (response.success && response.usuario) {
        this.currentUser = response.usuario;
        if (response.token) {
          this.token = response.token;
          this.saveToken(response.token);
        }
        this.saveUser(response.usuario);
      }

      return response;
    } catch (error: any) {
      console.error('Error en login:', error);
      throw new Error(error.message || 'Error al iniciar sesión');
    }
  }

  /**
   * Cierra la sesión del usuario
   */
  logout(): void {
    this.currentUser = null;
    this.token = null;
    this.removeToken();
    this.removeUser();
  }

  /**
   * Obtiene el usuario actual
   */
  getCurrentUser(): Usuario | null {
    if (!this.currentUser) {
      const savedUser = this.getSavedUser();
      if (savedUser) {
        this.currentUser = savedUser;
      }
    }
    return this.currentUser;
  }

  /**
   * Verifica si el usuario está autenticado
   */
  isAuthenticated(): boolean {
    return this.getCurrentUser() !== null;
  }

  /**
   * Obtiene el token de autenticación
   */
  getToken(): string | null {
    if (!this.token) {
      this.token = this.getSavedToken();
    }
    return this.token;
  }

  /**
   * Verifica si el usuario tiene un rol específico
   */
  hasRole(role: 'ADMIN' | 'USER'): boolean {
    const user = this.getCurrentUser();
    return user?.rol === role;
  }

  /**
   * Verifica si el usuario es administrador
   */
  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  // Métodos privados para manejo de localStorage
  private saveToken(token: string): void {
    localStorage.setItem('auth_token', token);
  }

  private getSavedToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  private removeToken(): void {
    localStorage.removeItem('auth_token');
  }

  private saveUser(user: Usuario): void {
    localStorage.setItem('current_user', JSON.stringify(user));
  }

  private getSavedUser(): Usuario | null {
    const savedUser = localStorage.getItem('current_user');
    try {
      return savedUser ? JSON.parse(savedUser) : null;
    } catch {
      return null;
    }
  }

  private removeUser(): void {
    localStorage.removeItem('current_user');
  }
}

// Instancia singleton del servicio de autenticación
export const authService = new AuthService();
export default AuthService;
