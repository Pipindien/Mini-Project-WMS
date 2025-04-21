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
import { AiOutlineInfoCircle } from "react-icons/ai"; // Import the icon

const COLORS = ["#3b82f6", "#10b981", "#f59e0b", "#e11d48", "#8b5cf6"]; // More vibrant Tailwind colors

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
          "It seems you haven't started investing for this goal. Once you make your first investment, your portfolio details will show up here."
        );
      } finally {
        setLoading(false);
      }
    };

    fetchPortfolio();
  }, [goalId]);

  const handleSimulate = async () => {
    console.log("Simulasi dijalankan!");
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
      setSimError("Gagal melakukan simulasi.");
    } finally {
      setSimLoading(false);
    }
  };

  const formatCurrency = (value: number) =>
    value?.toLocaleString("id-ID", { style: "currency", currency: "IDR" }) ??
    "Rp 0";

  if (loading)
    return (
      <div className="text-center mt-10 text-blue-500 animate__fadeIn">
        Loading portfolio details...
      </div>
    );
  if (error)
    return (
      <div className="text-red-500 text-center py-4 animate__fadeIn">
        {error}
      </div>
    );
  if (!portfolio)
    return (
      <div className="text-yellow-500 text-center py-4 animate__fadeIn">
        Portfolio data not found for this goal.
      </div>
    );

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

  const formatDate = (dateString: string) => {
    const options: Intl.DateTimeFormatOptions = {
      day: "numeric",
      month: "long",
      year: "numeric",
    };
    const date = new Date(dateString);
    return date.toLocaleDateString("en-GB", options); // Bisa ganti 'en-GB' ke 'id-ID' kalau mau Bahasa Indonesia
  };

  return (
    <div className="container mx-auto p-4 sm:p-6 md:p-8 animate__fadeInUp">
      <h1 className="text-2xl sm:text-3xl font-semibold text-gray-900 mb-4 sm:mb-6 animate__fadeInUp">
        Goal Portfolio
      </h1>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 sm:gap-6 md:gap-8">
        {/* Left: Goal Summary */}
        <div className="bg-white shadow-md hover:shadow-xl rounded-2xl p-5 sm:p-6 border border-gray-200 transition-all duration-300 ease-in-out">
          <h2 className="text-xl sm:text-2xl font-bold text-gray-800 mb-4 border-b border-gray-200 pb-2">
            Goal Summary
          </h2>

          <div className="space-y-2 text-sm sm:text-base text-gray-700">
            <p>
              <span className="font-medium text-gray-600">Goal Name:</span>{" "}
              <span className="font-semibold text-gray-800">
                {goal?.goalName}
              </span>
            </p>
            <p>
              <span className="font-medium text-gray-600">Target Date:</span>{" "}
              <span className="font-semibold text-gray-800">
                {goal?.targetDate ? formatDate(goal.targetDate) : "-"}
              </span>
            </p>
          </div>

          <div className="mt-5 space-y-3">
            <div className="flex justify-between items-center">
              <span className="text-gray-600 font-medium">
                Total Investment:
              </span>
              <span className="text-blue-600 font-semibold">
                {formatCurrency(portfolio.totalInvestment)}
              </span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-gray-600 font-medium">
                Estimated Return:
              </span>
              <span className="text-green-600 font-semibold">
                {formatCurrency(portfolio.estimatedReturn)}
              </span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-gray-600 font-medium">Profit:</span>
              <span
                className={
                  portfolio.totalProfit >= 0
                    ? "text-green-600 font-semibold"
                    : "text-red-600 font-semibold"
                }
              >
                {formatCurrency(portfolio.totalProfit)}
              </span>
            </div>

            <div className="space-y-2 text-sm sm:text-base text-gray-700">
              <p>
                <span className="font-medium text-gray-600">
                  Based on your current condition:{" "}
                </span>{" "}
                <span className="font-semibold text-gray-800">
                  {goal?.insightMessage}
                </span>
              </p>
            </div>
          </div>
        </div>

        {/* Right: Pie Chart */}
        <div className="bg-white shadow-md rounded-xl p-4 sm:p-6 border border-gray-200 hover:shadow-2xl transform transition-all duration-300 animate__fadeInUp">
          <h2 className="text-lg sm:text-xl font-semibold text-gray-800 mb-2 sm:mb-4 border-b pb-2">
            Investment Allocation
          </h2>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={pieData}
                dataKey="value"
                nameKey="name"
                cx="50%"
                cy="50%"
                outerRadius={90}
                innerRadius={50}
                onClick={(d) => setSelectedCategory(d.name)}
                label={({ cx, cy, midAngle, outerRadius, value }) => {
                  const RADIAN = Math.PI / 180;
                  const radius = outerRadius + 10;
                  const x = cx + radius * Math.cos(-midAngle * RADIAN);
                  const y = cy + radius * Math.sin(-midAngle * RADIAN);
                  const percent =
                    (value / pieData.reduce((acc, cur) => acc + cur.value, 0)) *
                    100;

                  return (
                    <text
                      x={x}
                      y={y}
                      fill="black"
                      textAnchor="middle"
                      dominantBaseline="central"
                      style={{ fontSize: 12, fontWeight: "bold" }}
                    >
                      {`${Math.round(percent)}%`}
                    </text>
                  );
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
                formatter={(value) => {
                  if (typeof value === "number") {
                    return formatCurrency(value);
                  }
                  return value;
                }}
              />
              <Legend
                layout={window.innerWidth >= 640 ? "vertical" : "horizontal"}
                align={window.innerWidth >= 640 ? "right" : "center"}
                verticalAlign={window.innerWidth >= 640 ? "middle" : "bottom"}
              />
            </PieChart>
          </ResponsiveContainer>

          {/* Tip Icon Below Pie Chart */}
          <div className="mt-4 text-gray-600 flex items-center space-x-2">
            <AiOutlineInfoCircle className="text-blue-500" />
            <span className="text-sm">
              Click on a category in the pie chart to view purchased stocks.
            </span>
          </div>
        </div>
      </div>

      {selectedCategory && (
        <div className="mt-6 sm:mt-8 bg-white shadow-md rounded-xl p-4 sm:p-6 border border-gray-200 animate__fadeInUp">
          <h2 className="text-lg sm:text-xl font-semibold text-gray-800 mb-2 sm:mb-4 border-b pb-2">
            Products in{" "}
            <span className="text-blue-500">{selectedCategory}</span>
          </h2>
          <ul className="space-y-3 sm:space-y-4">
            {categoryGroups[selectedCategory]?.map((product, idx) => (
              <li
                key={idx}
                className="p-3 sm:p-4 border rounded-lg shadow-sm bg-gray-50 hover:bg-gray-100 transition duration-200 transform hover:scale-105"
              >
                <div className="sm:flex sm:justify-between sm:items-center">
                  <div className="mb-2 sm:mb-0">
                    <p className="font-semibold text-gray-800 text-sm sm:text-md">
                      {product.productName}
                    </p>
                    <p className="text-xs sm:text-sm text-gray-600">
                      Category: {product.productCategory}
                    </p>
                    <p className="text-xs sm:text-sm text-gray-600">
                      Lot: {product.totalLot}
                    </p>
                    <p className="text-xs sm:text-sm text-gray-600">
                      Buy Price: {formatCurrency(product.buyPrice)}
                    </p>
                    <p className="text-xs sm:text-sm text-gray-600">
                      Investment:{" "}
                      <span className="font-medium">
                        {formatCurrency(product.investmentAmount)}
                      </span>
                    </p>
                    <p className="text-xs sm:text-sm text-gray-600">
                      Est. Return:{" "}
                      <span className="text-green-500 font-medium">
                        {formatCurrency(product.estimatedReturn)}
                      </span>
                    </p>
                    <p className="text-xs sm:text-sm text-gray-600">
                      Profit:{" "}
                      <span
                        className={
                          product.profit >= 0
                            ? "text-green-500 font-medium"
                            : "text-red-500 font-medium"
                        }
                      >
                        {formatCurrency(product.profit)}
                      </span>
                    </p>
                    <p className="text-xs sm:text-sm text-gray-600">
                      Buy Date: {new Date(product.buyDate).toLocaleDateString()}
                    </p>
                  </div>
                  <div>
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
                          },
                        });
                      }}
                      className="mt-2 sm:mt-0 inline-block bg-red-500 hover:bg-red-600 text-white text-xs sm:text-sm px-4 py-2 rounded-lg shadow transition duration-200"
                    >
                      Sell
                    </button>
                  </div>
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Simulasi Investasi Section */}
      <div className="mt-8 bg-white shadow-md rounded-xl p-4 sm:p-6 border border-gray-200 animate__fadeInUp">
        <h2 className="text-lg sm:text-xl font-semibold text-gray-800 mb-2 sm:mb-4 border-b pb-2">
          Investment Simulation
        </h2>
        <div className="flex flex-col sm:flex-row sm:items-center sm:space-x-4">
          <input
            type="number"
            className="w-full sm:w-60 px-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={monthlyInvestment}
            onChange={(e) => setMonthlyInvestment(Number(e.target.value))}
          />
          <button
            onClick={handleSimulate}
            className="mt-3 sm:mt-0 bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg shadow transition duration-200"
          >
            {simLoading ? "Simulating..." : "Simulate"}
          </button>
        </div>
        {simError && <p className="text-red-500 mt-2 text-sm">{simError}</p>}
        {simulation && (
          <div className="mt-4">
            <p className="text-sm sm:text-md text-gray-700">
              Months to reach target:{" "}
              <span className="text-blue-600 font-semibold">
                {simulation.monthsToAchieve}
              </span>
            </p>
            <p className="text-sm sm:text-md text-gray-600 italic mt-2">
              {`With a monthly investment of ${new Intl.NumberFormat("id-ID", {
                style: "currency",
                currency: "IDR",
                minimumFractionDigits: 0,
              }).format(
                monthlyInvestment
              )}, you are projected to reach your target of ${new Intl.NumberFormat(
                "id-ID",
                {
                  style: "currency",
                  currency: "IDR",
                  minimumFractionDigits: 0,
                }
              ).format(goal.targetAmount)} in approximately ${
                simulation.monthsToAchieve
              } months. Keep investing consistently to stay on track!`}
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default GoalDetailPage;
