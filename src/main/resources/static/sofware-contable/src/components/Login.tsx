import React, { useState } from 'react';
import { authService } from '../services/authService';
import type { LoginRequest } from '../types/api';

interface LoginFormData {
  username: string;
  password: string;
  remember: boolean;
}

interface LoginProps {
  onLoginSuccess: () => void;
}

const Login: React.FC<LoginProps> = ({ onLoginSuccess }) => {
  const [formData, setFormData] = useState<LoginFormData>({
    username: '',
    password: '',
    remember: false,
  });
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>('');

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const credentials: LoginRequest = {
        username: formData.username,
        password: formData.password,
      };

      const response = await authService.login(credentials);

      if (response.success && response.usuario) {
        console.log('Login exitoso:', response);
        // Redirigir al dashboard
        onLoginSuccess();
      } else {
        setError(response.message || 'Error al iniciar sesión');
      }
      
    } catch (err: any) {
      console.error('Error en login:', err);
      setError(err.message || 'Error al iniciar sesión');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        {/* Header */}
        <div className="login-header">
          <h2 className="login-title">
            Iniciar Sesión
          </h2>
          <p className="login-subtitle">
            Accede a tu cuenta de Livo
          </p>
        </div>

        {/* Error Message */}
        {error && (
          <div className="error-message">
            <p>{error}</p>
          </div>
        )}

        {/* Login Form */}
        <form onSubmit={handleSubmit}>
          {/* Username Field */}
          <div className="form-group">
            <label htmlFor="username" className="form-label">
              Usuario
            </label>
            <input
              id="username"
              name="username"
              type="text"
              required
              value={formData.username}
              onChange={handleInputChange}
              placeholder="nombre_usuario"
              className="form-input"
            />
          </div>

          {/* Password Field */}
          <div className="form-group">
            <label htmlFor="password" className="form-label">
              Contraseña
            </label>
            <input
              id="password"
              name="password"
              type="password"
              required
              value={formData.password}
              onChange={handleInputChange}
              placeholder="••••••••••••"
              className="form-input"
            />
          </div>

          {/* Remember me and Forgot password */}
          <div className="remember-forgot">
            <div className="remember-group">
              <input
                id="remember"
                name="remember"
                type="checkbox"
                checked={formData.remember}
                onChange={handleInputChange}
                className="checkbox"
              />
              <label htmlFor="remember" className="checkbox-label">
                Recordarme
              </label>
            </div>

            <div>
              <a href="#" className="forgot-link">
                ¿Olvidaste tu contraseña?
              </a>
            </div>
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            disabled={loading}
            className="login-button"
          >
            {loading ? (
              <div className="loading-spinner"></div>
            ) : null}
            {loading ? 'Iniciando...' : 'Iniciar Sesión'}
          </button>
        </form>

        {/* Footer */}
        <div className="login-footer">
          <p className="footer-text">
            ¿No tienes cuenta?{' '}
            <a href="#" className="signup-link">
              Contacta a tu administrador
            </a>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
