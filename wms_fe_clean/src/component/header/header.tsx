import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { LogOut } from "lucide-react";

const Header: React.FC = () => {
  const user = localStorage.getItem("user");
  const role = user ? JSON.parse(user).role : null;
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    navigate("/", { replace: true });
  };

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

        <nav className="flex items-center space-x-6 text-white text-sm font-medium">
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
                Transaction
              </Link>
            </>
          )}

          {/* Logout Button */}
          {role && (
            <button
              onClick={handleLogout}
              className="flex items-center gap-2 px-4 py-2 rounded-xl bg-white/10 hover:bg-white/20 text-white text-sm transition-all duration-200"
            >
              <LogOut size={16} />
              Logout
            </button>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;
