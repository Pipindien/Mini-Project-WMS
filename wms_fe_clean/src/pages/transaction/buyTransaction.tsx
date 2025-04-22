import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { buyTransaction } from "../../services/transaction/api";
import { BuyTransactionRequest } from "../../services/transaction/type";
import useProductById from "../hooks/useProduct/useProductById";
import useGoal from "../hooks/useGoal/useGoal";
import "animate.css"; // Import animate.css for animations

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
    return (
      <div className="min-h-screen bg-gradient-to-br from-sky-200 via-blue-300 to-indigo-200 flex items-center justify-center px-4 py-10 animate__animated animate__fadeIn">
        <p className="text-center text-gray-700 animate__animated animate__pulse animate__infinite">
          Memuat detail produk...
        </p>
      </div>
    );
  if (error)
    return (
      <div className="min-h-screen bg-gradient-to-br from-sky-200 via-blue-300 to-indigo-200 flex items-center justify-center px-4 py-10 animate__animated animate__fadeIn">
        <p className="text-center text-red-600 animate__animated animate__shake">
          {error}
        </p>
      </div>
    );
  if (!product)
    return (
      <div className="min-h-screen bg-gradient-to-br from-sky-200 via-blue-300 to-indigo-200 flex items-center justify-center px-4 py-10 animate__animated animate__fadeIn">
        <p className="text-center text-gray-500">Produk tidak ditemukan.</p>
      </div>
    );

  return (
    <div className="min-h-screen bg-gradient-to-br from-sky-200 via-blue-300 to-indigo-200 flex items-center justify-center px-4 py-10 animate__animated animate__fadeIn">
      <div className="w-full max-w-md bg-white/80 backdrop-blur-md p-8 rounded-xl shadow-lg border border-white/60 transition-all duration-300 animate__animated animate__slideInDown">
        <h2 className="text-3xl font-extrabold text-gray-800 text-center mb-6 tracking-tight drop-shadow-sm animate__animated animate__fadeIn">
          🛒 Beli Produk
        </h2>
        <p className="text-lg text-center text-gray-700 mb-4 animate__animated animate__fadeIn animate__delay-1s">
          {product.productName}
        </p>

        <form
          onSubmit={handleSubmit}
          className="space-y-4 animate__animated animate__fadeIn animate__delay-1s"
        >
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2 animate__animated animate__fadeIn">
              💰 Harga per Unit
            </label>
            <p className="text-lg text-blue-700 font-medium animate__animated animate__slideInLeft">
              Rp {product.productPrice.toLocaleString("id-ID")}
            </p>
          </div>

          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2 animate__animated animate__fadeIn">
              💸 Jumlah Dana Investasi (Rp)
            </label>
            <input
              type="number"
              min="1"
              className="w-full border border-gray-300 p-3 rounded-md focus:outline-none focus:ring-2 focus:ring-sky-400 bg-white/90 placeholder-gray-400 animate__animated animate__slideInRight"
              value={amount}
              onChange={(e) => setAmount(Number(e.target.value))}
              required
              placeholder="Masukkan nominal dana"
            />
          </div>

          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2 animate__animated animate__fadeIn">
              🎯 Tujuan Keuangan
            </label>
            {goalsLoading ? (
              <p className="text-sm text-gray-500 animate__animated animate__pulse animate__infinite">
                Memuat tujuan...
              </p>
            ) : goalsError ? (
              <p className="text-sm text-red-500 animate__animated animate__shake">
                {goalsError}
              </p>
            ) : (
              <select
                className="w-full border border-gray-300 p-3 rounded-md focus:outline-none focus:ring-2 focus:ring-sky-400 bg-white/90 animate__animated animate__slideInLeft"
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
            <label className="block text-sm font-semibold text-gray-700 mb-2 animate__animated animate__fadeIn">
              📝 Catatan Transaksi
            </label>
            <textarea
              className="w-full border border-gray-300 p-3 rounded-md focus:outline-none focus:ring-2 focus:ring-sky-400 bg-white/90 animate__animated animate__slideInRight"
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              rows={3}
            />
          </div>

          <button
            type="submit"
            className="w-full bg-gradient-to-r from-sky-400 to-blue-500 hover:from-sky-500 hover:to-blue-600 text-white font-semibold py-3 px-6 rounded-md transition duration-200 shadow-md hover:shadow-lg animate__animated animate__fadeIn animate__delay-2s"
          >
            💸 Beli Sekarang
          </button>

          {submitError && (
            <div className="text-red-600 text-center mt-4 animate__animated animate__shake">
              {submitError}
            </div>
          )}
        </form>
      </div>
    </div>
  );
};

export default BuyTransaction;
