import React from "react";
import { Link } from "react-router-dom";

const Header: React.FC = () => {
  const user = localStorage.getItem("user");
  const role = user ? JSON.parse(user).role : null;

  return (
    <header className="bg-gradient-to-r from-indigo-600 to-purple-600 shadow-md py-4 px-6">
      <div className="container mx-auto flex justify-between items-center">
        {/* Logo as Link to /information */}
        <Link
          to="/information"
          className="text-white text-2xl font-bold tracking-wide"
        >
          WealthScape
        </Link>

        <nav className="space-x-6 text-white text-sm font-medium">
          {role === "USER" && (
            <>
              <Link
                to="/dashboard"
                className="hover:underline hover:text-gray-200 transition"
              >
                Dashboard
              </Link>
              <Link
                to="/portfolio"
                className="hover:underline hover:text-gray-200 transition"
              >
                Portfolio
              </Link>
              <Link
                to="/history"
                className="hover:underline hover:text-gray-200 transition"
              >
                Transaksi
              </Link>
              <a
                href="#"
                className="hover:underline hover:text-gray-200 transition"
              >
                Profile
              </a>
            </>
          )}

          {role === "ADMIN" && <></>}
        </nav>
      </div>
    </header>
  );
};

export default Header;
