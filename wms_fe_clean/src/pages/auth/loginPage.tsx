import React, { useState, FormEvent, useEffect } from "react";
import { login as loginRequest } from "../../services/auth/api";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../../contexts/authContext";
import { UserData } from "../../services/auth/type";
import { FaUser, FaLock, FaSignInAlt } from "react-icons/fa";
import { motion } from "framer-motion";

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
        "Login failed! Please check and enter the correct username/password";
      setErrorMessage(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <motion.div
      className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-black text-white flex flex-col md:flex-row transition-all duration-500"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.5, ease: "easeInOut" }}
    >
      {/* Login Form Container */}
      <div className="w-full md:w-1/4 flex items-center justify-center px-6 py-12">
        <motion.div
          className="w-full max-w-md bg-white/20 backdrop-blur-md rounded-md shadow-2xl p-8 flex flex-col h-full"
          initial={{ y: 50, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ duration: 0.5, delay: 0.2 }}
        >
          {/* animation of form container fade-in-up (removed animate-fade-in-up class) */}

          <div className="flex-grow flex flex-col justify-center">
            <motion.h2
              className="text-4xl font-bold text-center text-white drop-shadow-md mb-2 transition-transform duration-300 hover:scale-105 hover:brightness-110"
              initial={{ y: -20, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              transition={{ duration: 0.5, delay: 0.3 }}
            >
              {/* animation of title fade-in-down (removed animate-fade-in-down class) */}
              WealthScape
            </motion.h2>
            <motion.p
              className="text-center text-sm text-gray-300 mb-6 transition-transform duration-300 hover:scale-105 hover:brightness-110"
              initial={{ y: 20, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              transition={{ duration: 0.5, delay: 0.4 }}
            >
              {/* animation of welcome paragraph fade-in-up (removed animate-fade-in-up class) */}
              Welcome back! Log in to continue managing your wealth.
            </motion.p>

            <form className="space-y-4" onSubmit={handleLogin}>
              {/* animation of form fade-in-up (removed animate-fade-in-up class) */}

              <div>
                <label
                  htmlFor="username"
                  className="block text-sm font-medium text-gray-300 mb-1"
                >
                  Username
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                    <FaUser />
                  </div>
                  <input
                    type="text"
                    id="username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    className="w-full pl-10 px-4 py-2 border border-gray-500 rounded-md bg-black/30 text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 transition-all duration-300"
                    placeholder="Enter your username"
                    required
                  />
                </div>
              </div>

              <div>
                <label
                  htmlFor="password"
                  className="block text-sm font-medium text-gray-300 mb-1"
                >
                  Password
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                    <FaLock />
                  </div>
                  <input
                    type="password"
                    id="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full pl-10 px-4 py-2 border border-gray-500 rounded-md bg-black/30 text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 transition-all duration-300"
                    placeholder="Enter your password"
                    required
                  />
                </div>
              </div>

              {errorMessage && (
                <motion.p
                  className="text-red-400 text-sm text-center animate-shake animate-duration-[600ms]"
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ duration: 0.3 }}
                >
                  {/* animation of error message shake */}
                  {errorMessage}
                </motion.p>
              )}

              <motion.button
                type="submit"
                disabled={loading}
                className="w-full bg-gradient-to-r from-purple-600 to-purple-600 hover:from-white hover:via-purple-600 hover:to-white text-white font-semibold py-2 rounded-md shadow-md hover:shadow-lg transition-all duration-300 hover:scale-105 flex items-center justify-center"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ duration: 0.5, delay: 0.5 }}
              >
                {/* animation of submit button fade-in-up (removed animate-fade-in-up class) */}
                {loading ? (
                  "Logging in..."
                ) : (
                  <>
                    <FaSignInAlt className="mr-2" />
                    Login
                  </>
                )}
              </motion.button>
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
        </motion.div>
      </div>

      {/* Image Container */}
      <div className="w-full md:w-3/4 relative hidden md:block overflow-hidden">
        <motion.img
          src="https://cdn.pixabay.com/photo/2020/04/11/08/06/money-5029288_1280.jpg"
          alt="Finance Illustration"
          className="w-full h-full object-cover scale-105 transform transition-transform duration-500"
          initial={{ scale: 1.05, opacity: 0 }}
          animate={{ scale: 1.05, opacity: 1 }}
          transition={{ duration: 0.7, delay: 0.3 }}
        />
        <div className="absolute inset-0 bg-gradient-to-br from-indigo-800 to-purple-800 opacity-60" />
        <motion.div
          className="absolute inset-0 flex flex-col justify-center px-12 text-white z-10"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5, delay: 0.7 }}
        >
          {/* animation of right side content fade-in (removed animate-fade-in class) */}
          <h2 className="text-4xl font-extrabold mb-4 leading-snug drop-shadow-lg transition-transform duration-300 hover:scale-105 hover:brightness-110">
            Secure Your Assets with WealthScape
            <br />
            Make Smarter Decisions now!
          </h2>
          <p className="text-lg text-gray-200 transition-transform duration-300 hover:scale-105 hover:brightness-110">
            Sign in to access your personal wealth dashboard and insights.
          </p>
        </motion.div>
      </div>
    </motion.div>
  );
};

export default Login;
