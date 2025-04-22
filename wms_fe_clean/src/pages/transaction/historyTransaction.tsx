import React, { useMemo, useState } from "react";
import { useTransactions } from "../hooks/useTransaction/useTransaction";
import { motion, AnimatePresence, stagger } from "framer-motion";
import {
  ArrowUpIcon,
  ArrowDownIcon,
  CheckCircleIcon,
  CurrencyDollarIcon,
  ClockIcon,
  TagIcon,
} from "@heroicons/react/24/outline";

type SortOrder = "asc" | "desc";

const containerVariants = {
  initial: { opacity: 0 },
  animate: {
    opacity: 1,
    transition: { delayChildren: 0.2, staggerChildren: 0.1 },
  },
};

const headerVariants = {
  initial: { y: -20, opacity: 0 },
  animate: { y: 0, opacity: 1, transition: { duration: 0.5 } },
};

const tabButtonVariants = {
  initial: { opacity: 0, y: 10 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.3 } },
  hover: { scale: 1.05 },
  tap: { scale: 0.95 },
};

const tableContainerVariants = {
  initial: { opacity: 0 },
  animate: { opacity: 1, transition: { duration: 0.4 } },
};

const tableRowVariants = {
  initial: { opacity: 0, y: 15 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.3 } },
  exit: { opacity: 0, height: 0, padding: 0, transition: { duration: 0.2 } },
};

const sortButtonVariants = {
  hover: { backgroundColor: "#556ee6", color: "white", scale: 1.03 },
  tap: { scale: 0.97 },
};

const paginationVariants = {
  initial: { opacity: 0, y: 10 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.3 } },
};

const borderRadius = "0.375rem"; // Equivalent to rounded-md

const HistoryTransaction: React.FC = () => {
  const [activeTab, setActiveTab] = useState<"SUCCESS" | "SOLD">("SUCCESS");
  const [currentPage, setCurrentPage] = useState(1);
  const [sortOrder, setSortOrder] = useState<SortOrder>("desc");
  const [rowsPerPage, setRowsPerPage] = useState<number>(6);

  const {
    transactions: successTransactions,
    loading: loadingSuccess,
    error: errorSuccess,
  } = useTransactions("SUCCESS");

  const {
    transactions: soldTransactions,
    loading: loadingSold,
    error: errorSold,
  } = useTransactions("SOLD");

  const transactions =
    activeTab === "SUCCESS" ? successTransactions : soldTransactions;
  const loading = activeTab === "SUCCESS" ? loadingSuccess : loadingSold;
  const error = activeTab === "SUCCESS" ? errorSuccess : errorSold;

  const sortedTransactions = useMemo(() => {
    if (!transactions) return [];
    const sorted = [...transactions];
    sorted.sort((a, b) => {
      const dateA = new Date(a.createdDate).getTime();
      const dateB = new Date(b.createdDate).getTime();
      return sortOrder === "asc" ? dateA - dateB : dateB - dateA;
    });
    return sorted;
  }, [transactions, sortOrder]);

  const paginatedTransactions = useMemo(() => {
    const start = (currentPage - 1) * rowsPerPage;
    return sortedTransactions.slice(start, start + rowsPerPage);
  }, [sortedTransactions, currentPage, rowsPerPage]);

  const totalPages = Math.ceil((transactions?.length || 0) / rowsPerPage);

  const handleSortToggle = () => {
    setSortOrder((prev) => (prev === "asc" ? "desc" : "asc"));
  };

  const handleRowsPerPageChange = (
    event: React.ChangeEvent<HTMLSelectElement>
  ) => {
    setRowsPerPage(Number(event.target.value));
    setCurrentPage(1);
  };

  return (
    <motion.div
      className={`p-8 max-w-7xl mx-auto bg-white shadow-xl rounded-lg`}
      variants={containerVariants}
      initial="initial"
      animate="animate"
    >
      <motion.h2
        className="text-3xl font-bold text-indigo-700 mb-8"
        variants={headerVariants}
        initial="initial"
        animate="animate"
      >
        Transaction History
      </motion.h2>

      <motion.div className="flex space-x-4 mb-8" variants={containerVariants}>
        <motion.button
          onClick={() => {
            setActiveTab("SUCCESS");
            setCurrentPage(1);
          }}
          className={`px-5 py-3 rounded-md text-sm font-medium border focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-colors duration-200 ${
            activeTab === "SUCCESS"
              ? "bg-indigo-600 text-white border-indigo-600 shadow-md"
              : "bg-indigo-50 text-indigo-700 border-indigo-300 hover:bg-indigo-100"
          }`}
          style={{ borderRadius }}
          variants={tabButtonVariants}
          whileHover="hover"
          whileTap="tap"
        >
          Successful Transactions
        </motion.button>
        <motion.button
          onClick={() => {
            setActiveTab("SOLD");
            setCurrentPage(1);
          }}
          className={`px-5 py-3 rounded-md text-sm font-medium border focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-colors duration-200 ${
            activeTab === "SOLD"
              ? "bg-indigo-600 text-white border-indigo-600 shadow-md"
              : "bg-indigo-50 text-indigo-700 border-indigo-300 hover:bg-indigo-100"
          }`}
          style={{ borderRadius }}
          variants={tabButtonVariants}
          whileHover="hover"
          whileTap="tap"
        >
          Sold Transactions
        </motion.button>
      </motion.div>

      {loading ? (
        <motion.div
          className="text-center text-indigo-600 text-lg"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.3 }}
        >
          Loading...
        </motion.div>
      ) : error ? (
        <motion.div
          className="text-center text-red-500 text-lg"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.3 }}
        >
          {error}
        </motion.div>
      ) : (
        <motion.div
          className={`shadow-md overflow-hidden border border-gray-200 rounded-md`}
          style={{ borderRadius }}
          variants={tableContainerVariants}
          initial="initial"
          animate="animate"
        >
          {paginatedTransactions.length === 0 ? (
            <motion.p
              className="text-gray-500 text-center italic py-8"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1, transition: { duration: 0.3 } }}
            >
              No transactions available.
            </motion.p>
          ) : (
            <>
              <div className="px-4 py-3 bg-gray-50 flex justify-between items-center">
                <motion.button
                  onClick={handleSortToggle}
                  className="px-3 py-2 bg-indigo-100 text-indigo-700 text-sm rounded-md hover:bg-indigo-200 focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-colors duration-200 flex items-center"
                  style={{ borderRadius }}
                  variants={sortButtonVariants}
                  whileHover="hover"
                  whileTap="tap"
                >
                  Sort By:{" "}
                  {sortOrder === "asc" ? (
                    <ArrowUpIcon className="w-4 h-4 ml-1 text-indigo-500" />
                  ) : (
                    <ArrowDownIcon className="w-4 h-4 ml-1 text-indigo-500" />
                  )}
                  {sortOrder === "asc" ? "Oldest" : "Newest"}
                </motion.button>
                <div>
                  <label
                    htmlFor="rowsPerPage"
                    className="mr-2 text-sm text-gray-700"
                  >
                    Show:
                  </label>
                  <select
                    id="rowsPerPage"
                    className="border border-indigo-300 rounded-md py-1 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    value={rowsPerPage}
                    onChange={handleRowsPerPageChange}
                    style={{ borderRadius }}
                  >
                    <option value={5}>5</option>
                    <option value={10}>10</option>
                    <option value={20}>20</option>
                    <option value={50}>50</option>
                  </select>
                  <span className="ml-2 text-sm text-gray-700">Rows</span>
                </div>
              </div>
              <table className="min-w-full bg-white text-sm">
                <thead className="bg-indigo-500 text-white shadow-sm">
                  <tr>
                    <th className="px-6 py-3 text-left font-semibold tracking-wider">
                      <ClockIcon className="w-4 h-4 inline-block mr-1 -mt-0.5" />{" "}
                      <span className="ml-1">Date</span>
                    </th>
                    <th className="px-6 py-3 text-left font-semibold tracking-wider">
                      <CheckCircleIcon className="w-4 h-4 inline-block mr-1 -mt-0.5" />{" "}
                      <span className="ml-1">Status</span>
                    </th>
                    <th className="px-6 py-3 text-left font-semibold tracking-wider">
                      <CurrencyDollarIcon className="w-4 h-4 inline-block mr-1 -mt-0.5" />
                      <span className="ml-1">
                        {activeTab === "SUCCESS" ? "Investment" : "Profit"}
                      </span>
                    </th>
                    <th className="px-6 py-3 text-left font-semibold tracking-wider">
                      <TagIcon className="w-4 h-4 inline-block mr-1 -mt-0.5" />{" "}
                      <span className="ml-1">Product</span>
                    </th>
                    <th className="px-6 py-3 text-left font-semibold tracking-wider">
                      Price
                    </th>
                    <th className="px-6 py-3 text-left font-semibold tracking-wider">
                      Lot
                    </th>
                    <th className="px-6 py-3 text-left font-semibold tracking-wider">
                      Goal
                    </th>
                  </tr>
                </thead>
                <AnimatePresence>
                  <tbody className="divide-y divide-gray-100 text-gray-700">
                    {paginatedTransactions.map((trx, index) => (
                      <motion.tr
                        key={index}
                        variants={tableRowVariants}
                        initial="initial"
                        animate="animate"
                        exit="exit"
                        className="hover:bg-indigo-50 transition-colors duration-200"
                      >
                        <td className="px-6 py-4 whitespace-nowrap">
                          {new Date(trx.createdDate).toLocaleDateString()}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap capitalize">
                          <span
                            className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                              trx.status === "SUCCESS"
                                ? "bg-green-100 text-green-800"
                                : "bg-yellow-100 text-yellow-800"
                            }`}
                            style={{ borderRadius: "1rem" }} // Keep status pill rounded
                          >
                            {trx.status}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-indigo-600 font-semibold">
                          Rp {trx.amount.toLocaleString()}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          {trx.productName}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          Rp {trx.productPrice.toLocaleString()}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          {trx.lot}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-gray-800">
                          {trx.goalName}
                        </td>
                      </motion.tr>
                    ))}
                  </tbody>
                </AnimatePresence>
              </table>
            </>
          )}
        </motion.div>
      )}

      {totalPages > 1 && (
        <motion.div
          className="flex justify-center mt-8"
          variants={paginationVariants}
          initial="initial"
          animate="animate"
        >
          <button
            onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
            disabled={currentPage === 1}
            className="px-4 py-2 bg-indigo-100 text-indigo-700 rounded-md hover:bg-indigo-200 disabled:opacity-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-colors duration-200"
            style={{ borderRadius }}
            whileHover={{ scale: 1.03 }}
            whileTap={{ scale: 0.97 }}
          >
            Previous
          </button>
          <span className="mx-4 text-gray-600 font-medium">
            {currentPage} / {totalPages}
          </span>
          <button
            onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
            disabled={currentPage === totalPages}
            className="px-4 py-2 bg-indigo-100 text-indigo-700 rounded-md hover:bg-indigo-200 disabled:opacity-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-colors duration-200"
            style={{ borderRadius }}
            whileHover={{ scale: 1.03 }}
            whileTap={{ scale: 0.97 }}
          >
            Next
          </button>
        </motion.div>
      )}
    </motion.div>
  );
};

export default HistoryTransaction;
