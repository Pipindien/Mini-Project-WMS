import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import useProductById from "../hooks/useProduct/useProductById";
import useSimulateProduct from "../hooks/useSimulate/useSimulate";

const ProductDetail: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { product, loading, error } = useProductById(id);
  const {
    data,
    loading: simulating,
    error: simulateError,
    simulate,
  } = useSimulateProduct();

  const [monthlyInvestment, setMonthlyInvestment] = useState<number>(0);
  const [years, setYears] = useState<number>(1);

  if (loading)
    return <div className="text-center mt-20 text-red-500">Loading...</div>;
  if (error)
    return <div className="text-center mt-20 text-red-500">{error}</div>;
  if (!product)
    return (
      <div className="text-center mt-20 text-gray-500">Product not found.</div>
    );

  const handleBuy = () => {
    navigate(`/buy/${product.productId}`);
  };

  const handleSimulate = () => {
    const token = localStorage.getItem("token") || "";
    simulate({ productId: product.productId, monthlyInvestment, years }, token);
  };

  return (
    <div className="container mx-auto p-6">
      <div className="max-w-3xl mx-auto bg-white rounded-xl shadow-md overflow-hidden md:flex">
        <div className="p-8 md:w-full">
          <h2 className="text-2xl font-bold mb-4">{product.productName}</h2>
          <p className="text-lg font-semibold text-blue-600">
            Rp {product.productPrice.toLocaleString()}
          </p>
          <p className="text-md text-gray-600 mt-2">
            Rate: {product.productRate}%
          </p>

          <button
            onClick={handleBuy}
            className="mt-6 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
          >
            Beli
          </button>

          {/* Simulasi Form */}
          <div className="mt-8">
            <h3 className="text-lg font-semibold mb-2">Simulasi Investasi</h3>
            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Investasi per bulan (Rp)
                </label>
                <input
                  type="number"
                  className="w-full border px-3 py-2 rounded mt-1"
                  value={monthlyInvestment}
                  onChange={(e) => setMonthlyInvestment(Number(e.target.value))}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Jangka waktu (tahun)
                </label>
                <input
                  type="number"
                  className="w-full border px-3 py-2 rounded mt-1"
                  value={years}
                  onChange={(e) => setYears(Number(e.target.value))}
                />
              </div>
              <button
                onClick={handleSimulate}
                className="w-full mt-4 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition"
              >
                Simulasikan
              </button>
            </div>
          </div>

          {/* Hasil Simulasi */}
          {simulating && (
            <p className="mt-4 text-blue-600">Menghitung simulasi...</p>
          )}
          {simulateError && (
            <p className="mt-4 text-red-500">{simulateError}</p>
          )}
          {data && (
            <div className="mt-6 bg-gray-100 p-4 rounded shadow">
              <p className="font-medium text-green-700 mb-2">
                {data.insightMessage}
              </p>
              <ul className="text-sm text-gray-700 space-y-1">
                <li>Future Value: Rp {data.futureValue.toLocaleString()}</li>
                <li>Estimasi bulan tercapai: {data.monthsToAchieve} bulan</li>
                <li>
                  Investasi bulanan: Rp{" "}
                  {data.monthlyInvestmentNeeded.toLocaleString()}
                </li>
              </ul>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;
