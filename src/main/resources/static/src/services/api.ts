import axios, { AxiosResponse } from 'axios';
import { Producto, Venta, Compra, LoginRequest, LoginResponse, RegisterRequest } from '@/types';

// Configuración base de Axios
const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'X-Requested-With': 'XMLHttpRequest'
  }
});

// Interceptor para manejo de errores
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

// API de Productos
export const productosApi = {
  getAll: (): Promise<AxiosResponse<Producto[]>> => 
    api.get('/api/productos/listar'),
  
  create: (producto: Omit<Producto, 'id'>): Promise<AxiosResponse<Producto>> => 
    api.post('/api/productos/guardar', producto),
  
  update: (id: number, producto: Producto): Promise<AxiosResponse<Producto>> => 
    api.put(`/api/productos/actualizar/${id}`, producto),
  
  delete: (id: number): Promise<AxiosResponse<void>> => 
    api.delete(`/api/productos/eliminar/${id}`),
};

// API de Ventas
export const ventasApi = {
  getAll: (): Promise<AxiosResponse<Venta[]>> => 
    api.get('/api/ventas'),
  
  getToday: (): Promise<AxiosResponse<Venta[]>> => 
    api.get('/api/ventas/hoy'),
  
  create: (venta: Omit<Venta, 'id'>): Promise<AxiosResponse<Venta>> => 
    api.post('/api/ventas', venta),
  
  update: (id: number, venta: Omit<Venta, 'id'>): Promise<AxiosResponse<Venta>> => 
    api.put(`/api/ventas/${id}`, venta),
  
  delete: (id: number): Promise<AxiosResponse<void>> => 
    api.delete(`/api/ventas/${id}`),
};

// API de Compras
export const comprasApi = {
  getAll: (): Promise<AxiosResponse<Compra[]>> => 
    api.get('/api/compras'),
  
  create: (compra: Omit<Compra, 'id'>): Promise<AxiosResponse<Compra>> => 
    api.post('/api/compras', compra),
  
  update: (id: number, compra: Omit<Compra, 'id'>): Promise<AxiosResponse<Compra>> => 
    api.put(`/api/compras/${id}`, compra),
  
  delete: (id: number): Promise<AxiosResponse<void>> => 
    api.delete(`/api/compras/${id}`),
};

// API de Reportes
export const reportesApi = {
  getVentas: (fechaInicio: string, fechaFin: string): Promise<AxiosResponse<Venta[]>> => 
    api.get(`/api/reportes/ventas?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`),
  
  getCompras: (fechaInicio: string, fechaFin: string): Promise<AxiosResponse<Compra[]>> => 
    api.get(`/api/reportes/compras?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`),
};

// API de Autenticación
export const authApi = {
  login: (credentials: LoginRequest): Promise<AxiosResponse<LoginResponse>> =>
    api.post('/auth/login', credentials),
  
  register: (userData: RegisterRequest): Promise<AxiosResponse<LoginResponse>> =>
    api.post('/auth/register', userData),
};

export default api;
