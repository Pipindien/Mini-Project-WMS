import React, { useEffect, useState } from "react";
import { getProfile } from "../../services/auth/api";
import { LoginResponse } from "../../services/auth/type";
import {
  UserCircle2,
  Mail,
  Phone,
  Calendar,
  PiggyBank,
  Wallet,
} from "lucide-react";

const Profile: React.FC = () => {
  const [profile, setProfile] = useState<LoginResponse | null>(null);
  const [loading, setLoading] = useState(true);

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

      {/* Highlighted financial section */}
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

        <div className="bg-green-50 p-4 rounded-xl shadow-sm border">
          <div className="flex items-center gap-2 text-green-700 mb-1">
            <Wallet className="w-5 h-5" />
            <span className="font-semibold">Balance</span>
          </div>
          <p className="text-lg font-bold text-green-800">
            Rp {profile.balance?.toLocaleString()}
          </p>
        </div>
      </div>
    </div>
  );
};

export default Profile;
