import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import ClientNavbar from '../../components/ClientNavbar';
import Filter from '../../components/CompanyProfile/Filter';
import { Star, Search, Loader, AlertCircle } from 'lucide-react';
import { companyAPI } from '../../services/apiService';

const RegisteredCompaniesPage = () => {
  const [isSidebarMinimized, setIsSidebarMinimized] = useState(false);
  const [companies, setCompanies] = useState([]);
  const [filteredCompanies, setFilteredCompanies] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [filters, setFilters] = useState({
    type: '',
    location: '',
    rating: '',
    employeeSize: '',
    established: ''
  });

  // Fetch companies from API
  useEffect(() => {
    const fetchCompanies = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const response = await companyAPI.getAllCompanies();
        const companiesData = response.data;
        setCompanies(companiesData);
        setFilteredCompanies(companiesData);
      } catch (err) {
        console.error("Failed to fetch companies:", err);
        setError(err.response?.data?.message || "Failed to load companies. Please try again later.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchCompanies();
  }, []);

  useEffect(() => {
    const handleSidebarStateChange = (event) => {
      setIsSidebarMinimized(event.detail);
    };

    window.addEventListener('sidebarStateChange', handleSidebarStateChange);
    return () => {
      window.removeEventListener('sidebarStateChange', handleSidebarStateChange);
    };
  }, []);

  const handleFilterChange = async (newFilters) => {
    setIsLoading(true);
    setFilters(newFilters);
    
    try {
      // Call API with filters
      const response = await companyAPI.getAllCompanies(newFilters);
      let results = response.data;
      
      // Apply search query if exists
      if (searchQuery) {
        results = results.filter(company => 
          company.companyName.toLowerCase().includes(searchQuery.toLowerCase()) ||
          company.shortDescription?.toLowerCase().includes(searchQuery.toLowerCase())
        );
      }
      
      setFilteredCompanies(results);
    } catch (err) {
      console.error("Error applying filters:", err);
      setError("Failed to filter companies. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleSearch = async (e) => {
    const query = e.target.value;
    setSearchQuery(query);
    
    if (query === '') {
      handleFilterChange(filters); // Reset to current filters only
      return;
    }
    
    setIsLoading(true);
    
    try {
      // Apply search on existing filtered results
      const results = companies.filter(company => 
        company.companyName.toLowerCase().includes(query.toLowerCase()) ||
        company.shortDescription?.toLowerCase().includes(query.toLowerCase())
      );
      setFilteredCompanies(results);
    } catch (err) {
      console.error("Error searching companies:", err);
    } finally {
      setIsLoading(false);
    }
  };
  
  const renderStars = (rating) => {
    return (
      <div className="flex items-center mt-2">
        {[...Array(5)].map((_, i) => (
          <Star
            key={i}
            className={`h-5 w-5 ${
              i < Math.round(rating) ? 'text-yellow-400 fill-yellow-400' : 'text-gray-300'
            }`}
          />
        ))}
        <span className="ml-2 text-gray-600">{rating.toFixed(1)}</span>
      </div>
    );
  };

  return (
    <div className="flex">
      <ClientNavbar />
      <div 
        className={`flex-1 transition-all duration-300 ${
          isSidebarMinimized ? 'ml-20' : 'ml-80'
        }`}
      >
        <div className="container mx-auto p-4">
          <h1 className="text-3xl font-bold text-center mb-8">
            Registered Construction Companies
          </h1>
          <div className="mb-6">
            <div className="relative">
              <input
                type="text"
                placeholder="Search companies by name..."
                value={searchQuery}
                onChange={handleSearch}
                className="w-full p-4 pl-12 pr-4 text-gray-900 border rounded-lg focus:ring-2 focus:ring-yellow-400 focus:border-yellow-400"
              />
              <div className="absolute inset-y-0 left-0 flex items-center pl-4 pointer-events-none">
                <Search className="w-5 h-5 text-gray-500" />
              </div>
            </div>
          </div>
          
          <div className="flex flex-col md:flex-row gap-6">
            <div className="md:w-1/4">
              <Filter onFilterChange={handleFilterChange} />
            </div>
            
            <div className="md:w-3/4">
              {error && (
                <div className="bg-red-50 p-4 rounded-lg text-red-800 mb-6">
                  <div className="flex">
                    <AlertCircle className="h-5 w-5 mr-2" />
                    <div>
                      <p className="font-medium">Error loading companies</p>
                      <p>{error}</p>
                    </div>
                  </div>
                </div>
              )}
              
              {isLoading ? (
                <div className="flex justify-center items-center h-64">
                  <Loader className="animate-spin h-8 w-8 text-yellow-500" />
                </div>
              ) : filteredCompanies.length === 0 ? (
                <div className="text-center py-12 bg-gray-50 rounded-lg">
                  <h3 className="text-lg font-medium text-gray-900">No companies found</h3>
                  <p className="mt-2 text-gray-500">Try adjusting your search or filter criteria</p>
                  <button 
                    onClick={() => {
                      setSearchQuery('');
                      setFilters({});
                      handleFilterChange({});
                    }} 
                    className="mt-4 text-yellow-600 hover:text-yellow-500 font-medium"
                  >
                    Clear all filters
                  </button>
                </div>
              ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
                  {filteredCompanies.map((company) => (
                    <Link to={`/client/companies/${company.id}`} key={company.id} className="block h-full">
                      <div className="bg-white shadow-xl h-full hover:shadow-2xl transition-shadow rounded-xl overflow-hidden">
                        <figure className="h-48 w-full">
                          <img 
                            src={company.coverImage || "https://placehold.co/400x200?text=No+Image"} 
                            alt={company.companyName} 
                            className="w-full h-full object-cover" 
                          />
                        </figure>
                        <div className="p-6 flex flex-col justify-between h-[calc(100%-12rem)]">
                          <div>
                            <h2 className="text-xl font-bold mb-2">{company.companyName}</h2>
                            <p className="text-gray-600 line-clamp-2 mb-4">{company.shortDescription}</p>
                          </div>
                          <div className="flex justify-between items-center">
                            {renderStars(company.rating || 0)}
                            <div className="badge badge-outline">{company.location}</div>
                          </div>
                        </div>
                      </div>
                    </Link>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisteredCompaniesPage;