import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createGoal } from "../../services/goal/api";
import "animate.css"; // Import animate.css for animations

const CreateGoalPage: React.FC = () => {
  const navigate = useNavigate();
  const [goalName, setGoalName] = useState("");
  const [targetAmount, setTargetAmount] = useState<number>(0);
  const [targetDate, setTargetDate] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    const token = localStorage.getItem("token");
    if (!token) {
      setError("Token can't be found, please Login");
      setLoading(false);
      return;
    }

    try {
      await createGoal({ goalName, targetAmount, targetDate }, token);
      navigate("/portfolio");
    } catch {
      setError("Failed to create a goal, please try again");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-pink-100 via-purple-200 to-indigo-200 flex items-center justify-center px-4 py-10 animate__animated animate__fadeIn">
      <div className="w-full max-w-md bg-white/80 backdrop-blur-md p-8 rounded-xl shadow-lg border border-white/60 transition-all duration-300 animate__animated animate__slideInDown">
        <h1 className="text-3xl font-extrabold text-gray-800 text-center mb-6 tracking-tight drop-shadow-sm animate__animated animate__fadeIn">
          ✨ Create Goal
        </h1>

        {error && (
          <div className="bg-red-100 text-red-700 px-4 py-3 rounded-lg mb-4 text-sm shadow-sm animate__animated animate__shake">
            {error}
          </div>
        )}

        <form
          onSubmit={handleSubmit}
          className="space-y-4 animate__animated animate__fadeIn animate__delay-1s"
        >
          <div>
            <label className="block text-gray-700 text-sm font-semibold mb-2 animate__animated animate__fadeIn">
              🎯 Goal Name
            </label>
            <input
              type="text"
              value={goalName}
              onChange={(e) => setGoalName(e.target.value)}
              required
              placeholder="Dream Vacation, New Laptop, Down Payment..."
              className="w-full px-4 py-2 rounded-md border border-gray-300 focus:ring-2 focus:ring-purple-400 focus:outline-none bg-white/90 placeholder-gray-400 animate__animated animate__slideInLeft"
            />
          </div>

          <div>
            <label className="block text-gray-700 text-sm font-semibold mb-2 animate__animated animate__fadeIn">
              💰 Target Amount (Rp)
            </label>
            <input
              type="number"
              value={targetAmount}
              onChange={(e) => setTargetAmount(Number(e.target.value))}
              required
              placeholder="e.g., 1000000"
              className="w-full px-4 py-2 rounded-md border border-gray-300 focus:ring-2 focus:ring-purple-400 focus:outline-none bg-white/90 placeholder-gray-400 animate__animated animate__slideInRight"
            />
          </div>

          <div>
            <label className="block text-gray-700 text-sm font-semibold mb-2 animate__animated animate__fadeIn">
              📅 Target Date
            </label>
            <input
              type="date"
              value={targetDate}
              onChange={(e) => setTargetDate(e.target.value)}
              required
              className="w-full px-4 py-2 rounded-md border border-gray-300 focus:ring-2 focus:ring-purple-400 focus:outline-none bg-white/90 animate__animated animate__slideInLeft"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white px-6 py-3 rounded-md font-semibold transition-all duration-200 ease-in-out shadow-md hover:shadow-lg disabled:opacity-60 animate__animated animate__fadeIn animate__delay-2s"
          >
            {loading ? (
              <svg
                className="animate-spin -ml-1 mr-3 h-5 w-5 text-white inline-block"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  className="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="4"
                ></circle>
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                ></path>
              </svg>
            ) : (
              "🚀 Create Goal"
            )}
          </button>
        </form>
      </div>
    </div>
  );
};

export default CreateGoalPage;
