import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import ClientNavbar from '../../components/ClientNavbar';
import { 
  PlusCircle, FileText, Clock, Users, TrendingDown, CheckCircle, AlertCircle,
  Sparkles, TrendingUp, Calendar, ChevronRight, Eye, X, Loader2
} from 'lucide-react';
import { tenderService } from '../../services/apiService';

const ClientTender = () => {
  const [userTenders, setUserTenders] = useState([]);
  const [selectedTender, setSelectedTender] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isClosingTender, setIsClosingTender] = useState(false);
  const [isSidebarMinimized, setIsSidebarMinimized] = useState(false);
  const [error, setError] = useState(null);

  // Fetch tenders from API
  useEffect(() => {
    const fetchClientTenders = async () => {
      try {
        setIsLoading(true);
        const response = await tenderService.getClientTenders();
        setUserTenders(response.data);
      } catch (err) {
        console.error('Failed to fetch tenders:', err);
        setError(err.response?.data?.message || 'Failed to load tenders. Please try again later.');
      } finally {
        setIsLoading(false);
      }
    };
    
    fetchClientTenders();
  }, []);

  // Sidebar state management
  useEffect(() => {
    const handleSidebarStateChange = (event) => {
      setIsSidebarMinimized(event.detail);
    };

    window.addEventListener('sidebarStateChange', handleSidebarStateChange);
    return () => {
      window.removeEventListener('sidebarStateChange', handleSidebarStateChange);
    };
  }, []);

  const handleViewDetails = (tender) => {
    setSelectedTender(tender);
    document.getElementById('tender-details-modal').showModal();
  };

  const closeTender = async (id) => {
    try {
      setIsClosingTender(true);
      await tenderService.closeTender(id);
      
      // Update UI after successful API call
      const updatedTenders = userTenders.map(tender => 
        tender.id === id ? {...tender, status: 'ended'} : tender
      );
      setUserTenders(updatedTenders);
      
      if (selectedTender && selectedTender.id === id) {
        setSelectedTender({...selectedTender, status: 'ended'});
      }
    } catch (err) {
      console.error('Error closing tender:', err);
      alert(err.response?.data?.message || 'Failed to close tender. Please try again.');
    } finally {
      setIsClosingTender(false);
    }
  };

  // Helper function to determine status badge style
  const getStatusBadge = (status) => {
    switch(status) {
      case 'new':
        return { 
          color: 'bg-blue-100 text-blue-800', 
          gradient: 'from-blue-500 to-blue-600',
          icon: <AlertCircle className="w-4 h-4 mr-1" />, 
          text: 'New' 
        };
      case 'active':
        return { 
          color: 'bg-green-100 text-green-800', 
          gradient: 'from-green-500 to-green-600',
          icon: <Users className="w-4 h-4 mr-1" />, 
          text: 'Receiving Bids' 
        };
      case 'ended':
        return { 
          color: 'bg-gray-100 text-gray-800', 
          gradient: 'from-gray-500 to-gray-600',
          icon: <CheckCircle className="w-4 h-4 mr-1" />, 
          text: 'Ended' 
        };
      default:
        return { 
          color: 'bg-yellow-100 text-yellow-800', 
          gradient: 'from-yellow-500 to-yellow-600',
          icon: <Clock className="w-4 h-4 mr-1" />, 
          text: 'Processing' 
        };
    }
  };

  return (
    <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      <ClientNavbar />
      <div 
        className={`flex-1 transition-all duration-300 ${
          isSidebarMinimized ? 'ml-20' : 'ml-80'
        }`}
      >
        <div className="max-w-7xl mx-auto p-4 sm:p-8">
          {/* Header with Create Button */}
          <div className="relative mb-12 overflow-hidden bg-white p-8 rounded-2xl shadow-xl border border-gray-100">
            <div className="relative z-10">
              <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-6">
                <div>
                  <h1 className="text-3xl font-extrabold text-gray-800 mb-2 tracking-tight">My Tenders</h1>
                  <p className="text-gray-600 mb-6">Manage your construction project tenders</p>
                </div>
                
                <Link to="/client/tender/create" className="block w-full md:w-auto">
                  <div className="relative group">
                    <div className="absolute -inset-0.5 bg-gradient-to-r from-yellow-600 to-amber-600 
                      rounded-xl blur opacity-30 group-hover:opacity-100 transition duration-500 group-hover:duration-200"></div>
                    <div className="bg-gradient-to-br from-yellow-500 to-amber-500 hover:from-yellow-600 hover:to-amber-600 
                      text-white px-6 py-4 rounded-xl shadow-lg transform transition-all duration-300 hover:-translate-y-1 
                      flex items-center justify-center sm:justify-start relative">
                      <PlusCircle className="w-6 h-6 mr-2" />
                      <span className="font-semibold text-lg">Create New Tender</span>
                      <div className="absolute right-3 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                        <ChevronRight className="w-5 h-5" />
                      </div>
                    </div>
                  </div>
                </Link>
              </div>
            </div>
            
            {/* Decorative elements */}
            <div className="absolute top-0 right-0 -mt-10 -mr-10 w-40 h-40 bg-yellow-200 rounded-full opacity-20"></div>
            <div className="absolute bottom-0 left-0 -mb-10 -ml-10 w-40 h-40 bg-amber-300 rounded-full opacity-20"></div>
            <div className="absolute top-1/2 left-1/4 transform -translate-y-1/2 w-20 h-20 bg-yellow-400 rounded-full opacity-10"></div>
          </div>
          
          {/* Loading State */}
          {isLoading ? (
            <div className="flex justify-center items-center py-12">
              <Loader2 className="animate-spin h-8 w-8 text-yellow-500" />
              <span className="ml-2 text-yellow-600">Loading tenders...</span>
            </div>
          ) : error ? (
            <div className="bg-red-50 border-l-4 border-red-400 p-4 mb-6">
              <div className="flex">
                <div className="flex-shrink-0">
                  <AlertCircle className="h-5 w-5 text-red-400" />
                </div>
                <div className="ml-3">
                  <p className="text-sm text-red-700">{error}</p>
                </div>
              </div>
            </div>
          ) : (
            <>
              {/* Tender Stats Summary */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
                <div className="bg-white p-6 rounded-xl shadow-md border border-gray-100 transform transition-all duration-300 hover:-translate-y-1 hover:shadow-lg">
                  <div className="flex items-center justify-between">
                    <h3 className="text-lg font-medium text-gray-700">Active Tenders</h3>
                    <div className="bg-yellow-100 p-3 rounded-full">
                      <FileText className="w-6 h-6 text-yellow-600" />
                    </div>
                  </div>
                  <p className="text-3xl font-bold text-gray-900 mt-2">
                    {userTenders.filter(t => t.status === 'active' || t.status === 'new').length}
                  </p>
                  <div className="h-2 w-full bg-gray-200 rounded-full mt-3 overflow-hidden">
                    <div 
                      className="h-full bg-gradient-to-r from-yellow-400 to-amber-500 rounded-full" 
                      style={{ width: `${userTenders.length > 0 ? (userTenders.filter(t => t.status === 'active' || t.status === 'new').length / userTenders.length) * 100 : 0}%` }}
                    ></div>
                  </div>
                </div>
                
                <div className="bg-white p-6 rounded-xl shadow-md border border-gray-100 transform transition-all duration-300 hover:-translate-y-1 hover:shadow-lg">
                  <div className="flex items-center justify-between">
                    <h3 className="text-lg font-medium text-gray-700">Total Bids</h3>
                    <div className="bg-green-100 p-3 rounded-full">
                      <Users className="w-6 h-6 text-green-600" />
                    </div>
                  </div>
                  <p className="text-3xl font-bold text-gray-900 mt-2">
                    {userTenders.reduce((total, tender) => total + (tender.bidsCount || 0), 0)}
                  </p>
                  <div className="flex items-center gap-1 mt-2 text-green-600">
                    <TrendingUp className="w-4 h-4" />
                    <span className="text-xs font-medium">12% increase this month</span>
                  </div>
                </div>
                
                <div className="bg-white p-6 rounded-xl shadow-md border border-gray-100 transform transition-all duration-300 hover:-translate-y-1 hover:shadow-lg">
                  <div className="flex items-center justify-between">
                    <h3 className="text-lg font-medium text-gray-700">Completed</h3>
                    <div className="bg-blue-100 p-3 rounded-full">
                      <CheckCircle className="w-6 h-6 text-blue-600" />
                    </div>
                  </div>
                  <p className="text-3xl font-bold text-gray-900 mt-2">
                    {userTenders.filter(t => t.status === 'ended').length}
                  </p>
                  <div className="flex items-center gap-1 mt-2 text-blue-600">
                    <Calendar className="w-4 h-4" />
                    <span className="text-xs font-medium">
                      {userTenders.filter(t => t.status === 'ended').length > 0
                        ? `Last ended: ${new Date(
                            Math.max(...userTenders
                              .filter(t => t.status === 'ended')
                              .map(t => new Date(t.createdAt).getTime())
                            )
                          ).toLocaleDateString()}`
                        : 'No completed tenders'
                      }
                    </span>
                  </div>
                </div>
              </div>
              
              {/* Recent Tenders Section */}
              {userTenders.length === 0 ? (
                <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8 mb-8 text-center">
                  <FileText className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                  <h2 className="text-2xl font-semibold text-gray-700 mb-2">No tenders yet</h2>
                  <p className="text-gray-500 mb-6">You have not created any tenders yet.</p>
                  <Link to="/client/tender/create" className="inline-block bg-yellow-500 hover:bg-yellow-600 text-white font-medium px-6 py-3 rounded-lg transition-colors">
                    Create Your First Tender
                  </Link>
                </div>
              ) : (
                <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8 mb-8">
                  <div className="flex justify-between items-center mb-6">
                    <div className="flex items-center gap-3">
                      <div className="p-2 bg-gradient-to-br from-yellow-500 to-amber-500 rounded-lg text-white">
                        <FileText className="w-6 h-6" />
                      </div>
                      <h2 className="text-xl font-bold text-gray-800">Recent Tenders</h2>
                    </div>
                  </div>
                  
                  <div className="overflow-hidden">
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                      {userTenders.map(tender => {
                        const statusBadge = getStatusBadge(tender.status);
                        
                        return (
                          <div 
                            key={tender.id} 
                            className="bg-white border rounded-xl overflow-hidden hover:shadow-xl transition-all duration-300 
                              cursor-pointer group hover:border-yellow-300"
                            onClick={() => handleViewDetails(tender)}
                          >
                            <div className="p-5 border-b relative">
                              {tender.status === 'new' && (
                                <div className="absolute -right-5 -top-5 bg-gradient-to-br from-blue-500 to-blue-600 
                                  text-white w-10 h-10 flex items-center justify-center transform rotate-12 
                                  shadow-lg text-xs font-medium">
                                  NEW
                                </div>
                              )}
                              
                              <div className="flex justify-between items-start">
                                <h3 className="text-lg font-semibold text-gray-800 line-clamp-1 group-hover:text-yellow-600 
                                  transition-colors duration-200">
                                  {tender.title}
                                </h3>
                                <span className={`flex items-center px-2.5 py-1 rounded-full text-xs font-medium ${statusBadge.color}`}>
                                  {statusBadge.icon}
                                  {statusBadge.text}
                                </span>
                              </div>
                              
                              <p className="mt-2 text-sm text-gray-600 line-clamp-2">{tender.description}</p>
                            </div>
                            
                            <div className="p-5 bg-gray-50 group-hover:bg-yellow-50 transition-colors duration-200">
                              <div className="grid grid-cols-2 gap-4">
                                <div>
                                  <p className="text-xs text-gray-500">Budget</p>
                                  <p className="font-semibold text-gray-800">${tender.budget.toLocaleString()}</p>
                                </div>
                                <div>
                                  <p className="text-xs text-gray-500">Deadline</p>
                                  <p className="font-semibold text-gray-800">{new Date(tender.deadline).toLocaleDateString()}</p>
                                </div>
                              </div>
                              
                              <div className="mt-4 grid grid-cols-2 gap-4">
                                <div>
                                  <p className="text-xs text-gray-500">Bids Received</p>
                                  <p className="font-semibold text-gray-800">{tender.bidsCount || 0}</p>
                                </div>
                                <div>
                                  <p className="text-xs text-gray-500">Lowest Bid</p>
                                  <p className={`font-semibold ${tender.lowestBid && tender.lowestBid < tender.budget ? 'text-green-600' : 'text-gray-800'}`}>
                                    {tender.lowestBid ? `$${tender.lowestBid.toLocaleString()}` : 'No bids yet'}
                                  </p>
                                </div>
                              </div>
                              
                              {/* Progress bar showing days remaining */}
                              {tender.status !== 'ended' && (
                                <div className="mt-4">
                                  <div className="flex justify-between text-xs text-gray-500 mb-1">
                                    <span>Days Remaining</span>
                                    <span>{Math.max(0, Math.ceil((new Date(tender.deadline) - new Date()) / (1000 * 60 * 60 * 24)))} days</span>
                                  </div>
                                  <div className="h-1.5 w-full bg-gray-200 rounded-full overflow-hidden">
                                    <div 
                                      className={`h-full rounded-full ${
                                        Math.ceil((new Date(tender.deadline) - new Date()) / (1000 * 60 * 60 * 24)) < 10 
                                        ? 'bg-red-500' 
                                        : 'bg-green-500'
                                      }`} 
                                      style={{ 
                                        width: `${Math.min(100, Math.max(0, 
                                          Math.ceil((new Date(tender.deadline) - new Date()) / (1000 * 60 * 60 * 24)) / 
                                          Math.ceil((new Date(tender.deadline) - new Date(tender.createdAt)) / (1000 * 60 * 60 * 24)) * 100
                                        ))}%` 
                                      }}
                                    ></div>
                                  </div>
                                </div>
                              )}
                              
                              <div className="mt-4 pt-3 border-t border-gray-200 flex justify-end">
                                <button className="text-yellow-600 hover:text-yellow-800 transition-colors 
                                  duration-200 text-sm font-medium flex items-center gap-1 bg-white py-1 px-3 
                                  rounded-lg shadow-sm group-hover:shadow-md">
                                  <Eye className="w-4 h-4" />
                                  View Details
                                  <ChevronRight className="w-4 h-4 opacity-0 -ml-2 group-hover:opacity-100 group-hover:ml-0 
                                    transition-all duration-300" />
                                </button>
                              </div>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>
      
      {/* Tender Details Modal */}
      <dialog id="tender-details-modal" className="modal">
        <div className="modal-box max-w-4xl bg-white rounded-xl overflow-hidden p-0">
          {selectedTender && (
            <>
              <div className="relative">
                {/* Header with status banner */}
                <div className={`w-full h-16 bg-gradient-to-r ${getStatusBadge(selectedTender.status).gradient}`}></div>
                <div className="relative px-6 pb-4">
                  <div className="bg-white rounded-xl shadow-md p-4 -mt-8">
                    <div className="flex justify-between items-start">
                      <h3 className="text-2xl font-bold text-gray-800">{selectedTender.title}</h3>
                      <span className={`flex items-center px-3 py-1 rounded-full text-sm font-medium ${getStatusBadge(selectedTender.status).color}`}>
                        {getStatusBadge(selectedTender.status).icon}
                        {getStatusBadge(selectedTender.status).text}
                      </span>
                    </div>
                    <p className="text-gray-600 mt-2">{selectedTender.description}</p>
                  </div>
                </div>
              </div>
                
              <div className="px-6 space-y-6 pb-6">
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                  <div className="bg-gray-50 p-4 rounded-lg hover:shadow-md transition-shadow duration-200">
                    <p className="text-xs text-gray-500">Budget</p>
                    <p className="font-semibold text-gray-800 text-lg">${selectedTender.budget.toLocaleString()}</p>
                  </div>
                  <div className="bg-gray-50 p-4 rounded-lg hover:shadow-md transition-shadow duration-200">
                    <p className="text-xs text-gray-500">Deadline</p>
                    <p className="font-semibold text-gray-800 text-lg">{new Date(selectedTender.deadline).toLocaleDateString()}</p>
                  </div>
                  <div className="bg-gray-50 p-4 rounded-lg hover:shadow-md transition-shadow duration-200">
                    <p className="text-xs text-gray-500">Status</p>
                    <p className="font-semibold text-gray-800 text-lg">{getStatusBadge(selectedTender.status).text}</p>
                  </div>
                  <div className="bg-gray-50 p-4 rounded-lg hover:shadow-md transition-shadow duration-200">
                    <p className="text-xs text-gray-500">Created On</p>
                    <p className="font-semibold text-gray-800 text-lg">{new Date(selectedTender.createdAt).toLocaleDateString()}</p>
                  </div>
                </div>
                
                <div className="bg-gray-50 p-6 rounded-lg border border-gray-100">
                  <div className="flex items-center gap-3 mb-4">
                    <div className="bg-yellow-100 p-2 rounded-full">
                      <TrendingDown className="w-5 h-5 text-yellow-600" />
                    </div>
                    <h4 className="font-medium text-gray-800 text-lg">Bid Summary</h4>
                  </div>
                  
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <div className="bg-white p-4 rounded-lg shadow-sm">
                      <div className="flex justify-between items-center">
                        <p className="text-xs text-gray-500">Total Bids</p>
                        <div className="bg-blue-100 w-8 h-8 rounded-full flex items-center justify-center">
                          <Users className="w-4 h-4 text-blue-600" />
                        </div>
                      </div>
                      <p className="font-semibold text-gray-800 text-xl">{selectedTender.bidsCount || 0}</p>
                    </div>
                    
                    <div className="bg-white p-4 rounded-lg shadow-sm">
                      <div className="flex justify-between items-center">
                        <p className="text-xs text-gray-500">Lowest Bid</p>
                        <div className="bg-green-100 w-8 h-8 rounded-full flex items-center justify-center">
                          <TrendingDown className="w-4 h-4 text-green-600" />
                        </div>
                      </div>
                      <p className="font-semibold text-gray-800 text-xl">
                        {selectedTender.lowestBid ? `$${selectedTender.lowestBid.toLocaleString()}` : 'No bids yet'}
                      </p>
                    </div>
                    
                    <div className="bg-white p-4 rounded-lg shadow-sm">
                      <div className="flex justify-between items-center">
                        <p className="text-xs text-gray-500">Potential Savings</p>
                        <div className="bg-yellow-100 w-8 h-8 rounded-full flex items-center justify-center">
                          <Sparkles className="w-4 h-4 text-yellow-600" />
                        </div>
                      </div>
                      <p className="font-semibold text-gray-800 text-xl">
                        {selectedTender.lowestBid ? 
                          `$${(selectedTender.budget - selectedTender.lowestBid).toLocaleString()} (${Math.round((1 - selectedTender.lowestBid/selectedTender.budget) * 100)}%)` : 
                          'N/A'}
                      </p>
                    </div>
                  </div>
                </div>
                
                <div className="flex justify-between items-center gap-4">
                  <form method="dialog">
                    <button className="btn bg-white hover:bg-gray-100 text-gray-800 gap-2 border border-gray-200 shadow-sm hover:shadow">
                      <X className="w-5 h-5" />
                      Close
                    </button>
                  </form>

                  <div>
                    {selectedTender.status !== 'ended' ? (
                      <button 
                        onClick={() => closeTender(selectedTender.id)}
                        className="btn btn-error gap-2 hover:bg-red-700" 
                        disabled={isClosingTender}
                      >
                        {isClosingTender ? (
                          <>
                            <Loader2 className="w-5 h-5 animate-spin" />
                            Processing...
                          </>
                        ) : (
                          <>
                            <X className="w-5 h-5" />
                            Close Tender
                          </>
                        )}
                      </button>
                    ) : (
                      <button className="btn bg-gray-200 text-gray-600 cursor-not-allowed" disabled>
                        <CheckCircle className="w-5 h-5 mr-2" />
                        Closed
                      </button>
                    )}
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
      </dialog>
    </div>
  );
};

export default ClientTender;