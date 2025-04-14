import React from "react";
import { useNavigate } from "react-router-dom";

const Dashboard: React.FC = () => {
  const navigate = useNavigate();

  // Dummy data for dashboard
  const portfolioValue = 50000;
  const financialGoals = [
    { name: "Retirement", progress: 45 },
    { name: "Buy a House", progress: 60 },
    { name: "Vacation Fund", progress: 20 },
  ];

  const handleLogout = () => {
    localStorage.removeItem("auth");
    navigate("/login");
  };

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Sidebar */}
      <div className="w-64 bg-indigo-600 text-white flex flex-col items-center py-6 space-y-8">
        <h2 className="text-3xl font-bold">WealthScape</h2>
        <nav className="space-y-4">
          <button className="w-full text-lg hover:bg-indigo-700 py-2 rounded-lg">
            Dashboard
          </button>
          <button className="w-full text-lg hover:bg-indigo-700 py-2 rounded-lg">
            Financial Goals
          </button>
          <button className="w-full text-lg hover:bg-indigo-700 py-2 rounded-lg">
            Portfolio
          </button>
          <button className="w-full text-lg hover:bg-indigo-700 py-2 rounded-lg">
            Insights
          </button>
          <button
            className="w-full text-lg hover:bg-indigo-700 py-2 rounded-lg"
            onClick={handleLogout}
          >
            Logout
          </button>
        </nav>
      </div>

      {/* Main content */}
      <div className="flex-1 p-8 space-y-6">
        <h1 className="text-3xl font-semibold text-gray-900">Dashboard</h1>

        {/* Portfolio Overview */}
        <div className="bg-white rounded-2xl shadow-lg p-6 flex flex-col space-y-4">
          <h2 className="text-2xl font-bold text-gray-700">Portfolio Value</h2>
          <p className="text-3xl font-semibold text-indigo-600">
            ${portfolioValue}
          </p>
        </div>

        {/* Financial Goals */}
        <div className="bg-white rounded-2xl shadow-lg p-6">
          <h2 className="text-2xl font-bold text-gray-700 mb-4">
            Your Financial Goals
          </h2>
          <div className="space-y-4">
            {financialGoals.map((goal, index) => (
              <div key={index} className="flex justify-between items-center">
                <div>
                  <p className="text-xl text-gray-700">{goal.name}</p>
                  <div className="w-full bg-gray-300 rounded-full h-2 mt-2">
                    <div
                      className="bg-indigo-600 h-2 rounded-full"
                      style={{ width: `${goal.progress}%` }}
                    />
                  </div>
                </div>
                <p className="text-sm text-gray-500">{goal.progress}%</p>
              </div>
            ))}
          </div>
        </div>

        {/* Financial Insights */}
        <div className="bg-white rounded-2xl shadow-lg p-6">
          <h2 className="text-2xl font-bold text-gray-700">
            Financial Insights
          </h2>
          <p className="text-gray-600">
            Keep track of your financial trends and adjust your goals as needed.
          </p>
          <button className="mt-4 w-full bg-indigo-600 text-white py-2 rounded-lg hover:opacity-90 transition">
            View Insights
          </button>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
