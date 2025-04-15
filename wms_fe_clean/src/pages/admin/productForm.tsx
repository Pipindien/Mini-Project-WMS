import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  createProduct,
  getAllCategory,
  getProductById,
  updateProduct,
} from "../../services/product/api";
import { Product } from "../../services/product/type";

type Category = {
  categoryId: number;
  categoryType: string;
};

const ProductForm: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);

  const [form, setForm] = useState<Partial<Product>>({
    productName: "",
    productPrice: 0,
    productRate: 0,
    categoryId: undefined,
  });

  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    getAllCategory()
      .then(setCategories)
      .catch(() => setError("Failed to load categories"));

    if (isEdit && id) {
      setLoading(true);
      getProductById(id)
        .then((data) => {
          setForm({
            productName: data.productName,
            productPrice: data.productPrice,
            productRate: data.productRate,
            categoryId: data.categoryId,
          });
        })
        .catch(() => setError("Failed to load product"))
        .finally(() => setLoading(false));
    }
  }, [id, isEdit]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]:
        name === "productPrice" || name === "productRate"
          ? Number(value)
          : name === "categoryId"
          ? parseInt(value, 10)
          : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      if (isEdit && id) {
        await updateProduct(id, form);
      } else {
        await createProduct(form);
      }
      navigate("/admin");
    } catch {
      setError("Failed to save product");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-xl mx-auto p-6 bg-white rounded shadow mt-10">
      <h2 className="text-2xl font-bold mb-4">
        {isEdit ? "Edit" : "Create"} Product
      </h2>
      {error && <p className="text-red-500 mb-2">{error}</p>}
      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          type="text"
          name="productName"
          placeholder="Product Name"
          value={form.productName}
          onChange={handleChange}
          className="w-full border p-2 rounded"
          required
        />
        <input
          type="number"
          name="productPrice"
          placeholder="Price"
          value={form.productPrice}
          onChange={handleChange}
          className="w-full border p-2 rounded"
          required
        />
        <input
          type="number"
          name="productRate"
          placeholder="Rate"
          value={form.productRate}
          onChange={handleChange}
          className="w-full border p-2 rounded"
          required
        />
        <select
          name="categoryId"
          value={form.categoryId?.toString() ?? ""}
          onChange={handleChange}
          className="w-full border p-2 rounded"
          required
        >
          <option value="">-- Select Category --</option>
          {categories.map((cat) => (
            <option key={cat.categoryId} value={cat.categoryId}>
              {cat.categoryType}
            </option>
          ))}
        </select>
        <button
          type="submit"
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          disabled={loading}
        >
          {loading ? "Saving..." : isEdit ? "Update" : "Create"}
        </button>
      </form>
    </div>
  );
};

export default ProductForm;
