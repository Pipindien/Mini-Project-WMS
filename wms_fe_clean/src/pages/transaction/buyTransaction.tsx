import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { buyTransaction } from "../../services/transaction/api";
import { BuyTransactionRequest } from "../../services/transaction/type";
import useProductById from "../hooks/useProduct/useProductById";
import useGoal from "../hooks/useGoal/useGoal";

const BuyTransaction: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { product, loading, error } = useProductById(id);
  const { goals, goalsLoading, goalsError } = useGoal();

  const [amount, setAmount] = useState<number>(0);
  const [goalName, setGoalName] = useState<string>("");
  const [notes, setNotes] = useState<string>("Transaksi in Progress");
  const [submitError, setSubmitError] = useState<string | null>(null);

  useEffect(() => {
    if (goals.length > 0) {
      setGoalName(goals[0].goalName);
    }
  }, [goals]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!product) return;

    const token = localStorage.getItem("token");
    if (!token) {
      setSubmitError("Token tidak ditemukan. Silakan login kembali.");
      return;
    }

    const request: BuyTransactionRequest = {
      amount,
      productName: product.productName,
      goalName,
      notes,
    };

    try {
      const response = await buyTransaction(request, token);
      const trxNumber = response.trxNumber;

      // ✅ Redirect ke halaman pembayaran
      navigate(`/payment/${trxNumber}`);
    } catch (err) {
      setSubmitError("Gagal melakukan transaksi.");
    }
  };

  if (loading) return <div className="text-center mt-20">Loading...</div>;
  if (error)
    return <div className="text-center mt-20 text-red-600">{error}</div>;
  if (!product)
    return <div className="text-center mt-20">Product not found.</div>;

  return (
    <div className="max-w-xl mx-auto mt-10 p-6 bg-white rounded-xl shadow-md">
      <h2 className="text-2xl font-bold mb-4">Beli {product.productName}</h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block font-semibold">Harga per unit:</label>
          <p className="text-gray-700">
            Rp {product.productPrice.toLocaleString()}
          </p>
        </div>

        <div>
          <label className="block font-semibold">Jumlah Dana (Rp):</label>
          <input
            type="number"
            className="w-full border border-gray-300 p-2 rounded"
            value={amount}
            onChange={(e) => setAmount(Number(e.target.value))}
            required
          />
        </div>

        <div>
          <label className="block font-semibold">Tujuan Keuangan:</label>
          {goalsLoading ? (
            <p className="text-gray-500">Loading goals...</p>
          ) : goalsError ? (
            <p className="text-red-600">{goalsError}</p>
          ) : (
            <select
              className="w-full border border-gray-300 p-2 rounded"
              value={goalName}
              onChange={(e) => setGoalName(e.target.value)}
              required
            >
              {goals.map((goal) => (
                <option key={goal.goalId} value={goal.goalName}>
                  {goal.goalName}
                </option>
              ))}
            </select>
          )}
        </div>

        <div>
          <label className="block font-semibold">Catatan:</label>
          <textarea
            className="w-full border border-gray-300 p-2 rounded"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            rows={3}
          />
        </div>

        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition"
        >
          Beli Sekarang
        </button>

        {submitError && (
          <div className="text-red-600 text-center mt-4">{submitError}</div>
        )}
      </form>
    </div>
  );
};

export default BuyTransaction;
