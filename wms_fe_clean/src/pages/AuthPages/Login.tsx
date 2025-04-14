import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const Login: React.FC = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    if (email === "admin@example.com" && password === "password") {
      localStorage.setItem("auth", "true");
      navigate("/app/dashboard");
    } else {
      alert("Invalid credentials");
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
            Manage your wealth with clarity and confidence.
          </p>

          <form className="space-y-5" onSubmit={handleLogin}>
            <div>
              <label
                htmlFor="email"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Email
              </label>
              <input
                type="email"
                id="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
                placeholder="you@example.com"
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

            <div className="flex justify-between items-center text-sm">
              <label className="flex items-center space-x-2">
                <input type="checkbox" className="rounded" />
                <span>Remember me</span>
              </label>
              <a href="#" className="text-indigo-600 hover:underline">
                Forgot Password?
              </a>
            </div>

            <button
              type="submit"
              className="w-full bg-gradient-to-r from-indigo-600 to-purple-600 text-white py-2 rounded-lg hover:opacity-90 transition"
            >
              Login
            </button>
          </form>

          <div className="text-center mt-6 text-sm text-gray-600">
            New to WealthScape?{" "}
            <a href="#" className="text-indigo-600 hover:underline">
              Create an account
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
            Shape Your Future
            <br />
            Invest with Purpose
          </h2>
          <p className="text-lg text-gray-200">
            WealthScape empowers you to track, plan, and grow your financial
            goals all in one place.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
