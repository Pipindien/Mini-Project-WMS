import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { sellProduct } from "../../services/transaction/api";
import { FaSpinner } from "react-icons/fa";

export interface SellTransactionRequest {
  productName: string;
  lot: number;
  goalId: number;
}

const SellTransaction: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  let state = location.state || {};
  const storedProduct = localStorage.getItem("productDetail");
  const storedGoalId = localStorage.getItem("goalId");

  const productDetail =
    state.productDetail || (storedProduct ? JSON.parse(storedProduct) : null);
  const goalId =
    state.goalId || (storedGoalId ? Number(storedGoalId) : undefined);

  const [lot, setLot] = useState<number>(1);
  const [showModal, setShowModal] = useState<boolean>(false);
  const [isSelling, setIsSelling] = useState<boolean>(false);

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
      setIsSelling(true);
      await sellProduct(requestPayload, token);
      alert("Transaksi penjualan berhasil!");
      navigate("/portfolio");
    } catch (err: any) {
      const errorMessage =
        err.response?.data?.message ||
        err.message ||
        "Gagal melakukan penjualan.";
      alert(errorMessage);
    } finally {
      setIsSelling(false);
    }
  };

  const incrementLot = () => setLot((prev) => prev + 1);
  const decrementLot = () => setLot((prev) => (prev > 1 ? prev - 1 : 1));

  const formatCurrency = (value: number) =>
    value?.toLocaleString("id-ID", { style: "currency", currency: "IDR" });

  const openModal = () => setShowModal(true);
  const closeModal = () => setShowModal(false);

  if (!productDetail)
    return (
      <div className="text-center mt-10 text-red-500">
        Data produk tidak ditemukan.
      </div>
    );

  return (
    <div className="max-w-2xl mx-auto p-8 bg-white shadow-lg rounded-3xl mt-12 border border-gray-200 transition-all duration-300 ease-in-out">
      <h1 className="text-3xl font-bold text-indigo-700 mb-8 text-center">
        Sell Stock
      </h1>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Left Panel: Product Info */}
        <div className="space-y-3">
          <h2 className="text-xl font-semibold text-gray-800">
            {productDetail.productName}
          </h2>
          <p className="text-sm text-gray-600">
            Category:{" "}
            <span className="font-medium">{productDetail.productCategory}</span>
          </p>
          <p className="text-sm text-gray-600">
            Current Price:{" "}
            <span className="font-medium">
              {formatCurrency(productDetail.buyPrice)}
            </span>
          </p>
          {goalId && (
            <p className="text-sm text-gray-600">
              Goal ID: <span className="font-medium">{goalId}</span>
            </p>
          )}
        </div>

        {/* Right Panel: Input and Actions */}
        <div className="space-y-6">
          <div>
            <label className="block mb-2 text-sm font-medium text-gray-700">
              Number of Lots
            </label>
            <div className="flex items-center border border-gray-300 rounded-xl overflow-hidden w-full bg-gray-50">
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
            onClick={openModal}
            className="w-full bg-red-600 hover:bg-red-700 text-white font-semibold py-3 rounded-xl transition duration-200"
          >
            Confirm Sell
          </button>
        </div>
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 flex justify-center items-center z-50 bg-black/50 backdrop-blur-sm">
          <div className="bg-white p-6 rounded-lg shadow-xl max-w-sm w-full">
            <h3 className="text-lg font-semibold text-center text-gray-800 mb-4">
              Confirm Sale
            </h3>
            <p className="text-sm text-gray-600 mb-4 text-center">
              Are you sure you want to sell{" "}
              <span className="font-semibold">{productDetail.productName}</span>
              ?
              <br />
              Number of Lots:{" "}
              <span className="font-semibold text-red-600">{lot}</span>
            </p>
            <div className="flex justify-center gap-4">
              <button
                onClick={closeModal}
                className="px-4 py-2 text-sm text-gray-700 bg-gray-200 rounded-lg"
              >
                Cancel
              </button>
              <button
                onClick={() => {
                  handleSell();
                  closeModal();
                }}
                className="px-4 py-2 text-sm text-white bg-red-600 hover:bg-red-700 rounded-lg"
              >
                Confirm
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Spinner */}
      {isSelling && (
        <div className="fixed inset-0 flex justify-center items-center z-50 bg-black/30 backdrop-blur-sm">
          <FaSpinner className="animate-spin text-white text-4xl" />
        </div>
      )}
    </div>
  );
};

export default SellTransaction;
