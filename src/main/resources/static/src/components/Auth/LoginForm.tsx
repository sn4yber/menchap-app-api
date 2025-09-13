import React, { useState } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { authApi } from '@/services/api';
import { LoginRequest } from '@/types';

const LoginForm: React.FC = () => {
  const { login } = useAuth();
  const [isLogin, setIsLogin] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const [formData, setFormData] = useState<LoginRequest & { email?: string; nombreCompleto?: string }>({
    username: '',
    password: '',
    email: '',
    nombreCompleto: '',
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      if (isLogin) {
        // Login
        const response = await authApi.login({
          username: formData.username,
          password: formData.password,
        });
        
        if (response.data.success && response.data.usuario) {
          login(response.data.usuario);
        } else {
          setError(response.data.message || 'Error en el login');
        }
      } else {
        // Register
        const response = await authApi.register({
          username: formData.username,
          password: formData.password,
          email: formData.email,
          nombreCompleto: formData.nombreCompleto,
        });
        
        if (response.data.success && response.data.usuario) {
          login(response.data.usuario);
        } else {
          setError(response.data.message || 'Error en el registro');
        }
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error de conexión');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <div className="mx-auto h-12 w-12 flex items-center justify-center rounded-full bg-primary-100">
            <span className="text-2xl">💼</span>
          </div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Sistema Contable Menchap
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            {isLogin ? 'Inicia sesión en tu cuenta' : 'Crea una nueva cuenta'}
          </p>
        </div>
        
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="space-y-4">
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-gray-700">
                Usuario
              </label>
              <input
                id="username"
                name="username"
                type="text"
                required
                value={formData.username}
                onChange={handleInputChange}
                className="input-field"
                placeholder="Ingresa tu usuario"
              />
            </div>
            
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                Contraseña
              </label>
              <input
                id="password"
                name="password"
                type="password"
                required
                value={formData.password}
                onChange={handleInputChange}
                className="input-field"
                placeholder="Ingresa tu contraseña"
              />
            </div>
            
            {!isLogin && (
              <>
                <div>
                  <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                    Email (opcional)
                  </label>
                  <input
                    id="email"
                    name="email"
                    type="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    className="input-field"
                    placeholder="tu@email.com"
                  />
                </div>
                
                <div>
                  <label htmlFor="nombreCompleto" className="block text-sm font-medium text-gray-700">
                    Nombre Completo (opcional)
                  </label>
                  <input
                    id="nombreCompleto"
                    name="nombreCompleto"
                    type="text"
                    value={formData.nombreCompleto}
                    onChange={handleInputChange}
                    className="input-field"
                    placeholder="Tu nombre completo"
                  />
                </div>
              </>
            )}
          </div>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {error}
            </div>
          )}

          <div>
            <button
              type="submit"
              disabled={loading}
              className="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? (
                <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
              ) : (
                isLogin ? 'Iniciar Sesión' : 'Registrarse'
              )}
            </button>
          </div>
          
          <div className="text-center">
            <button
              type="button"
              onClick={() => {
                setIsLogin(!isLogin);
                setError('');
                setFormData({ username: '', password: '', email: '', nombreCompleto: '' });
              }}
              className="text-primary-600 hover:text-primary-500 text-sm font-medium"
            >
              {isLogin 
                ? '¿No tienes cuenta? Regístrate aquí' 
                : '¿Ya tienes cuenta? Inicia sesión aquí'
              }
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginForm;
