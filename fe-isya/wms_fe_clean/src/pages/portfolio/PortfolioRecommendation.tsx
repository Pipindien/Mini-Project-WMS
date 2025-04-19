import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getSuggestedPortfolio } from "../../services/goal/api";
import {
  PortfolioRecommendationResponse,
  SuggestedPortfolioItem,
  Product,
} from "../../services/goal/type";

const PortfolioRecommendation: React.FC = () => {
  const { goalId } = useParams();
  const [recommendation, setRecommendation] =
    useState<PortfolioRecommendationResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

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

  if (loading) return <div className="text-center mt-10">Loading...</div>;
  if (error) return <div className="text-red-500 text-center">{error}</div>;

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-4">
        Rekomendasi Alokasi Portofolio
      </h2>
      <ul className="space-y-2 mb-6">
        {recommendation?.suggestedPortfolio.map(
          (item: SuggestedPortfolioItem, index: number) => (
            <li
              key={index}
              className="p-4 border rounded shadow-sm bg-white flex justify-between items-center"
            >
              <p className="font-semibold">{item.category}</p>
              <span className="text-green-600 font-bold">
                {item.percentage}%
              </span>
            </li>
          )
        )}
      </ul>

      <h2 className="text-2xl font-bold mb-4">Produk Rekomendasi</h2>
      {Object.entries(recommendation?.recommendedProducts || {}).map(
        ([category, products]) => (
          <div key={category} className="mb-6">
            <h3 className="text-xl font-semibold mb-2">{category}</h3>
            <ul className="space-y-2">
              {products.map((product: Product, index: number) => (
                <li
                  key={index}
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
              ))}
            </ul>
          </div>
        )
      )}
    </div>
  );
};

export default PortfolioRecommendation;
