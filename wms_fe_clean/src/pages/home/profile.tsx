import React, { useEffect, useState } from "react";
import { getProfile, updateBalance } from "../../services/auth/api";
import { LoginResponse } from "../../services/auth/type";
import {
  UserCircle2,
  Mail,
  Phone,
  Calendar,
  PiggyBank,
  Wallet,
  X,
} from "lucide-react";

const Profile: React.FC = () => {
  const [profile, setProfile] = useState<LoginResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [amount, setAmount] = useState<number>(0);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const data = await getProfile();
        setProfile(data);
      } catch (error) {
        console.error("Failed to fetch profile:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  const handleBalanceSubmit = async () => {
    if (!profile || !token) return;
    setIsSubmitting(true);
    setError(null);

    try {
      // Default isAddition is true
      await updateBalance(profile.custId, amount, true, token);
      const updatedProfile = await getProfile(); // Refresh profile
      setProfile(updatedProfile);
      setShowModal(false);
      setAmount(0);
    } catch (err: any) {
      setError("Gagal memperbarui saldo.");
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading)
    return (
      <div className="flex justify-center items-center h-64 text-gray-600">
        Loading profile...
      </div>
    );

  if (!profile)
    return <div className="text-center text-red-500">Profile not found.</div>;

  return (
    <div className="max-w-lg mx-auto bg-white shadow-lg rounded-2xl p-8 mt-10 space-y-6">
      <div className="flex items-center space-x-4">
        <UserCircle2 className="w-14 h-14 text-blue-500" />
        <div>
          <h2 className="text-2xl font-bold">{profile.fullname}</h2>
          <p className="text-gray-500 capitalize">{profile.role}</p>
        </div>
      </div>

      <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 text-sm text-gray-700">
        <div className="flex items-center gap-2">
          <Mail className="w-5 h-5 text-blue-400" />
          <span>{profile.email}</span>
        </div>
        <div className="flex items-center gap-2">
          <Phone className="w-5 h-5 text-green-500" />
          <span>{profile.phone}</span>
        </div>
        <div className="flex items-center gap-2">
          <Calendar className="w-5 h-5 text-indigo-500" />
          <span>{profile.age} years</span>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-4">
        <div className="bg-blue-50 p-4 rounded-xl shadow-sm border">
          <div className="flex items-center gap-2 text-blue-700 mb-1">
            <PiggyBank className="w-5 h-5" />
            <span className="font-semibold">Salary</span>
          </div>
          <p className="text-lg font-bold text-blue-800">
            Rp {profile.salary?.toLocaleString()}
          </p>
        </div>

        <div
          className="bg-green-50 p-4 rounded-xl shadow-sm border cursor-pointer hover:bg-green-100 transition"
          onClick={() => setShowModal(true)}
        >
          <div className="flex items-center gap-2 text-green-700 mb-1">
            <Wallet className="w-5 h-5" />
            <span className="font-semibold">Balance</span>
          </div>
          <p className="text-lg font-bold text-green-800">
            Rp {profile.balance?.toLocaleString()}
          </p>
        </div>
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
          <div className="bg-white rounded-2xl p-6 w-full max-w-md relative shadow-xl animate-fade-in">
            <button
              className="absolute top-3 right-3 text-gray-400 hover:text-red-500 transition"
              onClick={() => setShowModal(false)}
            >
              <X size={20} />
            </button>
            <h3 className="text-2xl font-semibold mb-6 text-center text-gray-800">
              Tambah Saldo
            </h3>

            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-600 mb-1">
                Jumlah (Rp)
              </label>
              <input
                type="number"
                className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
                placeholder="Contoh: 100000"
                value={amount}
                onChange={(e) => setAmount(Number(e.target.value))}
              />
            </div>

            {error && (
              <p className="text-sm text-red-500 mb-3 text-center">{error}</p>
            )}

            <button
              className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 rounded-lg transition disabled:opacity-50"
              onClick={handleBalanceSubmit}
              disabled={isSubmitting}
            >
              {isSubmitting ? "Menyimpan..." : "Simpan"}
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Profile;
