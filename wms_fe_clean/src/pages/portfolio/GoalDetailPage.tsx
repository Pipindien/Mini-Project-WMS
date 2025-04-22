import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getPortfolioByGoalId } from "../../services/goal/api";
import useGoalById from "../hooks/useGoal/useGoalById";
import {
  PortfolioDashboardResponse,
  PortfolioProductDetail,
} from "../../services/goal/type";
import { createSimulateGoal } from "../../services/simulate/api";
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import { AiOutlineInfoCircle, AiOutlineWarning } from "react-icons/ai";
import "animate.css"; // Import the animate.css library

const COLORS = ["#6366f1", "#38b2ac", "#f6ad55", "#e53e3e", "#9f7aea"]; // More modern Tailwind colors

// Component to display when there are no investments yet
const GoalDetailPageEmptyState: React.FC = () => {
  const navigate = useNavigate();
  return (
    <div className="flex flex-col items-center justify-center min-h-[40vh] py-8 animate__animated animate__fadeIn">
      <div className="bg-red-50 rounded-lg p-6 shadow-md border border-red-200">
        <div className="flex items-center space-x-3 mb-4">
          <AiOutlineWarning className="text-red-500 h-8 w-8" />
          <h2 className="text-xl font-semibold text-red-700">
            Oops! No Investments Yet
          </h2>
        </div>
        <p className="text-gray-600 text-center">
          It seems you haven't started investing for this goal. Once you make
          your first investment, your portfolio details will show up here. Start
          investing now by returning to the dashboard below!
        </p>
        <button
          className="mt-6 bg-indigo-500 hover:bg-indigo-600 text-white font-semibold py-3 px-6 rounded-md shadow-md focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-1 transition-colors duration-200"
          onClick={() => {
            navigate("/dashboard"); // Example: Navigate to the main dashboard or investment options
          }}
        >
          Return Dashboard
        </button>
      </div>
    </div>
  );
};

const GoalDetailPage: React.FC = () => {
  const { goalId } = useParams();
  const { goal } = useGoalById(goalId);
  const navigate = useNavigate();
  const [portfolio, setPortfolio] = useState<PortfolioDashboardResponse | null>(
    null
  );
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);

  const [monthlyInvestment, setMonthlyInvestment] = useState(500000);
  const [simulation, setSimulation] = useState<any | null>(null);
  const [simLoading, setSimLoading] = useState(false);
  const [simError, setSimError] = useState<string | null>(null);

  useEffect(() => {
    const fetchPortfolio = async () => {
      const token = localStorage.getItem("token");
      try {
        const data = await getPortfolioByGoalId(goalId!, token!);
        setPortfolio(data);
      } catch (err) {
        console.error("Error:", err);
        setError(
          "It seems you haven't started investing for this goal. Once you make your first investment, your portfolio details will show up here. Start investing now by returning to the dashboard below!"
        );
      } finally {
        setLoading(false);
      }
    };

    fetchPortfolio();
  }, [goalId]);

  const handleSimulate = async () => {
    setSimLoading(true);
    setSimError(null);
    const token = localStorage.getItem("token") || "";
    try {
      const result = await createSimulateGoal(
        { goalId: Number(goalId), monthlyInvestment },
        token
      );
      setSimulation(result);
    } catch (err) {
      console.error("Simulate Error:", err);
      setSimError("Failed to perform simulation.");
    } finally {
      setSimLoading(false);
    }
  };

  const formatCurrency = (value: number) =>
    value?.toLocaleString("id-ID", { style: "currency", currency: "IDR" }) ??
    "Rp 0";

  const formatDate = (dateString: string) => {
    const options: Intl.DateTimeFormatOptions = {
      day: "numeric",
      month: "long",
      year: "numeric",
    };
    const date = new Date(dateString);
    return date.toLocaleDateString("en-GB", options);
  };

  if (loading)
    return (
      <div className="flex justify-center items-center h-64 animate__fadeIn">
        <div className="spinner-border text-blue-500" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );

  // Render the improved empty state if there's an error or no portfolio data
  if (error || !portfolio) {
    return <GoalDetailPageEmptyState />;
  }

  const products = portfolio.portfolioProductDetails ?? [];
  const categoryGroups: Record<string, PortfolioProductDetail[]> = {};
  products.forEach((product) => {
    if (!categoryGroups[product.productCategory]) {
      categoryGroups[product.productCategory] = [];
    }
    categoryGroups[product.productCategory].push(product);
  });

  const pieData = Object.entries(categoryGroups).map(([category, products]) => {
    const total = products.reduce((sum, p) => sum + p.investmentAmount, 0);
    return { name: category, value: total };
  });

  return (
    <div className="container mx-auto p-6 sm:p-8 md:p-10 animate__animated animate__fadeIn">
      <h1 className="text-3xl font-semibold text-gray-900 mb-6">
        Goal Portfolio
      </h1>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Goal Summary Card */}
        <div className="bg-white rounded-xl shadow-lg overflow-hidden border border-gray-200 animate__animated animate__fadeInLeft">
          <div className="p-6">
            <h2 className="text-xl font-semibold text-gray-800 mb-4 border-b pb-2">
              Goal Summary
            </h2>
            <div className="space-y-3 text-gray-700">
              <p>
                <span className="font-medium">Goal Name:</span> {goal?.goalName}
              </p>
              <p>
                <span className="font-medium">Target Date:</span>{" "}
                {goal?.targetDate ? formatDate(goal.targetDate) : "-"}
              </p>
              <p>
                <span className="font-medium">Total Investment:</span>{" "}
                <span className="text-indigo-600 font-semibold">
                  {formatCurrency(portfolio.totalInvestment)}
                </span>
              </p>
              <p>
                <span className="font-medium">Estimated Return:</span>{" "}
                <span className="text-green-600 font-semibold">
                  {formatCurrency(portfolio.estimatedReturn)}
                </span>
              </p>
              <p>
                <span className="font-medium">Profit:</span>{" "}
                <span
                  className={
                    portfolio.totalProfit >= 0
                      ? "text-green-600 font-semibold"
                      : "text-red-600 font-semibold"
                  }
                >
                  {formatCurrency(portfolio.totalProfit)}
                </span>
              </p>
              {goal?.insightMessage && (
                <div className="mt-2 p-3 bg-indigo-50 rounded-md text-indigo-700 text-sm">
                  <span className="font-medium">Insight:</span>{" "}
                  {goal.insightMessage}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Investment Allocation Chart Card */}
        <div className="bg-white rounded-xl shadow-lg overflow-hidden border border-gray-200 animate__animated animate__fadeInRight">
          <div className="p-6">
            <h2 className="text-xl font-semibold text-gray-800 mb-4 border-b pb-2">
              Investment Allocation
            </h2>
            <ResponsiveContainer width="100%" height={350}>
              <PieChart>
                <Pie
                  data={pieData}
                  dataKey="value"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  outerRadius={100}
                  innerRadius={60}
                  onClick={(d) => setSelectedCategory(d.name)}
                  label={({ cx, cy, midAngle, outerRadius, value }) => {
                    const RADIAN = Math.PI / 180;
                    const radius = outerRadius + 15;
                    const x = cx + radius * Math.cos(-midAngle * RADIAN);
                    const y = cy + radius * Math.sin(-midAngle * RADIAN);
                    const percent =
                      (value /
                        pieData.reduce((acc, cur) => acc + cur.value, 0)) *
                      100;

                    return percent > 2 ? ( // Only show labels for significant percentages
                      <text
                        x={x}
                        y={y}
                        fill="#374151"
                        textAnchor={x > cx ? "start" : "end"}
                        dominantBaseline="central"
                        style={{ fontSize: 12, fontWeight: "bold" }}
                      >
                        {`${Math.round(percent)}%`}
                      </text>
                    ) : null;
                  }}
                >
                  {pieData.map((entry, index) => (
                    <Cell
                      key={`cell-${index}`}
                      fill={COLORS[index % COLORS.length]}
                    />
                  ))}
                </Pie>
                <Tooltip
                  formatter={(value) => formatCurrency(value as number)}
                />
                <Legend
                  layout="vertical"
                  align="right"
                  verticalAlign="middle"
                  iconSize={14}
                  wrapperStyle={{ marginLeft: 20 }}
                />
              </PieChart>
            </ResponsiveContainer>
            <div className="mt-4 text-gray-600 flex items-center space-x-2">
              <AiOutlineInfoCircle className="text-blue-500" />
              <span className="text-sm">
                Click on a slice to view product details.
              </span>
            </div>
          </div>
        </div>
      </div>

      {selectedCategory && (
        <div className="mt-8 bg-white rounded-xl shadow-lg overflow-hidden border border-gray-200 animate__animated animate__fadeInUp">
          <div className="p-6">
            <h2 className="text-lg font-semibold mb-4">
              Products in{" "}
              <span className="text-indigo-600">{selectedCategory}</span>
            </h2>
            <ul className="space-y-4">
              {categoryGroups[selectedCategory]?.map((product, idx) => (
                <li
                  key={idx}
                  className="p-4 border rounded-md bg-gray-50 hover:bg-gray-100 transition-shadow duration-200 shadow-sm hover:shadow-md"
                >
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="font-semibold text-gray-800">
                        {product.productName}
                      </p>
                      <p className="text-sm text-gray-600">
                        Category: {product.productCategory}
                      </p>
                      <p className="text-sm text-gray-600">
                        Lot: {product.totalLot}
                      </p>
                      <p className="text-sm text-gray-600">
                        Buy Price: {formatCurrency(product.buyPrice)}
                      </p>
                      <p className="text-sm text-gray-600">
                        Investment: {formatCurrency(product.investmentAmount)}
                      </p>
                      <p className="text-sm text-green-500">
                        Est. Return: {formatCurrency(product.estimatedReturn)}
                      </p>
                      <p className="text-sm text-gray-700">
                        Profit:{" "}
                        <span
                          className={
                            product.profit >= 0
                              ? "text-green-500"
                              : "text-red-500"
                          }
                        >
                          {formatCurrency(product.profit)}
                        </span>
                      </p>
                      <p className="text-sm text-gray-600">
                        Buy Date:{" "}
                        {new Date(product.buyDate).toLocaleDateString()}
                      </p>
                    </div>
                    <button
                      onClick={() => {
                        localStorage.setItem(
                          "productDetail",
                          JSON.stringify(product)
                        );
                        localStorage.setItem(
                          "goalId",
                          portfolio.goalId.toString()
                        );
                        navigate(`/sell/${product.productId}`, {
                          state: {
                            productDetail: product,
                            goalId: portfolio.goalId,
                            goalName: goal?.goalName, // tambahkan ini
                          },
                        });
                      }}
                      className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 text-sm rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-1"
                    >
                      Sell
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        </div>
      )}

      {/* Simulation Section */}
      <div className="mt-8 bg-white rounded-xl shadow-lg overflow-hidden border border-gray-200 animate__animated animate__fadeInUp">
        <div className="p-6">
          <h2 className="text-xl font-semibold mb-4 border-b pb-2">
            Investment Simulation
          </h2>
          <div className="flex flex-col sm:flex-row items-center gap-4">
            <input
              type="number"
              value={monthlyInvestment}
              onChange={(e) => setMonthlyInvestment(Number(e.target.value))}
              className="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:w-64 border-gray-300 rounded-md py-2 px-3 text-gray-700"
              placeholder="Monthly Investment (IDR)"
            />
            <button
              onClick={handleSimulate}
              className="bg-indigo-500 hover:bg-indigo-600 text-white font-semibold py-2 px-4 rounded-md shadow-md focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-1"
              disabled={simLoading}
            >
              {simLoading ? (
                <svg
                  className="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
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
                "Simulate"
              )}
            </button>
          </div>
          {simError && <p className="text-red-500 mt-2 text-sm">{simError}</p>}
          {simulation && (
            <div className="mt-4 text-sm sm:text-base text-gray-700">
              <p>
                Months to reach target:{" "}
                <span className="text-indigo-600 font-semibold">
                  {simulation.monthsToAchieve}
                </span>
              </p>
              <p className="italic mt-1">
                With a monthly investment of {formatCurrency(monthlyInvestment)}
                , you are projected to reach your target.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default GoalDetailPage;
