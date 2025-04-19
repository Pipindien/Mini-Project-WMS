import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  getTransaction,
  updateTransaction,
} from "../../services/transaction/api";
import { BuyTransactionResponse } from "../../services/transaction/type";

const PaymentPage: React.FC = () => {
  const { trxNumber } = useParams();
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

      // Setelah berhasil, fetch ulang status transaksi
      await fetchPaymentStatus();

      setError(null);
    } catch (err: any) {
      console.error("Gagal melakukan update transaksi:", err);
      setError(err.message || "Gagal memproses pembayaran.");
    } finally {
      setProcessing(false);
    }
  };

  return (
    <div className="max-w-xl mx-auto mt-10 p-6 bg-white rounded-xl shadow-md text-center">
      <h2 className="text-2xl font-bold mb-4">Status Pembayaran</h2>

      {loading ? (
        <p>Memuat data transaksi...</p>
      ) : error ? (
        <p className="text-red-600">{error}</p>
      ) : (
        <>
          {status && (
            <div className="text-lg font-semibold">
              Status Transaksi:{" "}
              <span
                className={
                  status === "SUCCESS"
                    ? "text-green-600"
                    : status === "IN PROGRESS"
                    ? "text-yellow-500"
                    : "text-red-600"
                }
              >
                {status}
              </span>
            </div>
          )}

          {responseData && (
            <div className="mt-4 text-left">
              <p>
                <strong>Produk:</strong> {responseData.productName}
              </p>
              <p>
                <strong>Nominal:</strong> Rp{" "}
                {responseData.amount != null
                  ? responseData.amount.toLocaleString()
                  : "-"}
              </p>
              <p>
                <strong>Harga per Unit:</strong> Rp{" "}
                {responseData.productPrice != null
                  ? responseData.productPrice.toLocaleString()
                  : "-"}
              </p>
              <p>
                <strong>Lot:</strong> {responseData.lot}
              </p>
              <p>
                <strong>Financial Goal:</strong> {responseData.goalName}
              </p>
            </div>
          )}

          <button
            className="mt-6 bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition disabled:opacity-50"
            onClick={handleBayar}
            disabled={processing || status === "SUCCESS"}
          >
            {processing ? "Memproses..." : "Bayar"}
          </button>
        </>
      )}
    </div>
  );
};

export default PaymentPage;
