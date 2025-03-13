import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import ClientNavbar from "../../components/ClientNavbar";
import { Upload, FileText, Users, Clock, Building, AlertCircle, CheckCircle } from 'lucide-react';
import { tenderAPI } from '../../services/apiService';
import { useAuth } from '../../context/AuthContext';

export default function CreateTender() {
  const navigate = useNavigate();
  const { currentUser } = useAuth();
  
  const [tender, setTender] = useState({
    title: "",
    description: "",
    location: "",
    category: "",
    priority: "medium",
    budget: "",
    deadline: "",
    status: "open",
  });
  
  const [files, setFiles] = useState({
    plan: null,
    boq: null,
    additionalDocs: []
  });
  
  const [isSidebarMinimized, setIsSidebarMinimized] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    const handleSidebarStateChange = (event) => {
      setIsSidebarMinimized(event.detail);
    };

    window.addEventListener('sidebarStateChange', handleSidebarStateChange);
    return () => {
      window.removeEventListener('sidebarStateChange', handleSidebarStateChange);
    };
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setTender({ ...tender, [name]: value });
  };

  const handleFileChange = (e) => {
    const { name, files: selectedFiles } = e.target;
    
    if (name === 'additionalDocs') {
      setFiles(prev => ({
        ...prev,
        additionalDocs: [...prev.additionalDocs, ...Array.from(selectedFiles)]
      }));
    } else {
      setFiles(prev => ({
        ...prev,
        [name]: selectedFiles[0]
      }));
    }
  };

  const removeFile = (type, index) => {
    if (type === 'additionalDocs') {
      setFiles(prev => ({
        ...prev,
        additionalDocs: prev.additionalDocs.filter((_, i) => i !== index)
      }));
    } else {
      setFiles(prev => ({
        ...prev,
        [type]: null
      }));
    }
  };

  const validateForm = () => {
    if (!tender.title.trim()) return "Title is required";
    if (!tender.description.trim()) return "Description is required";
    if (!tender.location.trim()) return "Location is required";
    if (!tender.category.trim()) return "Category is required";
    if (!tender.budget || isNaN(Number(tender.budget))) return "Valid budget is required";
    if (!tender.deadline) return "Deadline is required";
    if (new Date(tender.deadline) < new Date()) return "Deadline must be in the future";
    
    // Optional file validation
    if (files.plan && files.plan.size > 10 * 1024 * 1024) return "Plan file size must be less than 10MB";
    if (files.boq && files.boq.size > 10 * 1024 * 1024) return "BOQ file size must be less than 10MB";
    
    return null;
  };

  // Focus on the handleSubmit function:

const handleSubmit = async (e) => {
  e.preventDefault();
  
  const validationError = validateForm();
  if (validationError) {
    setError(validationError);
    return;
  }
  
  setIsSubmitting(true);
  setError(null);
  
  try {
    // Create FormData object
    const formData = new FormData();
    
    // Add tender data as JSON string
    const tenderData = {
      ...tender,
      budget: Number(tender.budget),
    };
    formData.append('tenderData', JSON.stringify(tenderData));
    
    // Add files
    if (files.plan) formData.append('plan', files.plan);
    if (files.boq) formData.append('boq', files.boq);
    
    if (files.additionalDocs.length > 0) {
      files.additionalDocs.forEach(doc => {
        formData.append('additionalDocs', doc);
      });
    }
    
    // Submit the form
    const response = await tenderService.createTender(formData);
    setSuccess(true);
    
    // Redirect to tenders list after 2 seconds
    setTimeout(() => {
      navigate('/client/tender');
    }, 2000);
    
  } catch (err) {
    console.error('Error creating tender:', err);
    setError(err.response?.data?.message || 'Failed to create tender. Please try again.');
  } finally {
    setIsSubmitting(false);
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
          {/* Header Section */}
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-2">
              <Building className="h-8 w-8 text-yellow-500" />
              Create Tender
            </h1>
            <p className="text-gray-600 mt-2">Create a new tender for your project</p>
          </div>

          {/* Error message */}
          {error && (
            <div className="mb-6 bg-red-50 border-l-4 border-red-400 p-4">
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

          {/* Success message */}
          {success && (
            <div className="mb-6 bg-green-50 border-l-4 border-green-400 p-4">
              <div className="flex">
                <div className="flex-shrink-0">
                  <CheckCircle className="h-5 w-5 text-green-400" />
                </div>
                <div className="ml-3">
                  <p className="text-sm text-green-700">Tender created successfully! Redirecting...</p>
                </div>
              </div>
            </div>
          )}

          {/* Main Form */}
          <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-lg p-6">
            {/* Project Details Section */}
            <div className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-700">Title</label>
                <input
                  type="text"
                  name="title"
                  value={tender.title}
                  onChange={handleChange}
                  className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-yellow-500 focus:outline-none focus:ring-yellow-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">Description</label>
                <textarea
                  name="description"
                  value={tender.description}
                  onChange={handleChange}
                  rows={4}
                  className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-yellow-500 focus:outline-none focus:ring-yellow-500"
                  required
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Location</label>
                  <input
                    type="text"
                    name="location"
                    value={tender.location}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-yellow-500 focus:outline-none focus:ring-yellow-500"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">Category</label>
                  <select
                    name="category"
                    value={tender.category}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-yellow-500 focus:outline-none focus:ring-yellow-500"
                    required
                  >
                    <option value="">Select a category</option>
                    <option value="Residential">Residential</option>
                    <option value="Commercial">Commercial</option>
                    <option value="Industrial">Industrial</option>
                    <option value="Infrastructure">Infrastructure</option>
                    <option value="Hospitality">Hospitality</option>
                    <option value="Renovation">Renovation</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Budget (LKR)</label>
                  <input
                    type="number"
                    name="budget"
                    value={tender.budget}
                    onChange={handleChange}
                    min="1000"
                    className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-yellow-500 focus:outline-none focus:ring-yellow-500"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">Priority</label>
                  <select
                    name="priority"
                    value={tender.priority}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-yellow-500 focus:outline-none focus:ring-yellow-500"
                  >
                    <option value="low">Low</option>
                    <option value="medium">Medium</option>
                    <option value="high">High</option>
                    <option value="hot">Hot</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">Deadline</label>
                  <input
                    type="date"
                    name="deadline"
                    value={tender.deadline}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-yellow-500 focus:outline-none focus:ring-yellow-500"
                    min={new Date().toISOString().split('T')[0]}
                    required
                  />
                </div>
              </div>

              {/* File Upload Sections */}
              <div className="space-y-6">
                {/* Plan Upload */}
                <div>
                  <label className="block text-sm font-medium text-gray-700">Plan Document</label>
                  {files.plan ? (
                    <div className="mt-1 flex items-center justify-between bg-gray-50 p-3 rounded-md">
                      <div className="flex items-center">
                        <FileText className="h-5 w-5 text-gray-400 mr-2" />
                        <span className="text-sm text-gray-700">{files.plan.name}</span>
                      </div>
                      <button
                        type="button"
                        onClick={() => removeFile('plan')}
                        className="text-red-500 hover:text-red-700"
                      >
                        Remove
                      </button>
                    </div>
                  ) : (
                    <div className="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-300 border-dashed rounded-md hover:border-yellow-500 transition-colors">
                      <div className="space-y-1 text-center">
                        <Upload className="mx-auto h-12 w-12 text-gray-400" />
                        <div className="flex text-sm text-gray-600">
                          <label htmlFor="plan-upload" className="relative cursor-pointer rounded-md font-medium text-yellow-600 hover:text-yellow-500">
                            <span>Upload Plan</span>
                            <input
                              id="plan-upload"
                              name="plan"
                              type="file"
                              className="sr-only"
                              onChange={handleFileChange}
                              accept=".pdf,.doc,.docx,.dwg"
                            />
                          </label>
                          <p className="pl-1">or drag and drop</p>
                        </div>
                        <p className="text-xs text-gray-500">PDF, DOC, DOCX, DWG up to 10MB</p>
                      </div>
                    </div>
                  )}
                </div>

                {/* BOQ Upload */}
                <div>
                  <label className="block text-sm font-medium text-gray-700">Bill of Quantities (BOQ)</label>
                  {files.boq ? (
                    <div className="mt-1 flex items-center justify-between bg-gray-50 p-3 rounded-md">
                      <div className="flex items-center">
                        <FileText className="h-5 w-5 text-gray-400 mr-2" />
                        <span className="text-sm text-gray-700">{files.boq.name}</span>
                      </div>
                      <button
                        type="button"
                        onClick={() => removeFile('boq')}
                        className="text-red-500 hover:text-red-700"
                      >
                        Remove
                      </button>
                    </div>
                  ) : (
                    <div className="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-300 border-dashed rounded-md hover:border-yellow-500 transition-colors">
                      <div className="space-y-1 text-center">
                        <Upload className="mx-auto h-12 w-12 text-gray-400" />
                        <div className="flex text-sm text-gray-600">
                          <label htmlFor="boq-upload" className="relative cursor-pointer rounded-md font-medium text-yellow-600 hover:text-yellow-500">
                            <span>Upload BOQ</span>
                            <input
                              id="boq-upload"
                              name="boq"
                              type="file"
                              className="sr-only"
                              onChange={handleFileChange}
                              accept=".pdf,.doc,.docx,.xls,.xlsx"
                            />
                          </label>
                          <p className="pl-1">or drag and drop</p>
                        </div>
                        <p className="text-xs text-gray-500">PDF, DOC, DOCX, XLS, XLSX up to 10MB</p>
                      </div>
                    </div>
                  )}
                </div>

                {/* Additional Documents Upload */}
                <div>
                  <label className="block text-sm font-medium text-gray-700">Additional Documents (Optional)</label>
                  {files.additionalDocs.length > 0 && (
                    <div className="mt-1 mb-3 space-y-2">
                      {files.additionalDocs.map((file, index) => (
                        <div key={index} className="flex items-center justify-between bg-gray-50 p-3 rounded-md">
                          <div className="flex items-center">
                            <FileText className="h-5 w-5 text-gray-400 mr-2" />
                            <span className="text-sm text-gray-700">{file.name}</span>
                          </div>
                          <button
                            type="button"
                            onClick={() => removeFile('additionalDocs', index)}
                            className="text-red-500 hover:text-red-700"
                          >
                            Remove
                          </button>
                        </div>
                      ))}
                    </div>
                  )}
                  
                  <div className="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-300 border-dashed rounded-md hover:border-yellow-500 transition-colors">
                    <div className="space-y-1 text-center">
                      <Upload className="mx-auto h-12 w-12 text-gray-400" />
                      <div className="flex text-sm text-gray-600">
                        <label htmlFor="additional-upload" className="relative cursor-pointer rounded-md font-medium text-yellow-600 hover:text-yellow-500">
                          <span>Upload files</span>
                          <input
                            id="additional-upload"
                            name="additionalDocs"
                            type="file"
                            className="sr-only"
                            onChange={handleFileChange}
                            multiple
                            accept=".pdf,.doc,.docx,.xls,.xlsx,.jpg,.jpeg,.png"
                          />
                        </label>
                        <p className="pl-1">or drag and drop</p>
                      </div>
                      <p className="text-xs text-gray-500">PDF, DOC, DOCX, XLS, XLSX, JPG, JPEG, PNG up to 10MB each</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Info Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-8">
              <div className="bg-yellow-50 p-4 rounded-lg">
                <Clock className="h-8 w-8 text-yellow-500 mb-2" />
                <h3 className="font-semibold">Quick Response</h3>
                <p className="text-sm text-gray-600">Receive bids within 48 hours</p>
              </div>
              <div className="bg-yellow-50 p-4 rounded-lg">
                <Users className="h-8 w-8 text-yellow-500 mb-2" />
                <h3 className="font-semibold">Verified Contractors</h3>
                <p className="text-sm text-gray-600">Access to trusted professionals</p>
              </div>
              <div className="bg-yellow-50 p-4 rounded-lg">
                <FileText className="h-8 w-8 text-yellow-500 mb-2" />
                <h3 className="font-semibold">Secure Process</h3>
                <p className="text-sm text-gray-600">Protected bidding system</p>
              </div>
            </div>

            {/* Submit Button */}
                        {/* Submit Button */}
                        <div className="mt-8">
              <button
                type="submit"
                disabled={isSubmitting}
                className="w-full bg-yellow-500 text-white py-3 px-6 rounded-md shadow-md hover:bg-yellow-600 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:ring-offset-2 transition-colors duration-200 flex items-center justify-center disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {isSubmitting ? (
                  <>
                    <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Creating Tender...
                  </>
                ) : (
                  'Create Tender'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}