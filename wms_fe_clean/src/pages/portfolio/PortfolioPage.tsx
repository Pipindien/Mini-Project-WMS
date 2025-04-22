import React, { useEffect, useState } from "react";
import { Goal } from "../../services/goal/type";
import { getGoals, archiveGoal } from "../../services/goal/api";
import { useNavigate } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";

const containerVariants = {
  initial: { opacity: 0 },
  animate: {
    opacity: 1,
    transition: { duration: 0.5, delayChildren: 0.2, staggerChildren: 0.1 },
  },
  exit: { opacity: 0, transition: { duration: 0.3 } },
};

const headerVariants = {
  initial: { y: -20, opacity: 0 },
  animate: {
    y: 0,
    opacity: 1,
    transition: { duration: 0.4, ease: "easeInOut" },
  },
};

const buttonVariants = {
  hover: { scale: 1.05 },
  tap: { scale: 0.95 },
};

const goalCardVariants = {
  initial: { y: 20, opacity: 0 },
  animate: {
    y: 0,
    opacity: 1,
    transition: { duration: 0.3, ease: "easeInOut" },
  },
  hover: { scale: 1.02, boxShadow: "0px 5px 15px rgba(0, 0, 0, 0.1)" },
  tap: { scale: 0.98 },
};

const modalVariants = {
  initial: { opacity: 0, scale: 0.9 },
  animate: {
    opacity: 1,
    scale: 1,
    transition: { duration: 0.3, ease: "easeInOut" },
  },
  exit: { opacity: 0, scale: 0.9, transition: { duration: 0.2 } },
};

const HomePortfolio: React.FC = () => {
  const [goals, setGoals] = useState<Goal[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [selectedGoalId, setSelectedGoalId] = useState<string | null>(null);
  const navigate = useNavigate();

  const fetchGoals = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      setError("No token found.");
      setLoading(false);
      return;
    }

    try {
      const data = await getGoals(token);
      setGoals(data);
    } catch (err) {
      setError("Failed to fetch financial goals.");
      console.error("Error fetching goals:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchGoals();
  }, []); // Fetch goals on component mount

  const handleEdit = (goalId: string) => {
    navigate(`/portfolio/edit/${goalId}`);
  };

  const handleRecommendation = (goalId: string) => {
    navigate(`/portfolio/recommendation/${goalId}`);
  };

  const handleDelete = async (goalId: string) => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("Token not found");
      return;
    }

    try {
      await archiveGoal(goalId, token);

      // ✅ Optimistic update
      setGoals((prevGoals) =>
        prevGoals.filter((goal) => goal.goalId !== Number(goalId))
      );
    } catch (err) {
      console.error("Error archiving goal:", err);
      alert("Failed deleting the goal.");

      // Optional: you can re-fetch to stay in sync
      fetchGoals();
    }
  };

  const handleShowModal = (goalId: string) => {
    setSelectedGoalId(goalId);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedGoalId(null);
  };

  if (loading)
    return <div className="text-center mt-20 text-blue-500">Loading...</div>;
  if (error)
    return <div className="text-center mt-20 text-red-500">{error}</div>;

  return (
    <motion.div
      className="container mx-auto px-4 py-8"
      variants={containerVariants}
      initial="initial"
      animate="animate"
      exit="exit"
    >
      <motion.div
        className="flex flex-col sm:flex-row justify-between items-center mb-6"
        variants={headerVariants}
      >
        <h1 className="text-2xl sm:text-3xl font-bold text-gray-800 mb-2 sm:mb-0">
          Portfolio Management
        </h1>
        <motion.button
          onClick={() => navigate("/portfolio/create")}
          className="bg-gradient-to-r from-green-400 to-blue-500 hover:from-green-500 hover:to-blue-600 text-white px-4 py-2 rounded-lg shadow-lg transform transition-transform hover:scale-105 duration-300 text-sm sm:text-base"
          variants={buttonVariants}
          whileHover="hover"
          whileTap="tap"
        >
          + Add Goal
        </motion.button>
      </motion.div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 sm:gap-8">
        <AnimatePresence>
          {goals.map((goal) => {
            const progress =
              goal.targetAmount === 0
                ? 0
                : Math.min(
                    100,
                    Math.round((goal.currentAmount / goal.targetAmount) * 100)
                  );

            const formattedDate = goal.targetDate
              ? new Date(goal.targetDate).toLocaleDateString("id-ID", {
                  day: "2-digit",
                  month: "short",
                  year: "numeric",
                })
              : "Invalid Date";

            return (
              <motion.div
                key={goal.goalId}
                onClick={() => navigate(`/portfolio/detail/${goal.goalId}`)}
                className="bg-white shadow-xl rounded-xl p-4 sm:p-6 border transform transition-transform hover:scale-105 hover:shadow-2xl duration-300 cursor-pointer"
                variants={goalCardVariants}
                whileHover="hover"
                whileTap="tap"
                exit={{
                  opacity: 0,
                  scale: 0.95,
                  transition: { duration: 0.2 },
                }}
              >
                <h2 className="text-xl sm:text-2xl font-semibold mb-1 sm:mb-2 text-gray-800">
                  {goal.goalName}
                </h2>
                <p className="text-xs sm:text-sm text-gray-600 mb-0.5 sm:mb-1">
                  Status: <span className="font-medium">{goal.status}</span>
                </p>
                <p className="text-xs sm:text-sm text-gray-600 mb-0.5 sm:mb-1">
                  Risk Tolerance:{" "}
                  <span className="font-medium text-blue-600 capitalize">
                    {goal.riskTolerance}
                  </span>
                </p>
                <p className="text-xs sm:text-sm text-gray-500 mb-1 sm:mb-2">
                  Target Date: {formattedDate}
                </p>

                <div className="mb-2 sm:mb-3">
                  <div className="flex justify-between text-xs sm:text-sm text-gray-600 mb-1">
                    <span>Rp {goal.currentAmount.toLocaleString()}</span>
                    <span>Rp {goal.targetAmount.toLocaleString()}</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-3 sm:h-4 overflow-hidden">
                    <div
                      className="bg-gradient-to-r from-green-400 to-blue-500 h-full transition-all duration-500 ease-in-out"
                      style={{ width: `${progress}%` }}
                    ></div>
                  </div>
                  <p className="text-right text-xxs sm:text-xs mt-1 text-gray-500">
                    {progress}% achieved
                  </p>
                </div>

                <p className="text-xs sm:text-sm text-gray-700 mb-2 sm:mb-4">
                  {/* {goal.insightMessage} //Insight message yang akan muncul di
                  Portfolio Management. Dikomen karena keliatan berantakan */}
                </p>

                <hr className="my-2 sm:my-4 border-t border-gray-200" />
                <div className="flex justify-end space-x-2 sm:space-x-4">
                  <motion.button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleRecommendation(goal.goalId?.toString());
                    }}
                    className="px-3 py-1 sm:px-5 sm:py-2 text-xxs sm:text-sm font-medium text-green-600 bg-green-100 hover:bg-green-200 rounded-lg shadow-md transition duration-200 transform hover:scale-105"
                    variants={buttonVariants}
                    whileHover="hover"
                    whileTap="tap"
                  >
                    Recommendation
                  </motion.button>
                  <motion.button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleEdit(goal.goalId?.toString());
                    }}
                    className="px-3 py-1 sm:px-5 sm:py-2 text-xxs sm:text-sm font-medium text-blue-600 bg-blue-100 hover:bg-blue-200 rounded-lg shadow-md transition duration-200 transform hover:scale-105"
                    variants={buttonVariants}
                    whileHover="hover"
                    whileTap="tap"
                  >
                    Edit
                  </motion.button>
                  <motion.button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleShowModal(goal.goalId?.toString());
                    }}
                    className="px-3 py-1 sm:px-5 sm:py-2 text-xxs sm:text-sm font-medium text-red-600 bg-red-100 hover:bg-red-200 rounded-lg shadow-md transition duration-200 transform hover:scale-105"
                    variants={buttonVariants}
                    whileHover="hover"
                    whileTap="tap"
                  >
                    Delete
                  </motion.button>
                </div>
              </motion.div>
            );
          })}
        </AnimatePresence>
      </div>

      <AnimatePresence>
        {showModal && (
          <motion.div
            className="fixed inset-0 flex justify-center items-center z-50 bg-black/50 backdrop-blur-sm transition-opacity duration-300 ease-in-out"
            variants={modalVariants}
            initial="initial"
            animate="animate"
            exit="exit"
          >
            <motion.div
              className="bg-white/20 backdrop-blur-md p-6 sm:p-8 rounded-lg shadow-2xl transform transition-all duration-300 ease-in-out max-w-sm w-full opacity-100"
              variants={modalVariants}
            >
              <h3 className="text-lg sm:text-xl font-semibold text-white mb-2 sm:mb-4 text-center">
                Confirm Deletion
              </h3>
              <p className="text-xs sm:text-sm text-gray-300 mb-4 sm:mb-6 text-center">
                Are you sure you want to delete this goal? This action cannot be
                undone.
              </p>
              <div className="flex justify-center space-x-4 sm:space-x-6">
                <motion.button
                  onClick={handleCloseModal}
                  className="px-4 py-2 sm:px-6 sm:py-3 text-xxs sm:text-sm font-medium text-gray-300 bg-black/30 hover:bg-black/40 rounded-lg shadow-md transition duration-200 transform hover:scale-105"
                  variants={buttonVariants}
                  whileHover="hover"
                  whileTap="tap"
                >
                  Cancel
                </motion.button>
                <motion.button
                  onClick={() => {
                    handleDelete(selectedGoalId!);
                    handleCloseModal();
                  }}
                  className="px-4 py-2 sm:px-6 sm:py-3 text-xxs sm:text-sm font-medium text-white bg-red-600 hover:bg-red-700 rounded-lg shadow-md transition duration-200 transform hover:scale-105"
                  variants={buttonVariants}
                  whileHover="hover"
                  whileTap="tap"
                >
                  Delete
                </motion.button>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
};

export default HomePortfolio;
