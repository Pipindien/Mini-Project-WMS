import React, { useState } from "react";
import { login as loginRequest } from "../../services/auth/api";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/authContext";
import { UserData } from "../../services/auth/type";

const Login: React.FC = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const response = await loginRequest({ username, password });

      // Pisahkan user data dan token
      const { token, ...userData } = response;

      // Panggil login dari context
      login({
        token,
        user: userData as UserData,
      });

      navigate("/dashboard");
    } catch (err: any) {
      setError(err.response?.data?.message || "Login gagal");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col md:flex-row bg-white">
      {/* Left Panel - Form */}
      <div className="w-full md:w-1/2 flex items-center justify-center px-6 py-12 bg-gradient-to-br from-indigo-900 to-purple-700">
        <div className="w-full max-w-md bg-white rounded-3xl shadow-2xl p-10">
          <h2 className="text-4xl font-bold text-center text-indigo-700 mb-2">
            WealthScape
          </h2>
          <p className="text-center text-sm text-gray-500 mb-8">
            Welcome back! Log in to continue managing your wealth.
          </p>

          <form className="space-y-5" onSubmit={handleLogin}>
            <div>
              <label
                htmlFor="username"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Username
              </label>
              <input
                type="text"
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
                placeholder="yourusername"
                required
              />
            </div>

            <div>
              <label
                htmlFor="password"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Password
              </label>
              <input
                type="password"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
                placeholder="********"
                required
              />
            </div>

            {error && (
              <p className="text-red-500 text-sm text-center">{error}</p>
            )}

            <button
              type="submit"
              className="w-full bg-white text-indigo-600 py-2 rounded-lg border border-indigo-600 hover:bg-gradient-to-r from-indigo-600 to-purple-600 hover:text-white transition"
              disabled={loading}
            >
              {loading ? "Logging in..." : "Login"}
            </button>
          </form>

          <div className="text-center mt-6 text-sm text-gray-600">
            Don't have an account yet?{" "}
            <a href="/register" className="text-indigo-600 hover:underline">
              Register here
            </a>
          </div>
        </div>
      </div>

      {/* Right Panel - Splash image & intro */}
      <div className="w-full md:w-1/2 relative hidden md:block">
        <img
          src="https://cdn.pixabay.com/photo/2020/04/11/08/06/money-5029288_1280.jpg"
          alt="Finance Illustration"
          className="w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-gradient-to-br from-indigo-800 to-purple-700 opacity-50" />
        <div className="absolute inset-0 flex flex-col justify-center px-12 text-white z-10">
          <h2 className="text-4xl font-bold mb-4 leading-snug">
            Secure Your Assets
            <br />
            Make Smarter Decisions
          </h2>
          <p className="text-lg text-gray-200">
            Sign in to access your personal wealth dashboard and insights.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
