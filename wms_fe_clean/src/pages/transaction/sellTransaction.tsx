import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { sellProduct } from "../../services/transaction/api";

export interface SellTransactionRequest {
  productName: string;
  lot: number;
  goalId: number;
}

const SellTransaction: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { productDetail, goalId } = location.state || {};

  const [lot, setLot] = useState<number>(1);

  const handleSell = async () => {
    if (!productDetail || goalId === undefined) {
      alert("Data tidak lengkap.");
      return;
    }

    const token = localStorage.getItem("token");
    if (!token) {
      alert("Token tidak ditemukan. Silakan login kembali.");
      return;
    }

    if (lot < 1 || isNaN(lot)) {
      alert("Jumlah lot tidak valid.");
      return;
    }

    const requestPayload: SellTransactionRequest = {
      productName: productDetail.productName,
      lot: lot,
      goalId: Number(goalId),
    };

    try {
      console.log("Mengirim request payload:", requestPayload);
      await sellProduct(requestPayload, token);
      alert("Transaksi penjualan berhasil!");
      navigate("/portfolio");
    } catch (err: any) {
      console.error("Error saat melakukan penjualan:", err);

      const errorMessage =
        err.response?.data?.message ||
        err.message ||
        "Gagal melakukan penjualan.";

      alert(errorMessage);
    }
  };

  const incrementLot = () => setLot((prev) => prev + 1);
  const decrementLot = () => setLot((prev) => (prev > 1 ? prev - 1 : 1));

  if (!productDetail)
    return (
      <div className="text-center mt-10 text-red-500">
        Data produk tidak ditemukan.
      </div>
    );

  const formatCurrency = (value: number) =>
    value?.toLocaleString("id-ID", { style: "currency", currency: "IDR" });

  return (
    <div className="max-w-md mx-auto p-6 bg-white shadow-md rounded-2xl mt-10 border border-gray-200">
      <h1 className="text-2xl font-semibold text-indigo-700 mb-6">
        Sell Stock
      </h1>

      <div className="mb-5 space-y-1">
        <p className="text-xl font-semibold text-gray-800">
          {productDetail.productName}
        </p>
        <p className="text-sm text-gray-500">
          Category:{" "}
          <span className="font-medium">{productDetail.productCategory}</span>
        </p>
        <p className="text-sm text-gray-500">
          Current Price:{" "}
          <span className="font-medium">
            {formatCurrency(productDetail.buyPrice)}
          </span>
        </p>
        {goalId && (
          <p className="text-sm text-gray-500">
            Goal: <span className="font-medium">{goalId}</span>
          </p>
        )}
      </div>

      <div className="mb-6">
        <label className="block mb-2 text-sm font-medium text-gray-700">
          Number of Lots
        </label>
        <div className="flex items-center border border-gray-300 rounded-xl overflow-hidden w-44 bg-gray-50">
          <button
            onClick={decrementLot}
            className="px-4 py-2 bg-gray-100 text-gray-700 hover:bg-gray-200 transition"
          >
            −
          </button>
          <input
            type="number"
            value={lot}
            onChange={(e) => {
              const value = parseInt(e.target.value);
              setLot(Number.isNaN(value) ? 1 : value);
            }}
            min={1}
            className="w-full text-center text-sm py-2 bg-transparent outline-none"
          />
          <button
            onClick={incrementLot}
            className="px-4 py-2 bg-gray-100 text-gray-700 hover:bg-gray-200 transition"
          >
            +
          </button>
        </div>
      </div>

      <button
        onClick={handleSell}
        className="w-full bg-red-600 hover:bg-red-700 text-white font-medium py-2.5 rounded-xl transition duration-200"
      >
        Confirm Sell
      </button>
    </div>
  );
};

export default SellTransaction;
