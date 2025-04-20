import React, { useState, FormEvent } from "react";
import { useNavigate, Link } from "react-router-dom";
import { register } from "../../services/api/register";
import { RegisterPayload } from "../../services/auth/type";

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

  const [birthDate, setBirthDate] = useState(""); // YYYY-MM-DD
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string>("");

  const calculateAge = (birth: string) => {
    const today = new Date();
    const birthD = new Date(birth);
    let age = today.getFullYear() - birthD.getFullYear();
    const m = today.getMonth() - birthD.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < birthD.getDate())) {
      age--;
    }
    return age;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: name === "salary" ? Number(value) : value,
    }));
  };

  const handleSubmit = async (e: FormEvent) => {
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
    <div className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-black text-white flex flex-col md:flex-row transition-all duration-500">
      <div className="w-full md:w-1/4 flex items-center justify-center px-6 py-12">
        <div className="w-full max-w-md bg-white/20 backdrop-blur-md rounded-md shadow-2xl p-8 animate-fade-in-up flex flex-col h-full">
          <div className="flex-grow flex flex-col justify-center">
            <h2 className="text-4xl font-bold text-center text-white drop-shadow-md mb-2 transition-transform duration-300 hover:scale-105 hover:brightness-110">
              WealthScape
            </h2>
            <p className="text-center text-sm text-gray-300 mb-6">
              Create your account to begin managing your wealth journey.
            </p>

            <form className="space-y-4" onSubmit={handleSubmit}>
              <Input
                label="Full Name"
                name="fullName"
                value={form.fullName}
                onChange={handleChange}
                placeholder="Enter your full name"
              />
              <Input
                label="Email"
                name="email"
                type="email"
                value={form.email}
                onChange={handleChange}
                placeholder="Enter your email address"
              />
              <Input
                label="Phone"
                name="phone"
                value={form.phone}
                onChange={handleChange}
                placeholder="Enter your phone number"
              />

              <div>
                <label
                  htmlFor="birthDate"
                  className="block text-sm font-medium text-gray-300 mb-1"
                >
                  Birth Date
                </label>
                <input
                  type="date"
                  id="birthDate"
                  name="birthDate"
                  value={birthDate}
                  onChange={(e) => {
                    const date = e.target.value;
                    setBirthDate(date);
                    const calculatedAge = calculateAge(date);
                    setForm((prev) => ({ ...prev, age: calculatedAge }));
                  }}
                  className="w-full px-4 py-2 border border-gray-500 rounded-md bg-black/30 text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 transition-all duration-300 hover:scale-105"
                  required
                />
              </div>

              <Input
                label="Salary"
                name="salary"
                type="number"
                value={form.salary}
                onChange={handleChange}
                placeholder="Enter your salary"
              />
              <Input
                label="Username"
                name="username"
                value={form.username}
                onChange={handleChange}
                placeholder="Choose a username"
              />
              <Input
                label="Password"
                name="password"
                type="password"
                value={form.password}
                onChange={handleChange}
                placeholder="Enter your password"
              />
              <Input
                label="Confirm Password"
                name="confirmPassword"
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Confirm your password"
              />

              <button
                type="submit"
                disabled={loading}
                className="w-full bg-gradient-to-r from-purple-600 to-purple-600 hover:from-white hover:via-purple-600 hover:to-white text-white font-semibold py-2 rounded-md shadow-md hover:shadow-lg transition-all duration-300 hover:scale-105"
              >
                {loading ? "Registering..." : "Create Account"}
              </button>
              {message && (
                <p className="text-sm text-center text-white mt-2">{message}</p>
              )}
            </form>
          </div>
          <div className="text-center mt-6 text-sm text-gray-400">
            Already have an account?{" "}
            <Link
              to="/"
              className="text-purple-400 hover:text-purple-200 underline transition-transform duration-300 hover:scale-105 hover:brightness-110"
            >
              Login here
            </Link>
          </div>
        </div>
      </div>

      <div className="w-full md:w-3/4 relative hidden md:block overflow-hidden">
        <img
          src="https://cdn.pixabay.com/photo/2023/07/08/07/25/ai-generated-8113892_1280.jpg"
          alt="Finance Register Illustration"
          className="w-full h-full object-cover scale-105 transform transition-transform duration-500"
        />
        <div className="absolute inset-0 bg-gradient-to-br from-indigo-800 to-purple-800 opacity-60" />
        <div className="absolute inset-0 flex flex-col justify-center px-12 text-white z-10 animate-fade-in">
          <h2 className="text-4xl font-extrabold mb-4 leading-snug drop-shadow-lg transition-transform duration-300 hover:scale-105 hover:brightness-110">
            Start Your Wealth Journey
            <br />
            Build a better financial future.
          </h2>
          <p className="text-lg text-gray-200 transition-transform duration-300 hover:scale-105 hover:brightness-110">
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
  placeholder,
}: {
  label: string;
  name: string;
  type?: string;
  value: string | number;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  placeholder: string;
}) => (
  <div>
    <label className="block text-sm font-medium text-gray-300 mb-1">
      {label}
    </label>
    <input
      type={type}
      name={name}
      value={value}
      onChange={onChange}
      className="w-full px-4 py-2 border border-gray-500 rounded-md bg-black/30 text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 transition-all duration-300 hover:scale-105"
      placeholder={placeholder}
      required
    />
  </div>
);

export default RegisterPage;
