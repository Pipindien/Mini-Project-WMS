import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import useProductById from "../hooks/useProduct/useProductById";
import useSimulateProduct from "../hooks/useSimulate/useSimulate";
import { useState } from "react";

const ProductDetail: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { product, loading, error } = useProductById(id);

  const formatCurrency = (value: number) =>
    value.toLocaleString("id-ID", { style: "currency", currency: "IDR" });

  const formatRate = (rate: number) => `${rate.toFixed(2)}%`;
  const {
    data,
    loading: simulating,
    error: simulateError,
    simulate,
  } = useSimulateProduct();

  const [monthlyInvestment, setMonthlyInvestment] = useState<number>(0);
  const [years, setYears] = useState<number>(1);
  const isValid = monthlyInvestment > 0 && years >= 1 && years <= 10;

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="w-full max-w-2xl bg-white rounded-lg shadow-md p-6">
          <div className="text-2xl text-gray-500 animate-pulse">Loading...</div>
          <div className="mt-4 space-y-4">
            <div className="h-64 bg-gray-300 rounded-md animate-pulse"></div>
            <div className="space-y-2">
              <div className="h-6 w-3/4 bg-gray-300 rounded-md animate-pulse"></div>
              <div className="h-6 w-1/2 bg-gray-300 rounded-md animate-pulse"></div>
              <div className="h-10 w-full bg-gray-300 rounded-md animate-pulse"></div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center mt-20 text-red-500 font-semibold">
        {error}
      </div>
    );
  }

  if (!product) {
    return (
      <div className="text-center mt-20 text-gray-500">Product not found.</div>
    );
  }

  const handleBuy = () => {
    navigate(`/buy/${product.productId}`);
  };

  const handleSimulate = () => {
    const token = localStorage.getItem("token") || "";
    simulate({ productId: product.productId, monthlyInvestment, years }, token);
  };

  const getParaphrasedDescription = (
    description: string | undefined,
    productName: string
  ) => {
    const briefDescription = `${productName} offers a blend of innovative features designed to enhance your investment strategy 📈. It provides the capability to optimize your portfolio 🚀 and potentially improve returns 💰. A perfect choice for investors looking for versatility and growth 😊.`;

    if (description && description.length > 100) {
      return description
        .replace(/This product/gi, productName)
        .replace(/this product/gi, productName)
        .replace(/it is/gi, `${productName} is`)
        .replace(/it has/gi, `${productName} has`)
        .replace(/its/gi, `${productName}'s`)
        .replace(/the product/gi, productName)
        .replace(/a product/gi, productName)
        .replace(/product/gi, productName);
    }

    return briefDescription;
  };

  const paraphrasedDescription = getParaphrasedDescription(
    product.description,
    product.productName
  );

  return (
    <div className="bg-gradient-to-br from-gray-50 to-gray-200 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-lg shadow-xl overflow-hidden">
          <div className="grid md:grid-cols-2 gap-8">
            <div className="bg-gray-100 flex items-center justify-center p-4">
              <img
                src={
                  product.imageUrl ||
                  "https://www.pngkey.com/png/full/52-526880_stock-transparent.png"
                }
                alt={product.productName}
                className="object-contain h-full w-full rounded-lg"
                style={{ maxHeight: "400px" }}
              />
            </div>

            <div className="p-6 flex flex-col justify-between">
              <div>
                <h1
                  className="text-3xl font-bold text-gray-900 mb-4"
                  style={{
                    backgroundImage:
                      "linear-gradient(to right, #6366f1, #8b5cf6)",
                    color: "transparent",
                    backgroundClip: "text",
                  }}
                >
                  {product.productName}
                </h1>

                <p className="text-2xl font-semibold text-indigo-600 mb-4">
                  {formatCurrency(product.productPrice)}
                </p>

                <div className="flex items-center mb-3">
                  <span className="text-gray-700 mr-2">Rate:</span>
                  <span
                    className={
                      product.productRate > 0.7
                        ? "bg-green-100 text-green-800 border border-green-300 px-3 py-1 rounded-full font-medium"
                        : "bg-yellow-100 text-yellow-800 border border-yellow-300 px-3 py-1 rounded-full font-medium"
                    }
                  >
                    {formatRate(product.productRate)}
                  </span>
                </div>

                <p className="text-sm text-gray-600 mb-2">
                  Category:{" "}
                  <span className="font-medium text-gray-800">
                    {product.productCategory}
                  </span>
                </p>

                <p className="text-gray-700 leading-relaxed text-md mb-6">
                  {paraphrasedDescription}
                </p>
              </div>

              <button
                onClick={handleBuy}
                className="w-full bg-green-600 text-white py-2 px-6 rounded-lg font-semibold shadow hover:bg-green-700 transition-transform duration-300 hover:scale-105"
              >
                Beli Sekarang
              </button>
            </div>
          </div>
        </div>
        {/* Investment Simulation */}
        <div className="mt-10">
          <h3 className="text-xl font-semibold text-gray-800 mb-4">
            Investment Simulation
          </h3>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Monthly Investment (Rp)
              </label>
              <input
                type="number"
                className="w-full border border-gray-300 px-4 py-2 rounded-lg mt-1 focus:outline-none focus:ring-2 focus:ring-green-500"
                value={monthlyInvestment}
                onChange={(e) => setMonthlyInvestment(Number(e.target.value))}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">
                Duration (Years)
              </label>
              <input
                type="number"
                className="w-full border border-gray-300 px-4 py-2 rounded-lg mt-1 focus:outline-none focus:ring-2 focus:ring-green-500"
                value={years}
                onChange={(e) => setYears(Number(e.target.value))}
              />
              {years !== 0 && (years < 1 || years > 10) && (
                <p className="text-sm text-red-500 mt-1">
                  Duration must be between 1 to 10 years
                </p>
              )}
            </div>

            <button
              onClick={handleSimulate}
              disabled={!isValid}
              className={`w-full mt-2 px-5 py-2.5 rounded-lg font-medium transition ${
                isValid
                  ? "bg-green-600 text-white hover:bg-green-700"
                  : "bg-gray-300 text-gray-500 cursor-not-allowed"
              }`}
            >
              Simulate
            </button>
          </div>
        </div>

        {/* Simulation Result */}
        {simulating && (
          <p className="mt-6 text-blue-500 font-medium">
            Calculating simulation...
          </p>
        )}

        {simulateError && (
          <p className="mt-6 text-red-500 font-medium">{simulateError}</p>
        )}

        {data && (
          <div className="mt-8 bg-green-50 p-5 rounded-xl shadow-sm">
            <p className="font-semibold text-green-800 mb-3">
              With a monthly investment of Rp{" "}
              {monthlyInvestment.toLocaleString()}, your portfolio will grow to
              an estimated value of Rp {data.futureValue.toLocaleString()} in{" "}
              {years} years, based on the product rate of return.
            </p>
            <ul className="text-sm text-gray-800 space-y-1">
              <li>
                <span className="font-medium">Future Value:</span> Rp{" "}
                {data.futureValue.toLocaleString()}
              </li>
              <li>
                <span className="font-medium">Estimated Time to Achieve:</span>{" "}
                {data.monthsToAchieve} months
              </li>
              <li>
                <span className="font-medium">
                  Required Monthly Investment:
                </span>{" "}
                Rp {data.monthlyInvestmentNeeded.toLocaleString()}
              </li>
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProductDetail;
