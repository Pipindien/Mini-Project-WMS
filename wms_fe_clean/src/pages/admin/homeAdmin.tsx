import React, { useState, useEffect } from "react";
import { Product } from "../../services/product/type";
import { getProducts, deleteProduct } from "../../services/product/api";
import { useNavigate } from "react-router-dom";
import { FaTrash, FaEdit, FaPlus } from "react-icons/fa";

const HomeAdmin: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const navigate = useNavigate();

  const fetchData = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await getProducts();
      setProducts(data);
    } catch (err) {
      setError("❌ Failed to fetch products");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleDelete = async (id: string) => {
    const confirmDelete = window.confirm(
      "Are you sure you want to delete this product?"
    );
    if (!confirmDelete) return;

    try {
      await deleteProduct(id);
      setSuccessMessage("✅ Product deleted successfully");

      setTimeout(() => {
        setSuccessMessage("");
        fetchData();
      }, 3000);
    } catch (err) {
      setError("❌ Delete failed");
    }
  };

  return (
    <div className="container mx-auto px-6 py-10">
      {/* Welcome Message */}
      <div className="mb-6">
        <h2 className="text-2xl font-semibold text-gray-700">
          Welcome, <span className="text-indigo-600 font-bold">ADMIN 👋</span>
        </h2>
        <p className="text-sm text-gray-500 mt-1">
          Here's what's happening with your products today.
        </p>
      </div>

      {/* Header */}
      <div className="bg-gradient-to-br from-blue-500 to-indigo-600 text-white px-6 py-6 rounded-xl shadow-md mb-8">
        <div className="flex justify-between items-center">
          <h1 className="text-3xl font-bold">📦 Admin Product Dashboard</h1>
          <button
            onClick={() => navigate("/dashboardAdmin/create")}
            className="bg-white text-indigo-600 font-semibold px-5 py-2 rounded-md shadow hover:bg-indigo-50 flex items-center gap-2 transition-all duration-300"
          >
            <FaPlus />
            Add Product
          </button>
        </div>
        <p className="text-sm opacity-90 mt-2">
          Manage your product listings efficiently.
        </p>
      </div>

      {/* Feedback Messages */}
      {error && (
        <div className="bg-red-100 border border-red-300 text-red-700 px-4 py-2 mb-4 rounded">
          {error}
        </div>
      )}
      {successMessage && (
        <div className="bg-green-100 border border-green-300 text-green-700 px-4 py-2 mb-4 rounded">
          {successMessage}
        </div>
      )}

      {/* Table */}
      <div className="overflow-x-auto bg-white shadow-lg rounded-xl">
        {loading ? (
          <div className="text-center py-16 text-indigo-600 font-semibold text-lg">
            Loading products...
          </div>
        ) : (
          <table className="min-w-full">
            <thead className="bg-gray-100 text-left text-sm text-gray-600 uppercase tracking-wider">
              <tr>
                <th className="px-6 py-3">Name</th>
                <th className="px-6 py-3">Category</th>
                <th className="px-6 py-3">Price</th>
                <th className="px-6 py-3">Rate</th>
                <th className="px-6 py-3">Actions</th>
              </tr>
            </thead>
            <tbody>
              {products.length === 0 ? (
                <tr>
                  <td colSpan={5} className="text-center py-6 text-gray-500">
                    No products available.
                  </td>
                </tr>
              ) : (
                products.map((prod, index) => (
                  <tr
                    key={prod.productId}
                    className={`${
                      index % 2 === 0 ? "bg-white" : "bg-gray-50"
                    } hover:bg-indigo-50 transition-all`}
                  >
                    <td className="px-6 py-4 font-medium text-gray-800">
                      {prod.productName}
                    </td>
                    <td className="px-6 py-4 text-gray-700">
                      {prod.productCategory}
                    </td>
                    <td className="px-6 py-4 text-gray-700">
                      Rp {prod.productPrice.toLocaleString()}
                    </td>
                    <td className="px-6 py-4 text-gray-700">
                      {prod.productRate}
                    </td>
                    <td className="px-6 py-4 space-x-3">
                      <button
                        onClick={() =>
                          navigate(`/dashboardAdmin/edit/${prod.productId}`)
                        }
                        className="text-blue-600 hover:text-blue-800 transition"
                        title="Edit"
                      >
                        <FaEdit />
                      </button>
                      <button
                        onClick={() => handleDelete(prod.productId)}
                        className="text-red-600 hover:text-red-800 transition"
                        title="Delete"
                      >
                        <FaTrash />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default HomeAdmin;
