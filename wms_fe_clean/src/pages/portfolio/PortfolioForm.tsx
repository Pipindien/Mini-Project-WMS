import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createGoal } from "../../services/goal/api";

const CreateGoalPage: React.FC = () => {
  const navigate = useNavigate();
  const [goalName, setGoalName] = useState("");
  const [targetAmount, setTargetAmount] = useState<number>(0);
  const [targetDate, setTargetDate] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    const token = localStorage.getItem("token");
    if (!token) {
      setError("Token tidak ditemukan. Silakan login.");
      setLoading(false);
      return;
    }

    try {
      await createGoal({ goalName, targetAmount, targetDate }, token);
      navigate("/portfolio"); // ganti sesuai rute daftar goal kamu
    } catch {
      setError("Gagal membuat goal. Coba lagi.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-xl mx-auto mt-10 p-6 bg-white shadow rounded">
      <h1 className="text-2xl font-bold mb-4">Buat Goal Baru</h1>

      {error && <p className="text-red-500 mb-4">{error}</p>}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block font-semibold mb-1">Nama Goal</label>
          <input
            type="text"
            value={goalName}
            onChange={(e) => setGoalName(e.target.value)}
            required
            className="w-full border px-3 py-2 rounded"
          />
        </div>

        <div>
          <label className="block font-semibold mb-1">Target Dana (Rp)</label>
          <input
            type="number"
            value={targetAmount}
            onChange={(e) => setTargetAmount(Number(e.target.value))}
            required
            className="w-full border px-3 py-2 rounded"
          />
        </div>

        <div>
          <label className="block font-semibold mb-1">Tanggal Target</label>
          <input
            type="date"
            value={targetDate}
            onChange={(e) => setTargetDate(e.target.value)}
            required
            className="w-full border px-3 py-2 rounded"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
        >
          {loading ? "Menyimpan..." : "Buat Goal"}
        </button>
      </form>
    </div>
  );
};

export default CreateGoalPage;
