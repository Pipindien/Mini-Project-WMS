import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

const Register: React.FC = () => {
  const navigate = useNavigate();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const handleRegister = (e: React.FormEvent) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      alert("Passwords do not match");
      return;
    }

    // Mock registration logic
    localStorage.setItem("auth", "true");
    navigate("/app/dashboard");
  };

  return (
    <div className="min-h-screen flex flex-col md:flex-row bg-white">
      {/* Left Panel - Form */}
      <div className="w-full md:w-1/2 flex items-center justify-center px-6 py-12 bg-gradient-to-br from-indigo-600 to-purple-700">
        <div className="w-full max-w-md bg-white rounded-3xl shadow-2xl p-10">
          <h2 className="text-4xl font-bold text-center text-indigo-700 mb-2">
            WealthScape
          </h2>
          <p className="text-center text-sm text-gray-500 mb-8">
            Create your account to begin managing your wealth journey.
          </p>

          <form className="space-y-5" onSubmit={handleRegister}>
            <div>
              <label
                htmlFor="name"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Full Name
              </label>
              <input
                type="text"
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
                placeholder="John Doe"
                required
              />
            </div>

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

            <div>
              <label
                htmlFor="confirmPassword"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Confirm Password
              </label>
              <input
                type="password"
                id="confirmPassword"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
                placeholder="********"
                required
              />
            </div>

            <button
              type="submit"
              className="w-full bg-gradient-to-r from-indigo-600 to-purple-600 text-white py-2 rounded-lg hover:opacity-90 transition"
            >
              Create Account
            </button>
          </form>

          <div className="text-center mt-6 text-sm text-gray-600">
            Already have an account?{" "}
            <a href="/login" className="text-indigo-600 hover:underline">
              Login here
            </a>
          </div>
        </div>
      </div>

      {/* Right Panel - Splash image & intro */}
      <div className="w-full md:w-1/2 relative hidden md:block">
        <img
          src="https://cdn.pixabay.com/photo/2023/07/08/07/25/ai-generated-8113892_1280.jpg"
          alt="Finance Register Illustration"
          className="w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-gradient-to-br from-indigo-800 to-purple-700 opacity-50" />
        <div className="absolute inset-0 flex flex-col justify-center px-12 text-white z-10">
          <h2 className="text-4xl font-bold mb-4 leading-snug">
            Start Your Wealth Journey
            <br />
            Build a better financial future.
          </h2>
          <p className="text-lg text-gray-200">
            Join WealthScape to track your investments, goals, and financial
            progress all in one place.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register;
