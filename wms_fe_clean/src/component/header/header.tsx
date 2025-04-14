import React from "react";

const Header: React.FC = () => {
  return (
    <header className="bg-gradient-to-r from-indigo-600 to-purple-600 shadow-md py-4 px-6">
      <div className="container mx-auto flex justify-between items-center">
        <div className="text-white text-2xl font-bold tracking-wide">
          WealthScape
        </div>
        <nav className="space-x-6 text-white text-sm font-medium">
          <a
            href="#"
            className="hover:underline hover:text-gray-200 transition"
          >
            Dashboard
          </a>
          <a
            href="#"
            className="hover:underline hover:text-gray-200 transition"
          >
            Portfolio
          </a>
          <a
            href="#"
            className="hover:underline hover:text-gray-200 transition"
          >
            Transaksi
          </a>
          <a
            href="#"
            className="hover:underline hover:text-gray-200 transition"
          >
            Profile
          </a>
        </nav>
      </div>
    </header>
  );
};

export default Header;
