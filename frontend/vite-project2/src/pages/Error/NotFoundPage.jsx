import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { ArrowLeft, AlertTriangle, Home } from 'lucide-react';

const NotFoundPage = () => {
  const { isAuthenticated, isClient, isCompany } = useAuth();
  
  // Determine redirect path based on user role
  const getHomePath = () => {
    if (!isAuthenticated()) return '/login';
    if (isClient()) return '/client/home';
    if (isCompany()) return '/company/home';
    return '/';
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4 py-12">
      <div className="max-w-lg w-full text-center">
        <div className="mb-8 flex justify-center">
          <div className="w-24 h-24 bg-yellow-100 rounded-full flex items-center justify-center">
            <AlertTriangle className="h-12 w-12 text-yellow-600" />
          </div>
        </div>
        
        <h1 className="text-6xl font-extrabold text-gray-900 mb-4">404</h1>
        <h2 className="text-3xl font-bold text-gray-800 mb-6">Page Not Found</h2>
        
        <p className="text-gray-600 mb-8">
          The page you're looking for doesn't exist or has been moved.
          Check the URL or navigate back to the dashboard.
        </p>
        
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Link 
            to={-1}
            className="inline-flex items-center justify-center gap-2 px-6 py-3 border border-transparent 
              text-base font-medium rounded-md text-yellow-700 bg-yellow-100 hover:bg-yellow-200 
              transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500"
          >
            <ArrowLeft className="w-5 h-5" />
            Go Back
          </Link>
          
          <Link 
            to={getHomePath()}
            className="inline-flex items-center justify-center gap-2 px-6 py-3 border border-transparent 
              text-base font-medium rounded-md text-white bg-yellow-600 hover:bg-yellow-700 
              transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500"
          >
            <Home className="w-5 h-5" />
            Back to Dashboard
          </Link>
        </div>
        
        {/* Decorative elements */}
        <div className="absolute top-1/4 right-1/4 w-64 h-64 bg-yellow-200 rounded-full opacity-10 -z-10 blur-3xl"></div>
        <div className="absolute bottom-1/4 left-1/4 w-64 h-64 bg-amber-300 rounded-full opacity-10 -z-10 blur-3xl"></div>
      </div>
    </div>
  );
};

export default NotFoundPage;