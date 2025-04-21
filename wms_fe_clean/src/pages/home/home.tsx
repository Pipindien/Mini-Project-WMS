import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import useProducts from "../hooks/useProduct/useProduct";
import CardProduct from "../../component/card/cardProduct/cardProduct";
import { getPortfolioDashboard } from "../../services/goal/api";
import { PortfolioDashboardResponse } from "../../services/goal/type";

const Home: React.FC = () => {
  const { products, loading, error } = useProducts();
  const [dashboard, setDashboard] = useState<PortfolioDashboardResponse | null>(
    null
  );
  const [dashboardLoading, setDashboardLoading] = useState(true);
  const [dashboardError, setDashboardError] = useState<string | null>(null);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const token = localStorage.getItem("token") || "";
        const data = await getPortfolioDashboard(token);
        setDashboard(data);
      } catch (err) {
        setDashboardError("Failed to load dashboard");
      } finally {
        setDashboardLoading(false);
      }
    };

    fetchDashboard();
  }, []);

  if (loading) {
    return (
      <div className="text-center text-xl text-red-500 mt-20">Loading...</div>
    );
  }

  if (error) {
    return (
      <div className="text-center text-xl text-red-500 mt-20">{error}</div>
    );
  }

  return (
    <>
      <div className="container mx-auto p-4">
        {dashboardLoading ? (
          <div className="text-center text-xl mt-4">Loading dashboard...</div>
        ) : dashboardError ? (
          <div className="text-center text-xl text-red-500 mt-4">
            {dashboardError}
          </div>
        ) : dashboard ? (
          <div className="bg-white shadow p-6 rounded mb-10">
            <h2 className="text-2xl font-bold mb-4 text-gray-800">
              📊 Portfolio Summary
            </h2>
            <div className="text-gray-700 space-y-1 mb-4">
              <p>
                Total Investment: Rp{" "}
                {(dashboard?.totalInvestment ?? 0).toLocaleString()}
              </p>
              <p>
                Estimated Return:{" "}
                <strong>
                  Rp {(dashboard?.estimatedReturn ?? 0).toLocaleString()}
                </strong>
              </p>
              <p>
                Profit:{" "}
                <strong>
                  Rp {(dashboard?.totalProfit ?? 0).toLocaleString()}
                </strong>
              </p>
              <p>
                Return:{" "}
                <strong>
                  {(dashboard?.returnPercentage ?? 0).toFixed(2)}%
                </strong>
              </p>
            </div>

            {/* Category Allocation */}
            <div className="mt-6">
              <h3 className="text-lg font-semibold text-gray-800 mb-2">
                📁 Category Allocation
              </h3>
              <div className="flex flex-wrap gap-4">
                {dashboard.categoryAllocation &&
                  Object.entries(dashboard.categoryAllocation).map(
                    ([category, percentage]) => (
                      <div
                        key={category}
                        className="bg-blue-100 text-blue-800 px-4 py-2 rounded shadow-sm text-sm font-medium"
                      >
                        {category}: {percentage.toFixed(2)}%
                      </div>
                    )
                  )}
              </div>
            </div>
          </div>
        ) : null}

        {/* Product List */}
        <h1 className="text-3xl font-semibold text-center mb-8">
          🛒 Product List
        </h1>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {products.map((product) => (
            <Link key={product.productId} to={`/product/${product.productId}`}>
              <CardProduct product={product} />
            </Link>
          ))}
        </div>
      </div>
    </>
  );
};

export default Home;
