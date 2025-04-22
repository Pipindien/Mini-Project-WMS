import React from "react";
import { motion } from "framer-motion";
import {
  FaChartLine,
  FaPiggyBank,
  FaLightbulb,
  FaShieldAlt,
  FaHeart,
} from "react-icons/fa";

const fadeIn = {
  initial: { opacity: 0 },
  animate: { opacity: 1, transition: { duration: 0.8, delay: 0.2 } },
};

const slideUp = {
  initial: { y: 50, opacity: 0 },
  animate: { y: 0, opacity: 1, transition: { duration: 0.7, ease: "easeOut" } },
};

const FeatureItem: React.FC<{ icon: React.ReactNode; text: string }> = ({
  icon,
  text,
}) => (
  <motion.li
    className="flex items-center space-x-4 bg-white/10 backdrop-blur-md rounded-lg p-4 shadow-md"
    variants={slideUp}
  >
    <span className="text-2xl text-indigo-400">{icon}</span>
    <span className="text-gray-300">{text}</span>
  </motion.li>
);

const Information: React.FC = () => {
  return (
    <motion.div
      className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-black text-white py-16"
      variants={fadeIn}
      initial="initial"
      animate="animate"
    >
      <div className="container mx-auto px-6 md:px-12 lg:px-24">
        <motion.h1
          className="text-5xl font-extrabold text-indigo-400 mb-8 text-center drop-shadow-lg"
          variants={slideUp}
        >
          Unlock Your Financial Potential with{" "}
          <span className="text-purple-300">WealthScape</span>
        </motion.h1>
        <motion.p
          className="text-xl text-gray-300 leading-relaxed mb-10 text-center"
          variants={slideUp}
          transition={{ delay: 0.4 }}
        >
          A revolutionary platform designed to simplify wealth management and
          empower your financial future.
        </motion.p>

        <motion.section
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12"
          variants={slideUp}
          transition={{ delay: 0.6, staggerChildren: 0.2 }}
        >
          <div className="bg-white/10 backdrop-blur-md rounded-lg p-6 shadow-md hover:scale-105 transition-transform duration-300">
            <FaChartLine className="text-3xl text-teal-400 mb-4" />
            <h3 className="text-xl font-semibold text-gray-200 mb-2">
              Real-Time Insights
            </h3>
            <p className="text-gray-400">
              Monitor your portfolio performance with live data and make timely
              decisions.
            </p>
          </div>
          <div className="bg-white/10 backdrop-blur-md rounded-lg p-6 shadow-md hover:scale-105 transition-transform duration-300">
            <FaPiggyBank className="text-3xl text-yellow-400 mb-4" />
            <h3 className="text-xl font-semibold text-gray-200 mb-2">
              Goal-Based Planning
            </h3>
            <p className="text-gray-400">
              Set your financial goals and track your progress visually and
              effectively.
            </p>
          </div>
          <div className="bg-white/10 backdrop-blur-md rounded-lg p-6 shadow-md hover:scale-105 transition-transform duration-300">
            <FaLightbulb className="text-3xl text-orange-400 mb-4" />
            <h3 className="text-xl font-semibold text-gray-200 mb-2">
              Smart Recommendations
            </h3>
            <p className="text-gray-400">
              Receive personalized investment suggestions based on your risk
              profile.
            </p>
          </div>
          <div className="bg-white/10 backdrop-blur-md rounded-lg p-6 shadow-md hover:scale-105 transition-transform duration-300">
            <FaShieldAlt className="text-3xl text-blue-400 mb-4" />
            <h3 className="text-xl font-semibold text-gray-200 mb-2">
              Secure & Reliable
            </h3>
            <p className="text-gray-400">
              Your financial data is protected with advanced security measures.
            </p>
          </div>
          <div className="bg-white/10 backdrop-blur-md rounded-lg p-6 shadow-md hover:scale-105 transition-transform duration-300 md:col-span-2 lg:col-span-1">
            <FaHeart className="text-3xl text-red-400 mb-4" />
            <h3 className="text-xl font-semibold text-gray-200 mb-2">
              User-Friendly Interface
            </h3>
            <p className="text-gray-400">
              Enjoy a seamless and intuitive experience across all your devices.
            </p>
          </div>
          {/* You can add more visually appealing feature blocks here */}
        </motion.section>

        <motion.section
          className="bg-white/10 backdrop-blur-md rounded-lg p-8 shadow-md mb-12"
          variants={slideUp}
          transition={{ delay: 0.8 }}
        >
          <h2 className="text-3xl font-semibold text-indigo-300 mb-6">
            Our Core Values
          </h2>
          <ul className="grid grid-cols-1 md:grid-cols-2 gap-6 text-gray-300">
            <li className="flex items-center space-x-3">
              <span className="text-xl text-green-400">✅</span>
              <span>
                Empowerment: Giving you the tools to control your financial
                destiny.
              </span>
            </li>
            <li className="flex items-center space-x-3">
              <span className="text-xl text-blue-400">💡</span>
              <span>
                Innovation: Continuously evolving to provide cutting-edge
                solutions.
              </span>
            </li>
            <li className="flex items-center space-x-3">
              <span className="text-xl text-yellow-400">🤝</span>
              <span>
                Trust: Building a secure and transparent platform you can rely
                on.
              </span>
            </li>
            <li className="flex items-center space-x-3">
              <span className="text-xl text-purple-400">🌱</span>
              <span>
                Growth: Supporting your journey towards long-term financial
                success.
              </span>
            </li>
          </ul>
        </motion.section>

        <motion.div
          className="text-center text-gray-400 mt-12"
          variants={slideUp}
          transition={{ delay: 1.0 }}
        >
          <h2 className="text-xl font-semibold text-purple-300 mb-2">
            Made with ❤️ by
          </h2>
          <p className="text-lg">Pipin, Isyaroh, Hafidz</p>
        </motion.div>
      </div>
    </motion.div>
  );
};

export default Information;
