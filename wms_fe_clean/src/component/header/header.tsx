import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { LogOut } from "lucide-react";

const Header: React.FC = () => {
  const [role, setRole] = useState<string | null>(() => {
    const user = localStorage.getItem("user");
    return user ? JSON.parse(user).role : null;
  });

  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    setRole(null);
    navigate("/", { replace: true });
  };

  useEffect(() => {
    const handleStorageChange = () => {
      const updatedUser = localStorage.getItem("user");
      setRole(updatedUser ? JSON.parse(updatedUser).role : null);
    };

    window.addEventListener("storage", handleStorageChange);
    return () => {
      window.removeEventListener("storage", handleStorageChange);
    };
  }, []);

  return (
    <header className="bg-gradient-to-r from-indigo-600 to-purple-600 shadow-md py-4 px-6">
      <div className="container mx-auto flex justify-between items-center">
        <Link
          to="/information"
          className="text-white text-2xl font-bold tracking-wide transition-colors duration-300 hover:text-yellow-400"
        >
          WealthScape
        </Link>

        <nav className="flex items-center space-x-4 text-white text-sm font-medium">
          {role === "USER" && (
            <>
              <Link
                to="/dashboard"
                className="relative px-3 py-1 rounded-md transition-all duration-200 hover:bg-white/20 hover:scale-105 hover:shadow-md"
              >
                Dashboard
              </Link>
              <Link
                to="/portfolio"
                className="relative px-3 py-1 rounded-md transition-all duration-200 hover:bg-white/20 hover:scale-105 hover:shadow-md"
              >
                Portfolio
              </Link>
              <Link
                to="/history"
                className="relative px-3 py-1 rounded-md transition-all duration-200 hover:bg-white/20 hover:scale-105 hover:shadow-md"
              >
                Transaction
              </Link>
            </>
          )}

          {role && (
            <button
              onClick={handleLogout}
              className="flex items-center gap-2 px-4 py-2 rounded-xl bg-white/10 hover:bg-red-500 text-white text-sm transition-all duration-200"
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
