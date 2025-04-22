import React, { useEffect, useState, useRef } from "react";
import { Link } from "react-router-dom";
import useProducts from "../hooks/useProduct/useProduct";
import CardProduct from "../../component/card/cardProduct/cardProduct";
import { getPortfolioDashboard } from "../../services/goal/api";
import { PortfolioDashboardResponse } from "../../services/goal/type";
import { motion } from "framer-motion";

const Home: React.FC = () => {
  const [greeting, setGreeting] = useState("");
  const [currentTime, setCurrentTime] = useState("");
  const productListRef = useRef<HTMLHeadingElement>(null);
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

  useEffect(() => {
    const now = new Date();
    const hour = now.getHours();

    if (hour >= 5 && hour < 12) {
      setGreeting("Good morning");
    } else if (hour >= 12 && hour < 18) {
      setGreeting("Good afternoon");
    } else {
      setGreeting("Good evening");
    }

    const options: Intl.DateTimeFormatOptions = {
      hour: "numeric",
      minute: "numeric",
      second: "numeric",
      timeZone: "Asia/Jakarta",
    };
    setCurrentTime(new Intl.DateTimeFormat("en-US", options).format(now));
  }, []);

  const scrollToProducts = () => {
    productListRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  return (
    <motion.div
      className="container mx-auto p-6"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.8, ease: "easeInOut" }}
    >
      {/* Eye-Catching Welcome Banner */}
      <motion.div
        className="bg-gradient-to-br from-indigo-400 to-purple-500 shadow-xl rounded-xl p-12 mb-10 text-white"
        initial={{ opacity: 0, y: -30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.7, ease: "easeOut" }}
      >
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-4xl font-bold tracking-tight mb-2">
              👋 {greeting}!
            </h1>
            <p className="text-lg opacity-80">
              It's <span className="font-semibold">{currentTime} WIB</span> here
              in Indonesia. Ready to dive back in?
            </p>
          </div>
          {/* You could add a subtle icon here if you have any */}
          {/* <svg className="w-12 h-12 text-indigo-200 opacity-70" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.801 5s-3.03 5.476-3.03 5.476M12 6.253V19.5m0-13C13.168 5.477 14.754 5 16.199 5s3.03 5.476 3.03 5.476M12 6.253V6.253" />
          </svg> */}
        </div>
        <p className="text-md opacity-90 mb-4">
          Take a look at your portfolio summary below for a quick update on your
          investments. Explore new product opportunities further down the page.
        </p>
        <div className="flex space-x-4">
          <Link
            to="/portfolio"
            className="inline-block bg-indigo-500 hover:bg-indigo-600 text-white font-semibold py-3 px-6 rounded-md transition duration-300"
          >
            View Full Portfolio
          </Link>
          <button
            onClick={scrollToProducts}
            className="inline-block bg-purple-400 hover:bg-purple-500 text-white font-semibold py-3 px-6 rounded-md transition duration-300 cursor-pointer"
          >
            Explore All Products
          </button>
        </div>
      </motion.div>

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
        ref={productListRef} // Add the ref here
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
