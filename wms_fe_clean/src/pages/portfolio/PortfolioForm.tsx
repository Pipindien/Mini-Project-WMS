import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getGoals, getSuggestedPortfolio } from "../../services/goal/api";
import {
  Goal,
  PortfolioRecommendationResponse,
  Product,
  RecommendedProducts,
  SuggestedPortfolioItem,
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
  const [goal, setGoal] = useState<Goal | null>(null);
  const [suggestedPortfolio, setSuggestedPortfolio] = useState<
    SuggestedPortfolioItem[]
  >([]);
  const [recommendedProducts, setRecommendedProducts] =
    useState<RecommendedProducts>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      const token = localStorage.getItem("token");
      try {
        const [goalRes, portfolioRes] = await Promise.all([
          getGoal(goalId!, token!),
          getSuggestedPortfolio(goalId!, token!),
        ]);

        setGoal(goalRes);
        setSuggestedPortfolio(portfolioRes.suggestedPortfolio);
        setRecommendedProducts(portfolioRes.recommendedProducts);
      } catch (err) {
        console.error("Error:", err);
        setError("Gagal ambil data detail goal atau rekomendasi portofolio.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [goalId]);

  if (loading) return <div className="text-center mt-10">Loading...</div>;
  if (error) return <div className="text-red-500 text-center">{error}</div>;
  if (!goal)
    return (
      <div className="text-red-500 text-center">Data goal tidak ditemukan.</div>
    );

  const pieData = suggestedPortfolio.map((item) => ({
    name: item.category,
    value: item.percentage,
  }));

  const progress =
    goal.targetAmount === 0
      ? 0
      : Math.min(
          100,
          Math.round((goal.currentAmount / goal.targetAmount) * 100)
        );

  const formattedDate = goal.updatedDate
    ? new Date(goal.updatedDate).toLocaleDateString("id-ID", {
        day: "2-digit",
        month: "short",
        year: "numeric",
      })
    : "-";

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 p-6">
      {/* KIRI: GOAL DETAIL */}
      <div className="bg-white p-6 shadow-md rounded">
        <h2 className="text-2xl font-bold mb-4">Detail Goal</h2>
        <p className="text-lg font-semibold">{goal.goalName}</p>
        <p className="text-gray-600 text-sm">Status: {goal.status}</p>
        <p className="text-gray-600 text-sm capitalize">
          Risk Tolerance: {goal.riskTolerance}
        </p>
        <p className="text-gray-500 text-sm mb-4">Updated: {formattedDate}</p>

        <div className="mb-4">
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

        <p className="text-gray-700 mt-4">{goal.insightMessage}</p>
      </div>

      {/* KANAN: PIE + PRODUK */}
      <div>
        <h2 className="text-2xl font-bold mb-4">
          Rekomendasi Alokasi Portofolio
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
              fill="#8884d8"
              onClick={(d) => setSelectedCategory(d.name)}
            >
              {pieData.map((_, index) => (
                <Cell key={index} fill={COLORS[index % COLORS.length]} />
              ))}
            </Pie>
            <Tooltip />
            <Legend />
          </PieChart>
        </ResponsiveContainer>

        {selectedCategory && (
          <div className="mt-6">
            <h3 className="text-xl font-semibold mb-2">
              Produk Rekomendasi: {selectedCategory}
            </h3>
            <ul className="space-y-2">
              {(recommendedProducts[selectedCategory] || []).map(
                (product: Product, idx) => (
                  <li
                    key={idx}
                    className="p-4 border rounded shadow-sm bg-white"
                  >
                    <p className="font-semibold">{product.productName}</p>
                    <p className="text-sm text-gray-500">
                      Harga: Rp{product.productPrice.toLocaleString()}
                    </p>
                    <p className="text-sm text-gray-500">
                      Estimasi Return: {(product.productRate * 100).toFixed(2)}%
                    </p>
                  </li>
                )
              )}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};

export default GoalDetailPage;
