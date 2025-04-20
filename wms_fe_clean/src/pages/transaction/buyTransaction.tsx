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
      navigate(`/payment/${trxNumber}`);
    } catch (err) {
      setSubmitError("Gagal melakukan transaksi.");
    }
  };

  if (loading)
    return <div className="text-center mt-20 text-blue-600">Loading...</div>;
  if (error)
    return <div className="text-center mt-20 text-red-600">{error}</div>;
  if (!product)
    return (
      <div className="text-center mt-20 text-gray-500">Product not found.</div>
    );

  return (
    <div className="max-w-2xl mx-auto mt-12 px-6 py-8 bg-white rounded-2xl shadow-lg">
      <h2 className="text-3xl font-bold text-gray-800 mb-6">
        Beli Produk: {product.productName}
      </h2>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label className="block text-sm font-semibold text-gray-700 mb-1">
            Harga per Unit
          </label>
          <p className="text-lg text-green-700 font-medium">
            Rp {product.productPrice.toLocaleString("id-ID")}
          </p>
        </div>

        <div>
          <label className="block text-sm font-semibold text-gray-700 mb-1">
            Jumlah Dana Investasi (Rp)
          </label>
          <input
            type="number"
            className="w-full border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={amount}
            onChange={(e) => setAmount(Number(e.target.value))}
            required
            placeholder="Masukkan nominal dana"
          />
        </div>

        <div>
          <label className="block text-sm font-semibold text-gray-700 mb-1">
            Tujuan Keuangan
          </label>
          {goalsLoading ? (
            <p className="text-sm text-gray-500">Memuat tujuan keuangan...</p>
          ) : goalsError ? (
            <p className="text-sm text-red-500">{goalsError}</p>
          ) : (
            <select
              className="w-full border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
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
          <label className="block text-sm font-semibold text-gray-700 mb-1">
            Catatan Transaksi
          </label>
          <textarea
            className="w-full border border-gray-300 p-3 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            rows={3}
          />
        </div>

        <button
          type="submit"
          className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-lg transition duration-200"
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
