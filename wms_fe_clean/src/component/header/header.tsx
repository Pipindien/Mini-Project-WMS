import React from "react";
import { useNavigate } from "react-router-dom";

const Header: React.FC = () => {
  const navigate = useNavigate();

  const handleDashboardClick = () => {
    navigate("/dashboard");
  };

  return (
    <header className="bg-gradient-to-r from-indigo-600 to-purple-600 shadow-md py-4 px-6">
      <div className="container mx-auto flex flex-col sm:flex-row items-start sm:items-center justify-between">
        <div
          className="text-white text-2xl font-bold tracking-wide mb-2 sm:mb-0 text-center sm:text-left cursor-pointer transition-all duration-300
                     hover:scale-110 hover:text-white  hover:shadow-lg "
          onClick={handleDashboardClick}
        >
          WealthScape
        </div>
        <nav className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-6 text-white text-sm font-medium items-center">
          <div
            className="hover:underline hover:text-gray-200 transition text-center sm:text-left cursor-pointer"
            onClick={handleDashboardClick}
          >
            Dashboard
          </div>
          <a
            href="#"
            className="hover:underline hover:text-gray-200 transition text-center sm:text-left"
          >
            Portfolio
          </a>
          <a
            href="#"
            className="hover:underline hover:text-gray-200 transition text-center sm:text-left"
          >
            Transaksi
          </a>
          <a
            href="#"
            className="hover:underline hover:text-gray-200 transition text-center sm:text-left"
          >
            Profile
          </a>
        </nav>
      </div>
    </header>
  );
};

export default Header;
