import React, { useState, FormEvent } from "react";
import { login as loginRequest } from "../../services/auth/api";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../../contexts/authContext";
import { UserData } from "../../services/auth/type";

const Login: React.FC = () => {
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string>("");

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e: FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setErrorMessage("");

    try {
      const response = await loginRequest({ username, password });
      const { token, ...userData } = response;

      login({
        token,
        user: userData as UserData,
      });

      const role = (userData as UserData).role;
      navigate(role === "ADMIN" ? "/dashboardAdmin" : "/dashboard");
    } catch (err: any) {
      const message =
        err?.response?.data?.message ||
        "Login gagal. Periksa username/password Anda.";
      setErrorMessage(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-black text-white flex flex-col md:flex-row transition-all duration-500">
      {/* Login Form Container */}
      <div className="w-full md:w-1/4 flex items-center justify-center px-6 py-12">
        <div className="w-full max-w-md bg-white/20 backdrop-blur-md rounded-md shadow-2xl p-8 animate-fade-in-up flex flex-col h-full">
          <div className="flex-grow flex flex-col justify-center">
            <h2 className="text-4xl font-bold text-center text-white drop-shadow-md mb-2 transition-transform duration-300 hover:scale-105 hover:brightness-110">
              WealthScape
            </h2>
            <p className="text-center text-sm text-gray-300 mb-6 transition-transform duration-300 hover:scale-105 hover:brightness-110">
              Welcome back! Log in to continue managing your wealth.
            </p>

            <form className="space-y-4" onSubmit={handleLogin}>
              <div>
                <label
                  htmlFor="username"
                  className="block text-sm font-medium text-gray-300 mb-1"
                >
                  Username
                </label>
                <input
                  type="text"
                  id="username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-500 rounded-md bg-black/30 text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 transition-all duration-300 hover:scale-105"
                  placeholder="Enter your username"
                  required
                />
              </div>

              <div>
                <label
                  htmlFor="password"
                  className="block text-sm font-medium text-gray-300 mb-1"
                >
                  Password
                </label>
                <input
                  type="password"
                  id="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-500 rounded-md bg-black/30 text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 transition-all duration-300 hover:scale-105"
                  placeholder="Enter your password"
                  required
                />
              </div>

              {errorMessage && (
                <p className="text-red-400 text-sm text-center">
                  {errorMessage}
                </p>
              )}

              <button
                type="submit"
                disabled={loading}
                className="w-full bg-gradient-to-r from-purple-600 to-purple-600 hover:from-white hover:via-purple-600 hover:to-white text-white font-semibold py-2 rounded-md shadow-md hover:shadow-lg transition-all duration-300 hover:scale-105"
              >
                {loading ? "Logging in..." : "Login"}
              </button>
            </form>
          </div>
          <div className="text-center mt-6 text-sm text-gray-400">
            Don’t have an account?{" "}
            <Link
              to="/register"
              className="text-purple-400 hover:text-purple-200 underline transition-transform duration-300 hover:scale-105 hover:brightness-110"
            >
              Register here
            </Link>
          </div>
        </div>
      </div>

      {/* Image Container */}
      <div className="w-full md:w-3/4 relative hidden md:block overflow-hidden">
        <img
          src="https://cdn.pixabay.com/photo/2020/04/11/08/06/money-5029288_1280.jpg"
          alt="Finance Illustration"
          className="w-full h-full object-cover scale-105 transform transition-transform duration-500"
        />
        <div className="absolute inset-0 bg-gradient-to-br from-indigo-800 to-purple-800 opacity-60" />
        <div className="absolute inset-0 flex flex-col justify-center px-12 text-white z-10 animate-fade-in">
          <h2 className="text-4xl font-extrabold mb-4 leading-snug drop-shadow-lg transition-transform duration-300 hover:scale-105 hover:brightness-110">
            Secure Your Assets with WealthScape
            <br />
            Make Smarter Decisions now!
          </h2>
          <p className="text-lg text-gray-200 transition-transform duration-300 hover:scale-105 hover:brightness-110">
            Sign in to access your personal wealth dashboard and insights.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
