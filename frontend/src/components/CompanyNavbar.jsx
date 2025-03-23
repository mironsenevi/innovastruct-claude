import React, { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import PropTypes from 'prop-types';
import {
  Home,
  Building,
  FileText,
  Settings,
  LogOut,
  ChevronLeft,
  ChevronRight,
  Briefcase,
  LineChart,
} from "lucide-react";
import userService from '../services/userService';
import companyService from '../services/companyService';

// Helper component for nav items with PropTypes
const NavItem = ({ href, icon, text, isMinimized }) => {
  const location = useLocation();
  const isActive = location.pathname === href;

  return (
    <li>
      <Link
        to={href}
        className={`btn ${isMinimized ? 'btn-square' : 'btn-wide'} border-none justify-start shadow-none transition-colors ${
          isActive
            ? 'bg-white'
            : 'bg-transparent hover:bg-white/90 group'
        }`}
      >
        <div className={`flex items-center gap-3 ${
          isActive
            ? 'text-yellow-500'
            : 'text-white group-hover:text-yellow-500'
        }`}>
          {React.cloneElement(icon, {
            className: `w-5 h-5 ${isActive ? 'text-yellow-500' : ''}`
          })}
          {!isMinimized && <span>{text}</span>}
        </div>
      </Link>
    </li>
  );
};

// PropTypes for NavItem
NavItem.propTypes = {
  href: PropTypes.string.isRequired,
  icon: PropTypes.element.isRequired,
  text: PropTypes.string.isRequired,
  isMinimized: PropTypes.bool.isRequired
};

function CompanyNavbar() {
  const [isMinimized, setIsMinimized] = useState(false);
  const [companyName, setCompanyName] = useState('');

  useEffect(() => {
    // Get current user's name
    const currentUser = userService.getCurrentUser();
    if (currentUser) {
      // Fetch company data to get the company name
      const fetchCompanyData = async () => {
        try {
          const userDetails = await userService.getUserById(currentUser.id);
          if (userDetails && userDetails.companyId) {
            const companyData = await companyService.getCompanyById(userDetails.companyId);
            if (companyData) {
              setCompanyName(companyData.name);
            }
          }
        } catch (error) {
          console.error('Error fetching company data:', error);
        }
      };
      fetchCompanyData();
    }

    // Handle responsive behavior
    const handleResize = () => {
      if (window.innerWidth < 768 && !isMinimized) {
        setIsMinimized(true);
        window.dispatchEvent(new CustomEvent('sidebarStateChange', { detail: true }));
      }
    };

    // Set initial responsive state
    handleResize();

    // Add event listeners
    window.addEventListener('resize', handleResize);

    // Cleanup
    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, [isMinimized]);

  const toggleMinimize = () => {
    setIsMinimized(!isMinimized);
    // Dispatch custom event when sidebar state changes
    window.dispatchEvent(new CustomEvent('sidebarStateChange', { detail: !isMinimized }));
  };

  return (
    <div className={`fixed h-screen bg-yellow-400 text-white shadow-lg flex flex-col transition-all duration-300 z-50 ${isMinimized ? 'w-20' : 'w-80'}`}>
      <div className="px-6 py-4 border-b border-yellow-300 flex justify-between items-center">
        <div className="flex items-center gap-4">
          <img src="/assets/Logo.png" alt="Logo" className="w-9 h-10.5 -translate-y-0.5" />
          {!isMinimized && <h2 className="text-2xl font-bold">InnovaStruct</h2>}
        </div>
        <button
          onClick={toggleMinimize}
          className="p-2 hover:bg-white hover:text-yellow-400 rounded-full transition-colors"
          aria-label={isMinimized ? "Expand sidebar" : "Minimize sidebar"}
        >
          {isMinimized ? <ChevronRight size={24} /> : <ChevronLeft size={24} />}
        </button>
      </div>

      {!isMinimized && (
        <div className="mt-6 px-4">
          <div className="flex items-center gap-3">
            <div className="relative">
              <div className="w-10 h-10 bg-white rounded-full flex items-center justify-center shadow-sm">
                <Briefcase className="w-6 h-6 text-yellow-600" />
              </div>
              <div className="absolute bottom-0 right-0 w-3 h-3 bg-green-500 border-2 border-yellow-300 rounded-full"></div>
            </div>
            <div>
              <p className="text-sm font-medium text-yellow-900">{companyName}</p>
              <p className="text-xs text-yellow-800/70">Company Account</p>
            </div>
          </div>
        </div>
      )}

      <ul className="flex flex-col p-4 space-y-4 flex-grow">
        <NavItem href="/company/home" icon={<Home />} text="Home" isMinimized={isMinimized} />
        <NavItem href="/company/portfolio" icon={<Building />} text="Portfolio" isMinimized={isMinimized} />
        <NavItem href="/company/tender" icon={<FileText />} text="Tender" isMinimized={isMinimized} />
        <NavItem href="/company/insights" icon={<LineChart />} text="Insights" isMinimized={isMinimized} />
        <NavItem href="/company/settings" icon={<Settings />} text="Settings" isMinimized={isMinimized} />
      </ul>

      <div className="p-4 mt-auto border-t border-yellow-300">
        <Link
          to="/logout"
          className={`btn ${isMinimized ? 'btn-square' : 'btn-wide'} bg-white text-red-600 hover:bg-red-50 hover:text-red-700 flex items-center justify-center gap-3 transition-colors`}
        >
          <LogOut className="w-5 h-5" />
          {!isMinimized && "Log out"}
        </Link>
      </div>
    </div>
  );
}

export default CompanyNavbar;