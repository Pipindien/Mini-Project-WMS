// src/pages/PaymentPage.tsx
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { updateTransaction } from "../../services/transaction/api"; // Buatkan fungsi ini
import {
  BuyTransactionRequest,
  BuyTransactionResponse,
} from "../../services/transaction/type";

const PaymentPage: React.FC = () => {
  const { trxNumber } = useParams();
  const [status, setStatus] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [responseData, setResponseData] =
    useState<BuyTransactionResponse | null>(null);

  const fetchPaymentStatus = async () => {
    setLoading(true);
    const token = localStorage.getItem("token");
    if (!token || !trxNumber) {
      setError("Token atau nomor transaksi tidak ditemukan.");
      setLoading(false);
      return;
    }

    const request: BuyTransactionRequest = {
      productName: "Nama Produk", // bisa diisi dummy, backend akan ambil dari db
      goalName: "Tujuan Keuangan", // dummy, backend akan overwrite
      amount: 0, // dummy juga
      notes: "Cek Status Pembayaran",
    };

    try {
      const response = await updateTransaction(trxNumber, request, token);
      setStatus(response.status);
      setResponseData(response);
      setError(null);
    } catch (err) {
      setError("Gagal mengupdate status pembayaran.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPaymentStatus(); // Auto-fetch saat halaman dibuka
  }, []);

  return (
    <div className="max-w-xl mx-auto mt-10 p-6 bg-white rounded-xl shadow-md text-center">
      <h2 className="text-2xl font-bold mb-4">Status Pembayaran</h2>

      {loading && <p>Loading...</p>}
      {error && <p className="text-red-600">{error}</p>}

      {status && (
        <div className="text-lg font-semibold">
          Status Transaksi:{" "}
          <span
            className={
              status === "success"
                ? "text-green-600"
                : status === "failed"
                ? "text-red-600"
                : "text-yellow-500"
            }
          >
            {status}
          </span>
        </div>
      )}

      {responseData && (
        <div className="mt-4 text-left">
          <p>
            <strong>Produk:</strong> {responseData.productId}
          </p>
          <p>
            <strong>Nominal:</strong> Rp {responseData.amount.toLocaleString()}
          </p>
          <p>
            <strong>Harga per Unit:</strong> Rp{" "}
            {responseData.productPrice.toLocaleString()}
          </p>
          <p>
            <strong>Lot:</strong> {responseData.lot}
          </p>
          <p>
            <strong>Goal ID:</strong> {responseData.goalId}
          </p>
          <p>
            <strong>Catatan:</strong> {responseData.notes}
          </p>
        </div>
      )}

      <button
        className="mt-6 bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition"
        onClick={fetchPaymentStatus}
      >
        Cek Status Pembayaran
      </button>
    </div>
  );
};

export default PaymentPage;
