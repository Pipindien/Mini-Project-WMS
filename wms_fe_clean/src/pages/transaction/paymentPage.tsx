import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  getTransaction,
  updateTransaction,
} from "../../services/transaction/api";
import { BuyTransactionResponse } from "../../services/transaction/type";

const PaymentPage: React.FC = () => {
  const { trxNumber } = useParams();
  const navigate = useNavigate();

  const [status, setStatus] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [responseData, setResponseData] =
    useState<BuyTransactionResponse | null>(null);
  const [processing, setProcessing] = useState<boolean>(false);

  const fetchPaymentStatus = async () => {
    setLoading(true);
    const token = localStorage.getItem("token");

    if (!token || !trxNumber) {
      setError("Token atau nomor transaksi tidak ditemukan.");
      setLoading(false);
      return;
    }

    try {
      const response = await getTransaction(trxNumber, token);

      if (!response || !response.status) {
        throw new Error("Data transaksi tidak valid.");
      }

      setStatus(response.status);
      setResponseData(response);
      setError(null);
    } catch (err: any) {
      console.error("Gagal mengambil transaksi:", err);
      setError(
        err.message || "Terjadi kesalahan saat mengambil data transaksi."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (trxNumber) fetchPaymentStatus();
  }, [trxNumber]);

  // Auto-redirect jika status SUCCESS
  useEffect(() => {
    if (status === "SUCCESS") {
      const timer = setTimeout(() => {
        navigate("/dashboard");
      }, 5000);

      return () => clearTimeout(timer);
    }
  }, [status, navigate]);

  const handleBayar = async () => {
    const token = localStorage.getItem("token");

    if (!token || !trxNumber || !responseData) {
      setError("Data tidak lengkap untuk melakukan pembayaran.");
      return;
    }

    setProcessing(true);
    try {
      const response = await updateTransaction(trxNumber, responseData, token);

      if (!response || !response.status) {
        throw new Error("Respons update transaksi tidak valid.");
      }

      await fetchPaymentStatus(); // Refresh status
      setError(null);
    } catch (err: any) {
      console.error("Gagal melakukan update transaksi:", err);
      setError(err.message || "Gagal memproses pembayaran.");
    } finally {
      setProcessing(false);
    }
  };

  const renderStatusColor = (status: string) => {
    switch (status) {
      case "SUCCESS":
        return "text-green-600";
      case "IN PROGRESS":
        return "text-yellow-500";
      case "FAILED":
        return "text-red-600";
      default:
        return "text-gray-600";
    }
  };

  return (
    <div className="max-w-xl mx-auto mt-12 p-8 bg-white rounded-2xl shadow-xl">
      <h2 className="text-3xl font-bold text-gray-800 mb-6 text-center">
        Status Pembayaran
      </h2>

      {loading ? (
        <p className="text-center text-blue-500">Memuat data transaksi...</p>
      ) : error ? (
        <p className="text-center text-red-600">{error}</p>
      ) : (
        <>
          {status && (
            <div className="text-center mb-4">
              <span className="text-gray-700 font-medium">
                Status Transaksi:
              </span>{" "}
              <span className={`font-bold ${renderStatusColor(status)}`}>
                {status}
              </span>
            </div>
          )}

          {responseData && (
            <div className="bg-gray-50 rounded-lg p-6 space-y-3 text-sm text-gray-700">
              <div className="flex justify-between">
                <span className="font-medium">Produk:</span>
                <span>{responseData.productName}</span>
              </div>
              <div className="flex justify-between">
                <span className="font-medium">Nominal Investasi:</span>
                <span>
                  Rp {responseData.amount?.toLocaleString("id-ID") || "-"}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="font-medium">Harga per Unit:</span>
                <span>
                  Rp {responseData.productPrice?.toLocaleString("id-ID") || "-"}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="font-medium">Jumlah Lot Diperoleh:</span>
                <span>{responseData.lot}</span>
              </div>
              <div className="flex justify-between">
                <span className="font-medium">Goal Keuangan:</span>
                <span>{responseData.goalName}</span>
              </div>
            </div>
          )}

          {status === "SUCCESS" ? (
            <div className="mt-6 text-center">
              <p className="text-green-600 font-semibold">
                Pembayaran berhasil! Anda akan diarahkan ke dashboard dalam
                beberapa detik...
              </p>
              <button
                onClick={() => navigate("/dashboard")}
                className="mt-4 bg-gray-200 text-gray-800 py-2 px-4 rounded hover:bg-gray-300 transition"
              >
                Kembali ke Dashboard Sekarang
              </button>
            </div>
          ) : (
            <button
              onClick={handleBayar}
              disabled={processing || status === "SUCCESS"}
              className="mt-6 w-full bg-blue-600 text-white font-semibold py-3 rounded-lg hover:bg-blue-700 transition disabled:opacity-50"
            >
              {processing ? "Memproses..." : "Bayar Sekarang"}
            </button>
          )}
        </>
      )}
    </div>
  );
};

export default PaymentPage;
