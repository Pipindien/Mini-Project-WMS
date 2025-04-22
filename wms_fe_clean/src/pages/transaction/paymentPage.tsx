import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  getTransaction,
  updateTransaction,
} from "../../services/transaction/api";
import { BuyTransactionResponse } from "../../services/transaction/type";
import { Loader2, CheckCircle, XCircle, Clock } from "lucide-react";

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
      setError("Token or transaction number not found");
      setLoading(false);
      return;
    }

    try {
      const response = await getTransaction(trxNumber, token);

      if (!response || !response.status) {
        throw new Error("Invalid transaction data");
      }

      setStatus(response.status);
      setResponseData(response);
      setError(null);
    } catch (err: any) {
      console.error("Failed to retrieve transaction:", err);
      setError(
        err.message || "An error occurred while retrieving transaction data."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (trxNumber) fetchPaymentStatus();
  }, [trxNumber]);

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
      setError("Incomplete data could not make payment");
      return;
    }

    setProcessing(true);
    try {
      const response = await updateTransaction(trxNumber, responseData, token);

      if (!response || !response.status) {
        throw new Error("Invalid transaction update response");
      }

      await fetchPaymentStatus();
      setError(null);
    } catch (err: any) {
      console.error("Failed to update transaction:", err);
      setError(err.message || "Failed to process payment");
    } finally {
      setProcessing(false);
    }
  };

  const renderStatusIcon = (status: string) => {
    switch (status) {
      case "SUCCESS":
        return (
          <CheckCircle className="text-green-600 w-6 h-6 inline-block mr-1 animate-bounce" />
        );
      case "IN PROGRESS":
        return (
          <Clock className="text-yellow-500 w-6 h-6 inline-block mr-1 animate-pulse" />
        );
      case "FAILED":
        return (
          <XCircle className="text-red-600 w-6 h-6 inline-block mr-1 animate-shake" />
        );
      default:
        return (
          <Clock className="text-gray-500 w-6 h-6 inline-block mr-1 animate-spin" />
        );
    }
  };

  const renderStatusTextColor = (status: string) => {
    switch (status) {
      case "SUCCESS":
        return "text-green-600";
      case "IN PROGRESS":
        return "text-yellow-500";
      case "FAILED":
        return "text-red-600";
      default:
        return "text-gray-500";
    }
  };

  return (
    <div className="max-w-2xl mx-auto mt-16 px-8 py-10 bg-white shadow-2xl rounded-3xl transition-all animate-fade-in">
      <h2 className="text-4xl font-extrabold text-center text-gray-800 mb-8">
        💸 Status Pembayaran
      </h2>

      {loading ? (
        <div className="text-center text-blue-600 flex flex-col items-center">
          <Loader2 className="w-6 h-6 animate-spin mb-2" />
          <p>Memuat data transaksi...</p>
        </div>
      ) : error ? (
        <p className="text-center text-red-600 font-medium animate-fade-in">
          {error}
        </p>
      ) : (
        <>
          {status && (
            <div className="flex items-center justify-center mb-6 transition-all duration-300 scale-in">
              {renderStatusIcon(status)}
              <span
                className={`text-lg font-semibold ${renderStatusTextColor(
                  status
                )}`}
              >
                {status}
              </span>
            </div>
          )}

          {responseData && (
            <div className="bg-gray-50 rounded-xl p-6 space-y-4 shadow-inner border border-gray-100 animate-fade-in-up">
              <DetailRow label="📦 Produk" value={responseData.productName} />
              <DetailRow
                label="💰 Investment Nominal"
                value={`Rp ${
                  responseData.amount?.toLocaleString("id-ID") || "-"
                }`}
              />
              <DetailRow
                label="💵 Price per Unit"
                value={`Rp ${
                  responseData.productPrice?.toLocaleString("id-ID") || "-"
                }`}
              />
              <DetailRow
                label="📊 Number of Lots Acquired"
                value={responseData.lot}
              />
              <DetailRow
                label="🎯 Financial Goals"
                value={responseData.goalName}
              />
            </div>
          )}

          {status === "SUCCESS" ? (
            <div className="mt-8 text-center animate-fade-in">
              <p className="text-green-600 font-semibold mb-4">
                ✅ Pembayaran berhasil! Anda akan diarahkan ke dashboard dalam
                beberapa detik...
              </p>
              <button
                onClick={() => navigate("/dashboard")}
                className="bg-gray-200 text-gray-800 font-medium py-2 px-5 rounded-xl hover:bg-gray-300 transition-transform transform hover:scale-105"
              >
                Kembali ke Dashboard Sekarang
              </button>
            </div>
          ) : (
            <button
              onClick={handleBayar}
              disabled={processing || status === "SUCCESS"}
              className="mt-8 w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 rounded-xl transition-all duration-300 disabled:opacity-50 hover:shadow-lg hover:scale-[1.02]"
            >
              {processing ? "Processing..." : "💳 Purchase Now"}
            </button>
          )}
        </>
      )}
    </div>
  );
};

const DetailRow: React.FC<{
  label: string;
  value?: string | number | null;
}> = ({ label, value }) => (
  <div className="flex justify-between text-gray-700 transition-all duration-300">
    <span className="font-medium">{label}</span>
    <span>{value ?? "-"}</span>
  </div>
);

export default PaymentPage;
