import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import useGoalById from "../hooks/useGoal/useGoalById";
import { updateGoal } from "../../services/goal/api";
import { Goal } from "../../services/goal/type";
import "animate.css"; // Import animate.css for animations

const EditGoalPage: React.FC = () => {
  const { goalId } = useParams<{ goalId: string }>(); // Get goalId from the URL params
  const navigate = useNavigate();

  const { goal, loading, error } = useGoalById(goalId); // Use custom hook to fetch goal data

  // State for form fields
  const [goalName, setGoalName] = useState<string>("");
  const [targetAmount, setTargetAmount] = useState<number>(0);
  const [targetDate, setTargetDate] = useState<string>("");
  const [riskTolerance, setRiskTolerance] = useState<
    "Conservative" | "Moderate" | "Aggressive"
  >("Moderate");
  const [formError, setFormError] = useState<string>("");
  const [isSaving, setIsSaving] = useState(false);

  const token = localStorage.getItem("token") || ""; // Example: Get token from local storage

  // Populate the form when the goal data is fetched
  useEffect(() => {
    if (goal) {
      setGoalName(goal.goalName);
      setTargetAmount(goal.targetAmount);
      const formattedDate = goal.targetDate.split("T")[0]; // Extracts the date part
      setTargetDate(formattedDate);
      setRiskTolerance(goal.riskTolerance);
    }
  }, [goal]);

  // Handle form submission to update the goal
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError("");
    setIsSaving(true);

    const updatedGoal: Partial<Goal> = {
      goalName,
      targetAmount,
      targetDate,
      riskTolerance,
    };

    try {
      await updateGoal(goalId, token, updatedGoal); // Call the API to update the goal
      navigate(`/portfolio`); // Redirect to the goal's detail page after successful update
    } catch (err) {
      setFormError("Failed to update the goal.");
    } finally {
      setIsSaving(false);
    }
  };

  if (loading)
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-100 via-lime-100 to-emerald-200 flex items-center justify-center px-4 py-10 animate__animated animate__fadeIn">
        <p className="text-center text-gray-600 animate__animated animate__pulse animate__infinite">
          Loading goal data...
        </p>
      </div>
    );
  if (error || !goal)
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-100 via-lime-100 to-emerald-200 flex items-center justify-center px-4 py-10 animate__animated animate__fadeIn">
        <p className="error text-red-500 text-center animate__animated animate__shake">
          {error || "Goal not found."}
        </p>
      </div>
    );

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-100 via-lime-100 to-emerald-200 flex items-center justify-center px-4 py-10 animate__animated animate__fadeIn">
      <div className="w-full max-w-md bg-white/80 backdrop-blur-md p-8 rounded-xl shadow-lg border border-white/60 transition-all duration-300 animate__animated animate__slideInDown">
        <h1 className="text-3xl font-extrabold text-gray-800 text-center mb-6 tracking-tight drop-shadow-sm animate__animated animate__fadeIn">
          ✏️ Edit Goal
        </h1>

        {formError && (
          <div className="bg-red-100 text-red-700 px-4 py-3 rounded-lg mb-4 text-sm shadow-sm animate__animated animate__shake">
            {formError}
          </div>
        )}

        <form
          onSubmit={handleSubmit}
          className="space-y-4 animate__animated animate__fadeIn animate__delay-1s"
        >
          <div>
            <label className="block text-gray-700 text-sm font-semibold mb-2 animate__animated animate__fadeIn">
              📝 Goal Name
            </label>
            <input
              type="text"
              id="goalName"
              value={goalName}
              onChange={(e) => setGoalName(e.target.value)}
              className="w-full px-4 py-2 rounded-md border border-gray-300 focus:ring-2 focus:ring-green-400 focus:outline-none bg-white/90 placeholder-gray-400 animate__animated animate__slideInLeft"
            />
          </div>

          <div>
            <label className="block text-gray-700 text-sm font-semibold mb-2 animate__animated animate__fadeIn">
              💰 Target Value (Rp)
            </label>
            <input
              type="number"
              id="targetAmount"
              value={targetAmount}
              onChange={(e) => setTargetAmount(Number(e.target.value))}
              className="w-full px-4 py-2 rounded-md border border-gray-300 focus:ring-2 focus:ring-green-400 focus:outline-none bg-white/90 placeholder-gray-400 animate__animated animate__slideInRight"
            />
          </div>

          <div>
            <label className="block text-gray-700 text-sm font-semibold mb-2 animate__animated animate__fadeIn">
              📅 Target Date
            </label>
            <input
              type="date"
              id="targetDate"
              value={targetDate}
              onChange={(e) => setTargetDate(e.target.value)}
              className="w-full px-4 py-2 rounded-md border border-gray-300 focus:ring-2 focus:ring-green-400 focus:outline-none bg-white/90 animate__animated animate__slideInLeft"
            />
          </div>

          <div>
            <label
              htmlFor="riskTolerance"
              className="block text-gray-700 text-sm font-semibold mb-2 animate__animated animate__fadeIn"
            >
              📊 Risk Tolerance
            </label>
            <select
              id="riskTolerance"
              value={riskTolerance}
              onChange={(e) =>
                setRiskTolerance(
                  e.target.value as "Conservative" | "Moderate" | "Aggressive"
                )
              }
              className="w-full px-4 py-2 rounded-md border border-gray-300 focus:ring-2 focus:ring-emerald-400 focus:outline-none bg-white/90 animate__animated animate__slideInRight"
            >
              <option value="Conservative">Conservative</option>
              <option value="Moderate">Moderate</option>
              <option value="Aggressive">Aggressive</option>
            </select>
          </div>

          <div className="flex justify-end animate__animated animate__fadeIn animate__delay-2s">
            <button
              type="submit"
              disabled={isSaving}
              className="w-full sm:w-auto bg-gradient-to-r from-green-400 to-emerald-500 hover:from-green-500 hover:to-emerald-600 text-white px-6 py-3 rounded-md font-semibold transition-all duration-200 ease-in-out shadow-md hover:shadow-lg disabled:opacity-60"
            >
              {isSaving ? (
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
                "💾 Update Goal"
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditGoalPage;
