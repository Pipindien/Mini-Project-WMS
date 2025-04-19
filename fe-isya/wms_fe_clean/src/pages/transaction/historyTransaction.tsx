import React, { useState } from "react";
import { useTransactions } from "../hooks/useTransaction/useTransaction";

const HistoryTransaction: React.FC = () => {
  const [activeTab, setActiveTab] = useState<"SUCCESS" | "SOLD">("SUCCESS");

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

  const renderTable = (data: any[]) => (
    <div className="mt-6">
      {data.length === 0 ? (
        <p className="text-gray-500 text-center italic">Tidak ada transaksi.</p>
      ) : (
        <div className="overflow-x-auto rounded-xl border border-gray-200 shadow-sm">
          <table className="min-w-full bg-white text-sm">
            <thead className="bg-indigo-600 text-white">
              <tr>
                <th className="px-6 py-4 text-left font-semibold">Tanggal</th>
                <th className="px-6 py-4 text-left font-semibold">Status</th>
                <th className="px-6 py-4 text-left font-semibold">Amount</th>
                <th className="px-6 py-4 text-left font-semibold">Product</th>
                <th className="px-6 py-4 text-left font-semibold">Price</th>
                <th className="px-6 py-4 text-left font-semibold">Lot</th>
                <th className="px-6 py-4 text-left font-semibold">Goal</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 text-gray-700">
              {data.map((trx, index) => (
                <tr key={index} className="hover:bg-gray-50">
                  <td className="px-6 py-4">
                    {new Date(trx.createdDate).toLocaleDateString()}
                  </td>
                  <td className="px-6 py-4 capitalize">{trx.status}</td>
                  <td className="px-6 py-4">
                    Rp {trx.amount.toLocaleString()}
                  </td>
                  <td className="px-6 py-4">{trx.productName}</td>
                  <td className="px-6 py-4">
                    Rp {trx.productPrice.toLocaleString()}
                  </td>
                  <td className="px-6 py-4">{trx.lot}</td>
                  <td className="px-6 py-4">{trx.goalName}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );

  return (
    <div className="p-8 max-w-7xl mx-auto">
      <h2 className="text-3xl font-bold text-indigo-700 mb-6">
        Riwayat Transaksi
      </h2>

      {/* Toggle Buttons */}
      <div className="flex space-x-4 mb-6">
        <button
          onClick={() => setActiveTab("SUCCESS")}
          className={`px-4 py-2 rounded-lg text-sm font-medium border ${
            activeTab === "SUCCESS"
              ? "bg-indigo-600 text-white"
              : "bg-white text-indigo-600 border-indigo-600"
          } transition`}
        >
          Transaksi Berhasil
        </button>
        <button
          onClick={() => setActiveTab("SOLD")}
          className={`px-4 py-2 rounded-lg text-sm font-medium border ${
            activeTab === "SOLD"
              ? "bg-indigo-600 text-white"
              : "bg-white text-indigo-600 border-indigo-600"
          } transition`}
        >
          Transaksi Terjual
        </button>
      </div>

      {/* Tabel */}
      {loading ? (
        <div className="text-center text-indigo-600 text-lg">Loading...</div>
      ) : error ? (
        <div className="text-center text-red-500 text-lg">{error}</div>
      ) : (
        renderTable(transactions)
      )}
    </div>
  );
};

export default HistoryTransaction;
