import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { register, RegisterPayload } from "../../services/api/register";

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState<RegisterPayload>({
    fullName: "",
    email: "",
    phone: "",
    age: 0,
    salary: 0,
    username: "",
    password: "",
  });

  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: ["age", "salary"].includes(name) ? Number(value) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (form.password !== confirmPassword) {
      alert("Passwords do not match");
      return;
    }

    setLoading(true);
    try {
      await register(form);
      setMessage("Register berhasil! Redirecting...");
      setTimeout(() => navigate("/"), 1500);
    } catch (error) {
      setMessage("Gagal register.");
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col md:flex-row bg-white">
      <div className="w-full md:w-1/2 flex items-center justify-center px-6 py-12 bg-gradient-to-br from-indigo-600 to-purple-700">
        <div className="w-full max-w-md bg-white rounded-3xl shadow-2xl p-10">
          <h2 className="text-4xl font-bold text-center text-indigo-700 mb-2">
            WealthScape
          </h2>
          <p className="text-center text-sm text-gray-500 mb-8">
            Create your account to begin managing your wealth journey.
          </p>

          <form className="space-y-5" onSubmit={handleSubmit}>
            <Input
              label="Full Name"
              name="fullName"
              value={form.fullName}
              onChange={handleChange}
            />
            <Input
              label="Email"
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
            />
            <Input
              label="Phone"
              name="phone"
              value={form.phone}
              onChange={handleChange}
            />
            <Input
              label="Age"
              name="age"
              type="number"
              value={form.age}
              onChange={handleChange}
            />
            <Input
              label="Salary"
              name="salary"
              type="number"
              value={form.salary}
              onChange={handleChange}
            />
            <Input
              label="Username"
              name="username"
              value={form.username}
              onChange={handleChange}
            />
            <Input
              label="Password"
              name="password"
              type="password"
              value={form.password}
              onChange={handleChange}
            />
            <Input
              label="Confirm Password"
              name="confirmPassword"
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
            />

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-gradient-to-r from-indigo-600 to-purple-600 text-white py-2 rounded-lg hover:opacity-90 transition"
            >
              {loading ? "Registering..." : "Create Account"}
            </button>
          </form>

          {message && (
            <p className="mt-4 text-center text-sm text-gray-600">{message}</p>
          )}

          <div className="text-center mt-6 text-sm text-gray-600">
            Already have an account?{" "}
            <a href="/" className="text-indigo-600 hover:underline">
              Login here
            </a>
          </div>
        </div>
      </div>

      {/* Right Panel */}
      <div className="w-full md:w-1/2 relative hidden md:block">
        <img
          src="https://cdn.pixabay.com/photo/2023/07/08/07/25/ai-generated-8113892_1280.jpg"
          alt="Finance Register Illustration"
          className="w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-gradient-to-br from-indigo-800 to-purple-700 opacity-50" />
        <div className="absolute inset-0 flex flex-col justify-center px-12 text-white z-10">
          <h2 className="text-4xl font-bold mb-4 leading-snug">
            Start Your Wealth Journey
            <br />
            Build a better financial future.
          </h2>
          <p className="text-lg text-gray-200">
            Join WealthScape to track your investments, goals, and financial
            progress all in one place.
          </p>
        </div>
      </div>
    </div>
  );
};

const Input = ({
  label,
  name,
  type = "text",
  value,
  onChange,
}: {
  label: string;
  name: string;
  type?: string;
  value: string | number;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
}) => (
  <div>
    <label className="block text-sm font-medium text-gray-700 mb-1">
      {label}
    </label>
    <input
      type={type}
      name={name}
      value={value}
      onChange={onChange}
      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400"
      required
    />
  </div>
);

export default RegisterPage;
