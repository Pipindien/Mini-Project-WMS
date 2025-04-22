import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getSuggestedPortfolio } from "../../services/goal/api";
import {
  PortfolioRecommendationResponse,
  Product,
} from "../../services/goal/type";
import useGoalById from "../hooks/useGoal/useGoalById";
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  ResponsiveContainer,
  LabelList,
} from "recharts";

const COLORS = [
  "#6366F1",
  "#10B981",
  "#F59E0B",
  "#EF4444",
  "#3B82F6",
  "#8B5CF6",
];

const RADIAN = Math.PI / 180;

// Label inside pie segments
const renderCustomizedLabel = ({
  cx,
  cy,
  midAngle,
  innerRadius,
  outerRadius,

  name,
}: any) => {
  const radius = innerRadius + (outerRadius - innerRadius) * 0.6;
  const x = cx + radius * Math.cos(-midAngle * RADIAN);
  const y = cy + radius * Math.sin(-midAngle * RADIAN);

  return (
    <text
      x={x}
      y={y}
      fill="white"
      textAnchor="middle"
      dominantBaseline="central"
      fontSize={12}
      fontWeight="600"
    >
      {`${name}`}
    </text>
  );
};

const PortfolioRecommendation: React.FC = () => {
  const { goalId } = useParams();
  const { goal } = useGoalById(goalId);
  const navigate = useNavigate();
  const [recommendation, setRecommendation] =
    useState<PortfolioRecommendationResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);

  useEffect(() => {
    const fetchRecommendation = async () => {
      const token = localStorage.getItem("token");
      try {
        const data = await getSuggestedPortfolio(goalId!, token!);
        setRecommendation(data);
      } catch (err) {
        console.error("Error fetching suggested portfolio:", err);
        setError("Gagal ambil rekomendasi portofolio");
      } finally {
        setLoading(false);
      }
    };

    fetchRecommendation();
  }, [goalId]);

  const handleSegmentClick = (data: any) => {
    setSelectedCategory(data.categoryType);
  };

  const formattedDate = goal?.targetDate
    ? new Date(goal.targetDate).toLocaleDateString("id-ID", {
        day: "2-digit",
        month: "short",
        year: "numeric",
      })
    : "Invalid Date";

  const top3Products = selectedCategory
    ? [...(recommendation?.recommendedProducts[selectedCategory] || [])]
        .sort((a, b) => b.productRate - a.productRate)
        .slice(0, 3)
    : [];

  if (loading)
    return (
      <div className="text-center mt-10 text-lg font-medium text-gray-700">
        Loading...
      </div>
    );
  if (error)
    return (
      <div className="text-red-500 text-center mt-10 font-semibold">
        {error}
      </div>
    );

  return (
    <div className="p-6 max-w-6xl mx-auto">
      <h2 className="text-3xl font-bold text-center mb-4 text-indigo-700">
        Alokasi Rekomendasi Portofolio
      </h2>

      {/* Goal Info */}
      <div className="bg-white p-6 rounded-2xl shadow-lg mb-10 flex flex-col sm:flex-row sm:justify-between sm:items-center text-center sm:text-left border border-gray-100">
        <div>
          <h3 className="text-xl font-semibold text-gray-800">
            Goal: {goal?.goalName}
          </h3>
          <p className="text-gray-600">
            Risk Tolerance:{" "}
            <span className="font-medium capitalize text-blue-500">
              {goal?.riskTolerance?.toLowerCase()}
            </span>
          </p>
        </div>
        <div className="mt-4 sm:mt-0">
          <p className="text-gray-600">
            Target Amount:{" "}
            <span className="font-semibold text-green-600">
              Rp{goal?.targetAmount?.toLocaleString()}
            </span>
          </p>
          <p className="text-gray-600">
            Target Date:{" "}
            <span className="font-semibold text-green-600">
              {formattedDate}
            </span>
          </p>
        </div>
      </div>

      {/* Doughnut Chart */}
      <div className="w-full flex justify-center mb-10">
        <ResponsiveContainer width="100%" height={350}>
          <PieChart>
            <Pie
              data={recommendation?.suggestedPortfolio}
              dataKey="percentage"
              nameKey="categoryType"
              cx="50%"
              cy="50%"
              outerRadius={110}
              innerRadius={60}
              paddingAngle={2}
              onClick={handleSegmentClick}
              labelLine={false}
              label={({ name }) => name}
            >
              {(recommendation?.suggestedPortfolio || []).map(
                (entry, index) => (
                  <Cell
                    key={`cell-${index}`}
                    fill={COLORS[index % COLORS.length]}
                    cursor="pointer"
                  />
                )
              )}
              <LabelList
                dataKey="categoryType"
                content={renderCustomizedLabel}
              />
            </Pie>
            <Tooltip formatter={(value: any) => `${value}%`} />
          </PieChart>
        </ResponsiveContainer>
      </div>

      {/* Selected Category Info */}
      {selectedCategory && (
        <>
          <h3 className="text-2xl font-semibold text-gray-800 mb-4 text-center">
            Top 3 Produk di Kategori{" "}
            <span className="text-indigo-600">{selectedCategory}</span>
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
            {top3Products.map((product: Product, index: number) => (
              <div
                key={index}
                onClick={() => navigate(`/product/${product.productId}`)}
                className="cursor-pointer p-5 rounded-2xl bg-white border border-gray-100 shadow-md hover:shadow-xl transition-all duration-200"
              >
                <p className="font-semibold text-indigo-700 text-lg mb-2">
                  {product.productName}
                </p>
                <p className="text-sm text-gray-500">
                  Harga: Rp{product.productPrice.toLocaleString()}
                </p>
                <p className="text-sm text-gray-500">
                  Estimasi Return: {(product.productRate * 100).toFixed(2)}%
                </p>
                <p className="text-sm text-indigo-600 mt-3 underline font-medium">
                  Lihat Detail
                </p>
              </div>
            ))}
          </div>
        </>
      )}
    </div>
  );
};

export default PortfolioRecommendation;
