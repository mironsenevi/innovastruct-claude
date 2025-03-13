import { createContext, useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/apiService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    // Check if user is stored in local storage on initial load
    const storedUser = localStorage.getItem('user');
    const token = localStorage.getItem('token');
    
    if (storedUser && token) {
      setCurrentUser(JSON.parse(storedUser));
    }
    
    setLoading(false);
  }, []);

  const login = async (username, password) => {
    try {
      setError(null);
      setLoading(true);
      const response = await authService.login(username, password);
      
      // Store token and user data
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
      
      setCurrentUser(response.data);
      
      // Redirect based on user role
      if (response.data.roles.includes('ROLE_CLIENT')) {
        navigate('/client/home');
      } else if (response.data.roles.includes('ROLE_COMPANY')) {
        navigate('/company/home');
      } else {
        navigate('/');
      }
      
      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const registerClient = async (userData) => {
    try {
      setError(null);
      setLoading(true);
      const response = await authService.registerClient(userData);
      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const registerCompany = async (userData) => {
    try {
      setError(null);
      setLoading(true);
      const response = await authService.registerCompany(userData);
      return response.data;
    } finally {
      setLoading(false);
    }
  };

  const verifyOTP = async (email, otp) => {
    try {
      setError(null);
      setLoading(true);
      const response = await authService.verifyOTP(email, otp);
      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || 'Verification failed');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setCurrentUser(null);
    navigate('/login');
  };

  const isAuthenticated = () => {
    return !!currentUser && !!localStorage.getItem('token');
  };

  const isClient = () => {
    return currentUser && currentUser.roles && currentUser.roles.includes('ROLE_CLIENT');
  };

  const isCompany = () => {
    return currentUser && currentUser.roles && currentUser.roles.includes('ROLE_COMPANY');
  };

  return (
    <AuthContext.Provider
      value={{
        currentUser,
        loading,
        error,
        login,
        logout,
        registerClient,
        registerCompany,
        verifyOTP,
        isAuthenticated,
        isClient,
        isCompany,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};