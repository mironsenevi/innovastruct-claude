import axios from 'axios';


const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to attach token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Add response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const { response } = error;
    
    // Handle token expiration
    if (response && response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    
    return Promise.reject(error);
  }
);

// Auth services
export const authService = {
  login: (email, password) => 
    api.post('/auth/login', { email, password }),
  
  registerClient: (userData) => 
    api.post('/auth/register/client', userData),
  
  registerCompany: (userData) => 
    api.post('/auth/register/company', userData),
  
  verifyOTP: (email, otp) => 
    api.post('/auth/verify', { email, otp }),
  
  resendOTP: (email) => 
    api.post('/auth/resend-otp', { email }),
    
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
  
  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }
};

// Company services
export const companyService = {
  getAllCompanies: (filters = {}) => 
    api.get('/company/portfolios', { params: filters }),
  
  getCompanyById: (id) => 
    api.get(`/company/portfolio/${id}`),
  
  getCompanyByUserId: () => 
    api.get(`/company/portfolio/me`),
  
  createCompanyProfile: (formData) => 
    api.post('/company/portfolio', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }),
  
  updateCompanyProfile: (id, formData) => 
    api.put(`/company/portfolio/${id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }),
  
  deleteCompanyProfile: (id) => 
    api.delete(`/company/portfolio/${id}`),
    
  uploadProfileImage: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/company/portfolio/profile-image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
  
  uploadCoverImage: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/company/portfolio/cover-image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
};

// Tender services
export const tenderService = {
  getClientTenders: () => 
    api.get('/tenders/client'),
  
  getCompanyTenders: (filters = {}) => 
    api.get('/tenders', { params: filters }),
  
  getTenderById: (id) => 
    api.get(`/tenders/${id}`),
  
  createTender: (formData) => 
    api.post('/tenders', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }),
  
  updateTender: (id, formData) => 
    api.put(`/tenders/${id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }),
  
  deleteTender: (id) => 
    api.delete(`/tenders/${id}`),
  
  submitBid: (tenderId, formData) => 
    api.post(`/tenders/${tenderId}/bids`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }),
  
  getBidsForTender: (tenderId) =>
    api.get(`/tenders/${tenderId}/bids`),
    
  getCompanyBids: () => 
    api.get('/tenders/bids/company'),
  
  awardBid: (tenderId, bidId) => 
    api.put(`/tenders/${tenderId}/bids/${bidId}/award`),
    
  closeTender: (tenderId) =>
    api.put(`/tenders/${tenderId}/close`),
  
  getTenderAnalytics: () => 
    api.get('/analytics/tenders'),
};

// Review services
export const reviewService = {
  getCompanyReviews: (companyId) => 
    api.get(`/reviews/company/${companyId}`),
  
  submitReview: (reviewData) => 
    api.post('/reviews', reviewData),
    
  updateReview: (reviewId, reviewData) => 
    api.put(`/reviews/${reviewId}`, reviewData),
    
  deleteReview: (reviewId) => 
    api.delete(`/reviews/${reviewId}`),
};
export const tenderAPI = tenderService;
export const companyAPI = companyService;
export { api };