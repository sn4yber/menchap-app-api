// Tipos para las peticiones y respuestas del API

export interface LoginRequest {
  username: string;
  password: string;
}

export interface Usuario {
  id: number;
  username: string;
  email: string;
  nombreCompleto: string;
  rol: 'ADMIN' | 'USER';
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;
  ultimoAcceso?: string;
}

export interface LoginResponse {
  success: boolean;
  message: string;
  usuario?: Usuario;
  token?: string;
}

export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
  error?: string;
}

export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
}
