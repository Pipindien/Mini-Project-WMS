import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import useGoalById from "../hooks/useGoal/useGoalById";
import { updateGoal } from "../../services/goal/api";
import { Goal } from "../../services/goal/type";

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
    }
  };

  if (loading) return <p className="text-center text-gray-500">Loading...</p>;
  if (error || !goal)
    return (
      <p className="error text-red-500 text-center">
        {error || "Goal not found."}
      </p>
    );

  return (
    <div className="container mx-auto p-6 max-w-3xl bg-white shadow-lg rounded-lg">
      <h1 className="text-3xl font-bold mb-6 text-center text-blue-600">
        Edit Financial Goal
      </h1>

      {formError && (
        <p className="text-red-500 text-center mb-4">{formError}</p>
      )}

      {goal && (
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label
              htmlFor="goalName"
              className="block text-sm font-medium text-gray-700"
            >
              Goal Name
            </label>
            <input
              type="text"
              id="goalName"
              value={goalName}
              onChange={(e) => setGoalName(e.target.value)}
              className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <div>
            <label
              htmlFor="targetAmount"
              className="block text-sm font-medium text-gray-700"
            >
              Target Amount
            </label>
            <input
              type="number"
              id="targetAmount"
              value={targetAmount}
              onChange={(e) => setTargetAmount(Number(e.target.value))}
              className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <div>
            <label
              htmlFor="targetDate"
              className="block text-sm font-medium text-gray-700"
            >
              Target Date
            </label>
            <input
              type="date"
              id="targetDate"
              value={targetDate}
              onChange={(e) => setTargetDate(e.target.value)}
              className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <div>
            <label
              htmlFor="riskTolerance"
              className="block text-sm font-medium text-gray-700"
            >
              Risk Tolerance
            </label>
            <select
              id="riskTolerance"
              value={riskTolerance}
              onChange={(e) =>
                setRiskTolerance(
                  e.target.value as "Conservative" | "Moderate" | "Aggressive"
                )
              }
              className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-green-500 focus:border-green-500"
            >
              <option value="Conservative">Conservative</option>
              <option value="Moderate">Moderate</option>
              <option value="Aggressive">Aggressive</option>
            </select>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-2 px-4 bg-green-600 text-white font-semibold rounded-md shadow-sm hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2"
          >
            {loading ? "Updating..." : "Update Goal"}
          </button>
        </form>
      )}
    </div>
  );
};

export default EditGoalPage;
