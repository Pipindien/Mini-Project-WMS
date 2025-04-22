import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import useProducts from "../hooks/useProduct/useProduct";
import CardProduct from "../../component/card/cardProduct/cardProduct";
import { getPortfolioDashboard } from "../../services/goal/api";
import { PortfolioDashboardResponse } from "../../services/goal/type";
import { motion } from "framer-motion";

const Home: React.FC = () => {
  const {
    products,
    loading: productsLoading,
    error: productsError,
  } = useProducts();
  const [dashboard, setDashboard] = useState<PortfolioDashboardResponse | null>(
    null
  );
  const [dashboardLoading, setDashboardLoading] = useState(true);
  const [dashboardError, setDashboardError] = useState<string | null>(null);

  useEffect(() => {
    const fetchDashboard = async () => {
      setDashboardLoading(true);
      setDashboardError(null);
      try {
        const token = localStorage.getItem("token") || "";
        const data = await getPortfolioDashboard(token);
        setDashboard(data);
      } catch (err: any) {
        console.error("Error fetching dashboard:", err);
        setDashboardError("Failed to load portfolio summary.");
        setDashboard(null);
      } finally {
        setDashboardLoading(false);
      }
    };

    fetchDashboard();
  }, []);

  return (
    <motion.div
      className="container mx-auto p-6"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.8, ease: "easeInOut" }}
    >
      {/* Simplified Portfolio Summary Section - No Animations */}
      <div className="bg-white shadow-md rounded-lg p-8 mb-12">
        <h2 className="text-2xl font-semibold text-gray-800 mb-6">
          📊 Portfolio Summary
        </h2>
        {dashboardLoading ? (
          <div className="text-center text-lg text-indigo-600">
            Loading portfolio summary...
          </div>
        ) : dashboardError ? (
          <div className="text-center text-lg text-red-500">
            {dashboardError}
          </div>
        ) : dashboard ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 text-gray-700">
            <div className="bg-indigo-50 p-4 rounded-md shadow-sm border-l-4 border-indigo-400">
              <p className="font-semibold text-sm text-indigo-600 uppercase tracking-wider mb-1">
                Total Investment
              </p>
              <p className="text-xl font-bold text-indigo-700">
                Rp {dashboard.totalInvestment?.toLocaleString() || "0"}
              </p>
            </div>
            <div className="bg-green-50 p-4 rounded-md shadow-sm border-l-4 border-green-400">
              <p className="font-semibold text-sm text-green-600 uppercase tracking-wider mb-1">
                Estimated Return
              </p>
              <p className="text-xl font-bold text-green-700">
                Rp {dashboard.estimatedReturn?.toLocaleString() || "0"}
              </p>
            </div>
            <div className="bg-teal-50 p-4 rounded-md shadow-sm border-l-4 border-teal-400">
              <p className="font-semibold text-sm text-teal-600 uppercase tracking-wider mb-1">
                Total Profit
              </p>
              <p className="text-xl font-bold text-teal-700">
                Rp {dashboard.totalProfit?.toLocaleString() || "0"}
              </p>
            </div>
            <div className="bg-purple-50 p-4 rounded-md shadow-sm border-l-4 border-purple-400">
              <p className="font-semibold text-sm text-purple-600 uppercase tracking-wider mb-1">
                Return Rate
              </p>
              <p className="text-xl font-bold text-purple-700">
                {dashboard.returnPercentage?.toFixed(2) || "0.00"}%
              </p>
            </div>
          </div>
        ) : (
          <div className="text-center text-gray-500 italic">
            No portfolio data available.
          </div>
        )}

        {/* Category Allocation - No Animations */}
        {dashboard &&
          dashboard.categoryAllocation &&
          Object.keys(dashboard.categoryAllocation).length > 0 && (
            <div className="mt-8">
              <h3 className="text-lg font-semibold text-gray-800 mb-4">
                Investment by Category
              </h3>
              <div className="flex flex-wrap gap-3">
                {Object.entries(dashboard.categoryAllocation).map(
                  ([category, percentage]) => (
                    <div
                      key={category}
                      className="bg-gray-100 text-gray-800 px-3 py-2 rounded-full shadow-sm text-sm font-medium hover:scale-105 transition-transform duration-200"
                    >
                      {category}: {(Number(percentage) || 0).toFixed(2)}%
                    </div>
                  )
                )}
              </div>
            </div>
          )}
      </div>

      {/* Product List - Keeping Animations */}
      <motion.h1
        className="text-3xl font-semibold text-center mb-10 text-gray-800"
        initial={{ opacity: 0, y: -20 }}
        animate={{
          opacity: 1,
          y: 0,
          transition: { duration: 0.6, delay: 0.2 },
        }}
      >
        Explore Our Products
      </motion.h1>
      <motion.div
        className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8"
        variants={{
          initial: { opacity: 0 },
          animate: {
            opacity: 1,
            transition: { delayChildren: 0.3, staggerChildren: 0.15 },
          },
        }}
        initial="initial"
        animate="animate"
      >
        {productsLoading ? (
          <motion.div
            className="col-span-full text-center text-lg text-indigo-600"
            variants={{
              initial: { opacity: 0 },
              animate: { opacity: 1, transition: { duration: 0.5 } },
            }}
            initial="initial"
            animate="animate"
          >
            Loading products...
          </motion.div>
        ) : productsError ? (
          <motion.div
            className="col-span-full text-center text-lg text-red-500"
            variants={{
              initial: { opacity: 0 },
              animate: { opacity: 1, transition: { duration: 0.5 } },
            }}
            initial="initial"
            animate="animate"
          >
            Error loading products: {productsError}
          </motion.div>
        ) : (
          products.map((product) => (
            <Link
              key={product.productId}
              to={`/product/${product.productId}`}
              className="block"
            >
              <motion.div
                variants={{
                  initial: { opacity: 0, y: 20 },
                  animate: {
                    opacity: 1,
                    y: 0,
                    transition: { duration: 0.4, ease: "easeInOut" },
                  },
                  hover: {
                    scale: 1.03,
                    boxShadow: "0px 6px 12px rgba(0, 0, 0, 0.1)",
                  },
                }}
                whileHover="hover"
              >
                <CardProduct product={product} />
              </motion.div>
            </Link>
          ))
        )}
      </motion.div>
    </motion.div>
  );
};

export default Home;
