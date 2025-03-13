import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { AlertCircle, CheckCircle, Clock } from 'lucide-react';

const VerifyOTP = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { verifyOTP, error } = useAuth();
  const [otp, setOtp] = useState('');
  const [email, setEmail] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [success, setSuccess] = useState(false);
  const [countdown, setCountdown] = useState(60);
  const [canResend, setCanResend] = useState(false);
  
  useEffect(() => {
    // Get email from location state
    if (location.state?.email) {
      setEmail(location.state.email);
    } else {
      // Redirect to login if no email is provided
      navigate('/login');
    }
  }, [location, navigate]);
  
  // Countdown for resend OTP
  useEffect(() => {
    if (countdown > 0 && !canResend) {
      const timer = setTimeout(() => {
        setCountdown(countdown - 1);
      }, 1000);
      return () => clearTimeout(timer);
    } else if (countdown === 0 && !canResend) {
      setCanResend(true);
    }
  }, [countdown, canResend]);
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (otp.length !== 6) {
      return;
    }
    
    setIsSubmitting(true);
    
    try {
      const response = await verifyOTP(email, otp);
      setSuccess(true);
      
      // Navigate to appropriate page based on user role after 2 seconds
      setTimeout(() => {
        navigate('/login');
      }, 2000);
      
    } catch (err) {
      // Error is handled in auth context
    } finally {
      setIsSubmitting(false);
    }
  };
  
  const handleResendOTP = async () => {
    setCanResend(false);
    setCountdown(60);
    
    try {
      // Call the resend OTP API
      // For now, just show a message (you can implement the actual API call later)
      alert(`A new OTP has been sent to ${email}`);
    } catch (err) {
      console.error('Failed to resend OTP:', err);
    }
  };
  
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">Verify Your Account</h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Enter the verification code sent to {email}
          </p>
        </div>
        
        {error && (
          <div className="bg-red-50 border-l-4 border-red-400 p-4">
            <div className="flex">
              <div className="flex-shrink-0">
                <AlertCircle className="h-5 w-5 text-red-400" />
              </div>
              <div className="ml-3">
                <p className="text-sm text-red-700">{error}</p>
              </div>
            </div>
          </div>
        )}
        
        {success && (
          <div className="bg-green-50 border-l-4 border-green-400 p-4">
            <div className="flex">
              <div className="flex-shrink-0">
                <CheckCircle className="h-5 w-5 text-green-400" />
              </div>
              <div className="ml-3">
                <p className="text-sm text-green-700">
                  Verification successful! Redirecting to login...
                </p>
              </div>
            </div>
          </div>
        )}
        
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="rounded-md shadow-sm -space-y-px">
            <div>
              <label htmlFor="otp" className="sr-only">Verification Code</label>
              <input
                id="otp"
                name="otp"
                type="text"
                required
                maxLength={6}
                pattern="\d{6}"
                className="appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-yellow-500 focus:border-yellow-500 focus:z-10 sm:text-sm text-center tracking-widest text-lg"
                placeholder="Enter 6-digit OTP"
                value={otp}
                onChange={(e) => {
                  const value = e.target.value;
                  // Only allow digits
                  if (/^\d*$/.test(value) && value.length <= 6) {
                    setOtp(value);
                  }
                }}
              />
            </div>
          </div>
          
          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <Clock className="h-4 w-4 text-gray-500 mr-2" />
              <span className="text-sm text-gray-500">
                {canResend ? 'You can request a new code now' : `Resend code in ${countdown} seconds`}
              </span>
            </div>
            
            <button
              type="button"
              className={`text-sm font-medium ${
                canResend 
                  ? 'text-yellow-600 hover:text-yellow-500'
                  : 'text-gray-400 cursor-not-allowed'
              }`}
              disabled={!canResend}
              onClick={handleResendOTP}
            >
              Resend Code
            </button>
          </div>
          
          <div>
            <button
              type="submit"
              disabled={isSubmitting || otp.length !== 6}
              className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-yellow-600 hover:bg-yellow-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500 disabled:opacity-50"
            >
              {isSubmitting ? 'Verifying...' : 'Verify'}
            </button>
          </div>
        </form>
        
        <p className="mt-2 text-center text-sm text-gray-600">
          Entered the wrong email?{' '}
          <button 
            onClick={() => navigate(-1)} 
            className="font-medium text-yellow-600 hover:text-yellow-500"
          >
            Go Back
          </button>
        </p>
      </div>
    </div>
  );
};

export default VerifyOTP;