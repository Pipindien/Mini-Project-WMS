import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createGoal } from "../../services/goal/api";

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
    <div className="min-h-screen bg-gradient-to-br from-sky-100 via-blue-200 to-indigo-300 flex items-center justify-center px-4 py-10">
      <div className="w-full max-w-2xl bg-white/70 backdrop-blur-md p-10 rounded-3xl shadow-2xl border border-white/40 transition-all duration-500">
        <h1 className="text-4xl font-extrabold text-gray-800 text-center mb-6 tracking-tight drop-shadow-sm">
          🎯 Create Financial Goal
        </h1>

        {error && (
          <div className="bg-red-100 text-red-700 px-4 py-3 rounded-lg mb-4 text-sm shadow-sm">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-gray-700 text-sm font-semibold mb-2">
              📝 Target Goal
            </label>
            <input
              type="text"
              value={goalName}
              onChange={(e) => setGoalName(e.target.value)}
              required
              placeholder="Buying car, University funds, Travelling to Bali..."
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:outline-none bg-white/80 placeholder-gray-400"
            />
          </div>

          <div>
            <label className="block text-gray-700 text-sm font-semibold mb-2">
              💰 Target Value (Rp)
            </label>
            <input
              type="number"
              value={targetAmount}
              onChange={(e) => setTargetAmount(Number(e.target.value))}
              required
              placeholder="Contoh: 5000000"
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:outline-none bg-white/80 placeholder-gray-400"
            />
          </div>

          <div>
            <label className="block text-gray-700 text-sm font-semibold mb-2">
              📅 Target Date
            </label>
            <input
              type="date"
              value={targetDate}
              onChange={(e) => setTargetDate(e.target.value)}
              required
              className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:outline-none bg-white/80"
            />
          </div>

          <div className="flex justify-end">
            <button
              type="submit"
              disabled={loading}
              className="w-full sm:w-auto bg-gradient-to-r from-blue-500 to-indigo-600 hover:from-blue-600 hover:to-indigo-700 text-white px-6 py-3 rounded-xl font-semibold transition-all duration-300 ease-in-out shadow-md hover:shadow-xl disabled:opacity-60"
            >
              {loading ? "Saving..." : "🚀 Create Goal"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateGoalPage;
