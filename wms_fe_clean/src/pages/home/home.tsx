import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import useProducts from "../hooks/useProduct/useProduct";
import CardProduct from "../../component/card/cardProduct/cardProduct";
import { getPortfolioDashboard } from "../../services/goal/api";
import { PortfolioDashboardResponse } from "../../services/goal/type";
import { motion } from "framer-motion";

const summaryVariants = {
  initial: { opacity: 0, y: 20 },
  animate: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.6, ease: "easeInOut" },
  },
};

const summaryItemVariants = {
  initial: { opacity: 0, y: 10 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.4, delay: 0.1 } },
};

const categoryVariants = {
  initial: { opacity: 0, y: 15 },
  animate: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.5, delay: 0.2, staggerChildren: 0.1 },
  },
};

const categoryItemVariants = {
  initial: { opacity: 0, scale: 0.9 },
  animate: { opacity: 1, scale: 1, transition: { duration: 0.3 } },
  hover: { scale: 1.05, boxShadow: "0px 4px 8px rgba(0, 0, 0, 0.15)" },
};

const productListVariants = {
  initial: { opacity: 0 },
  animate: {
    opacity: 1,
    transition: { delayChildren: 0.3, staggerChildren: 0.15 },
  },
};

const productCardVariants = {
  initial: { opacity: 0, y: 20 },
  animate: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.4, ease: "easeInOut" },
  },
  hover: { scale: 1.03, boxShadow: "0px 6px 12px rgba(0, 0, 0, 0.1)" },
};

const loadingErrorVariants = {
  initial: { opacity: 0 },
  animate: { opacity: 1, transition: { duration: 0.5 } },
};

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
        setDashboardError("Failed to load portfolio summary.");
      } finally {
        setDashboardLoading(false);
      }
    };

    fetchDashboard();
  }, []);

  if (loading) {
    return (
      <motion.div
        className="text-center text-xl text-indigo-600 mt-20"
        variants={loadingErrorVariants}
        initial="initial"
        animate="animate"
      >
        Loading products...
      </motion.div>
    );
  }

  if (error) {
    return (
      <motion.div
        className="text-center text-xl text-red-500 mt-20"
        variants={loadingErrorVariants}
        initial="initial"
        animate="animate"
      >
        Error loading products: {error}
      </motion.div>
    );
  }

  return (
    <motion.div
      className="container mx-auto p-6"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.8, ease: "easeInOut" }}
    >
      {dashboardLoading ? (
        <motion.div
          className="text-center text-xl mt-8 text-indigo-600"
          variants={loadingErrorVariants}
          initial="initial"
          animate="animate"
        >
          Loading portfolio summary...
        </motion.div>
      ) : dashboardError ? (
        <motion.div
          className="text-center text-xl text-red-500 mt-8"
          variants={loadingErrorVariants}
          initial="initial"
          animate="animate"
        >
          {dashboardError}
        </motion.div>
      ) : dashboard ? (
        <motion.div
          className="bg-white shadow-md rounded-lg p-8 mb-12"
          variants={summaryVariants}
          initial="initial"
          animate="animate"
        >
          <h2 className="text-2xl font-semibold text-gray-800 mb-6">
            📊 Portfolio Summary
          </h2>
          <motion.div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 text-gray-700">
            <motion.div
              className="bg-indigo-50 p-4 rounded-md shadow-sm border-l-4 border-indigo-400"
              variants={summaryItemVariants}
            >
              <p className="font-semibold text-sm text-indigo-600 uppercase tracking-wider mb-1">
                Total Investment
              </p>
              <p className="text-xl font-bold text-indigo-700">
                Rp {dashboard.totalInvestment.toLocaleString()}
              </p>
            </motion.div>
            <motion.div
              className="bg-green-50 p-4 rounded-md shadow-sm border-l-4 border-green-400"
              variants={summaryItemVariants}
              style={{ transitionDelay: "0.2s" }}
            >
              <p className="font-semibold text-sm text-green-600 uppercase tracking-wider mb-1">
                Estimated Return
              </p>
              <p className="text-xl font-bold text-green-700">
                Rp {dashboard.estimatedReturn.toLocaleString()}
              </p>
            </motion.div>
            <motion.div
              className="bg-teal-50 p-4 rounded-md shadow-sm border-l-4 border-teal-400"
              variants={summaryItemVariants}
              style={{ transitionDelay: "0.4s" }}
            >
              <p className="font-semibold text-sm text-teal-600 uppercase tracking-wider mb-1">
                Total Profit
              </p>
              <p className="text-xl font-bold text-teal-700">
                Rp {dashboard.totalProfit.toLocaleString()}
              </p>
            </motion.div>
            <motion.div
              className="bg-purple-50 p-4 rounded-md shadow-sm border-l-4 border-purple-400"
              variants={summaryItemVariants}
              style={{ transitionDelay: "0.6s" }}
            >
              <p className="font-semibold text-sm text-purple-600 uppercase tracking-wider mb-1">
                Return Rate
              </p>
              <p className="text-xl font-bold text-purple-700">
                {dashboard.returnPercentage.toFixed(2)}%
              </p>
            </motion.div>
          </motion.div>

          {/* Category Allocation */}
          {dashboard.categoryAllocation &&
            Object.keys(dashboard.categoryAllocation).length > 0 && (
              <motion.div
                className="mt-8"
                variants={categoryVariants}
                initial="initial"
                animate="animate"
              >
                <h3 className="text-lg font-semibold text-gray-800 mb-4">
                  Investment by Category
                </h3>
                <motion.div className="flex flex-wrap gap-3">
                  {Object.entries(dashboard.categoryAllocation).map(
                    ([category, percentage]) => (
                      <motion.div
                        key={category}
                        className="bg-gray-100 text-gray-800 px-3 py-2 rounded-full shadow-sm text-sm font-medium"
                        variants={categoryItemVariants}
                        whileHover="hover"
                      >
                        {category}: {(Number(percentage) || 0).toFixed(2)}%
                      </motion.div>
                    )
                  )}
                </motion.div>
              </motion.div>
            )}
        </motion.div>
      ) : null}

      {/* Product List */}
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
        variants={productListVariants}
        initial="initial"
        animate="animate"
      >
        {products.map((product) => (
          <Link
            key={product.productId}
            to={`/product/${product.productId}`}
            className="block"
          >
            <motion.div variants={productCardVariants} whileHover="hover">
              <CardProduct product={product} />
            </motion.div>
          </Link>
        ))}
      </motion.div>
    </motion.div>
  );
};

export default Home;
