import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

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
const authService = {
  login: (username, password) => 
    api.post('/auth/signin', { username, password }),
  
  registerClient: (userData) => 
    api.post('/auth/signup/client', userData),
  
  registerCompany: (userData) => 
    api.post('/auth/signup/company', userData),
  
  verifyOTP: (email, otp) => 
    api.post('/auth/verify', { email, otp }),
};

// Company services
const companyService = {
  getAllCompanies: (params = {}) => 
    api.get('/company/portfolios', { params }),
  
  getCompanyById: (id) => 
    api.get(`/company/portfolio/${id}`),
  
  updateCompanyProfile: (id, data) => 
    api.put(`/company/portfolio/${id}`, data),
  
  createPortfolio: (formData) => 
    api.post('/company/portfolio', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
  
  getCompanyReviews: (companyId) => 
    api.get(`/reviews/company/${companyId}`),
  
  submitReview: (reviewData) => 
    api.post('/reviews', reviewData),
};

// Tender services
const tenderService = {
  getClientTenders: () => 
    api.get('/tenders/client'),
  
  getCompanyTenders: (params = {}) => 
    api.get('/tenders/company', { params }),
  
  getTenderById: (id) => 
    api.get(`/tenders/${id}`),
  
  createTender: (tenderData) => 
    api.post('/tenders', tenderData),
  
  updateTender: (id, tenderData) => 
    api.put(`/tenders/${id}`, tenderData),
  
  deleteTender: (id) => 
    api.delete(`/tenders/${id}`),
  
  submitBid: (tenderId, bidData) => 
    api.post(`/tenders/${tenderId}/bids`, bidData),
  
  getActiveBids: () => 
    api.get('/tenders/bids/active'),
  
  getTenderHeatmapData: () => 
    api.get('/tenders/map'),
  
  getTenderAnalytics: () => 
    api.get('/tenders/analytics'),
};

export { api, authService, companyService, tenderService };