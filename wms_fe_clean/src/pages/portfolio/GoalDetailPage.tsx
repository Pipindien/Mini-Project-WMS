import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getPortfolioByGoalId } from "../../services/goal/api";
import {
  PortfolioDashboardResponse,
  PortfolioProductDetail,
} from "../../services/goal/type";
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

const COLORS = ["#8884d8", "#82ca9d", "#ffc658", "#ff7f50", "#00c49f"];

const GoalDetailPage: React.FC = () => {
  const { goalId } = useParams();
  const navigate = useNavigate();
  const [portfolio, setPortfolio] = useState<PortfolioDashboardResponse | null>(
    null
  );
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);

  useEffect(() => {
    const fetchPortfolio = async () => {
      const token = localStorage.getItem("token");
      try {
        const data = await getPortfolioByGoalId(goalId!, token!);
        setPortfolio(data);
      } catch (err) {
        console.error("Error:", err);
        setError("Failed to fetch portfolio data.");
      } finally {
        setLoading(false);
      }
    };

    fetchPortfolio();
  }, [goalId]);

  const formatCurrency = (value: number) =>
    value?.toLocaleString("id-ID", { style: "currency", currency: "IDR" }) ??
    "Rp 0";

  if (loading) return <div className="text-center mt-10">Loading...</div>;
  if (error) return <div className="text-red-500 text-center">{error}</div>;
  if (!portfolio)
    return (
      <div className="text-red-500 text-center">Portfolio data not found.</div>
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

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 p-6">
      {/* Left: Goal Summary */}
      <div className="bg-white p-6 shadow-lg rounded-xl">
        <h2 className="text-2xl font-semibold text-gray-800 mb-4">
          Goal Overview
        </h2>
        <p className="text-xl font-bold text-gray-700">
          Goal ID: {portfolio.goalId}
        </p>
        <p className="text-sm text-gray-600 mt-2">
          Risk Tolerance: (auto-assigned)
        </p>
        <div className="mt-4 space-y-1 text-sm text-gray-600">
          <p>Total Investment: {formatCurrency(portfolio.totalInvestment)}</p>
          <p>Estimated Return: {formatCurrency(portfolio.estimatedReturn)}</p>
          <p>Profit: {formatCurrency(portfolio.totalProfit)}</p>
        </div>
      </div>

      {/* Right: Pie Chart */}
      <div className="bg-white p-6 shadow-lg rounded-xl">
        <h2 className="text-2xl font-semibold text-gray-800 mb-4">
          Allocation Overview
        </h2>
        <ResponsiveContainer width="100%" height={300}>
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
            >
              {pieData.map((entry, index) => (
                <Cell
                  key={`cell-${index}`}
                  fill={COLORS[index % COLORS.length]}
                />
              ))}
            </Pie>
            <Tooltip />
            <Legend />
          </PieChart>
        </ResponsiveContainer>

        {selectedCategory && (
          <div className="mt-6">
            <h3 className="text-xl font-semibold text-gray-800 mb-4">
              Products in {selectedCategory}
            </h3>
            <ul className="space-y-4">
              {categoryGroups[selectedCategory]?.map((product, idx) => (
                <li
                  key={idx}
                  className="p-4 border rounded-lg shadow bg-white hover:bg-gray-50 transition"
                >
                  <div className="flex justify-between items-center">
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
                      <p className="text-sm text-gray-600">
                        Est. Return: {formatCurrency(product.estimatedReturn)}
                      </p>
                      <p className="text-sm text-gray-600">
                        Profit: {formatCurrency(product.profit)}
                      </p>
                      <p className="text-sm text-gray-600">
                        Buy Date:{" "}
                        {new Date(product.buyDate).toLocaleDateString()}
                      </p>
                    </div>
                    <button
                      onClick={() =>
                        navigate(`/sell/${product.productId}`, {
                          state: {
                            productDetail: product,
                            goalId: portfolio.goalId,
                          },
                        })
                      }
                      className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded"
                    >
                      Sell
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};

export default GoalDetailPage;
