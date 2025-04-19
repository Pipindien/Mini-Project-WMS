import React, { useEffect, useState } from "react";
import { Goal } from "../../services/goal/type";
import { getGoals, archiveGoal } from "../../services/goal/api";
import { useNavigate } from "react-router-dom";

const HomePortfolio: React.FC = () => {
  const [goals, setGoals] = useState<Goal[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const fetchGoals = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      setError("No token found.");
      setLoading(false);
      return;
    }

    try {
      const data = await getGoals(token);
      setGoals(data);
    } catch (err) {
      setError("Failed to fetch financial goals.");
      console.error("Error fetching goals:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchGoals();
  }, []);

  const handleEdit = (goalId: string) => {
    navigate(`/portfolio/edit/${goalId}`);
  };

  const handleDelete = async (goalId: string) => {
    const confirm = window.confirm("Yakin ingin menghapus goal ini?");
    if (!confirm) return;

    const token = localStorage.getItem("token");
    try {
      await archiveGoal(goalId, token!);
      fetchGoals(); // Refresh list
    } catch (err) {
      console.error("Error archiving goal:", err);
      alert("Gagal menghapus goal.");
    }
  };

  if (loading)
    return <div className="text-center mt-20 text-blue-500">Loading...</div>;
  if (error)
    return <div className="text-center mt-20 text-red-500">{error}</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Portfolio Management</h1>
        <button
          onClick={() => navigate("/portfolio/create")}
          className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded"
        >
          + Add Goal
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {goals.map((goal) => {
          const progress =
            goal.targetAmount === 0
              ? 0
              : Math.min(
                  100,
                  Math.round((goal.currentAmount / goal.targetAmount) * 100)
                );

          const formattedDate = new Date(goal.updatedAt).toLocaleDateString(
            "id-ID",
            {
              day: "2-digit",
              month: "short",
              year: "numeric",
            }
          );

          return (
            <div
              key={goal.goalId}
              onClick={() => navigate(`/portfolio/detail/${goal.goalId}`)}
              className="bg-white shadow-md rounded-lg p-4 border transform transition-transform hover:scale-[1.02] hover:shadow-lg duration-300 cursor-pointer"
            >
              <h2 className="text-lg font-semibold mb-1">{goal.goalName}</h2>
              <p className="text-sm text-gray-600 mb-1">
                Status: <span className="font-medium">{goal.status}</span>
              </p>
              <p className="text-sm text-gray-600 mb-1">
                Risk Tolerance:{" "}
                <span className="font-medium text-blue-600 capitalize">
                  {goal.riskTolerance}
                </span>
              </p>
              <p className="text-sm text-gray-500 mb-2">
                Last Updated: {formattedDate}
              </p>

              <div className="mb-2">
                <div className="flex justify-between text-sm text-gray-600 mb-1">
                  <span>Rp {goal.currentAmount.toLocaleString()}</span>
                  <span>Rp {goal.targetAmount.toLocaleString()}</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-3 overflow-hidden">
                  <div
                    className="bg-green-500 h-3 transition-all duration-700"
                    style={{ width: `${progress}%` }}
                  ></div>
                </div>
                <p className="text-right text-xs mt-1 text-gray-500">
                  {progress}% achieved
                </p>
              </div>

              <p className="text-sm text-gray-700 mb-4">
                {goal.insightMessage}
              </p>

              <hr className="my-4 border-t border-gray-200" />
              <div className="flex justify-end space-x-2">
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleEdit(goal.goalId);
                  }}
                  className="px-4 py-1.5 text-sm font-medium text-blue-600 bg-blue-100 hover:bg-blue-200 rounded-lg transition duration-200"
                >
                  Edit
                </button>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleDelete(goal.goalId);
                  }}
                  className="px-4 py-1.5 text-sm font-medium text-red-600 bg-red-100 hover:bg-red-200 rounded-lg transition duration-200"
                >
                  Delete
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default HomePortfolio;
