import React, { useState, useEffect } from "react";
import { Product } from "../../services/product/type";
import { getProducts, deleteProduct } from "../../services/product/api";
import { useNavigate } from "react-router-dom";

const HomeAdmin: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const fetchData = async () => {
    try {
      const data = await getProducts();
      setProducts(data);
    } catch (err) {
      setError("Failed to fetch products");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleDelete = async (id: string) => {
    if (!window.confirm("Are you sure you want to delete this product?"))
      return;

    try {
      await deleteProduct(id);
      fetchData();
    } catch (err) {
      alert("Delete failed");
    }
  };

  if (loading)
    return <div className="text-center mt-20 text-red-500">Loading...</div>;
  if (error)
    return <div className="text-center mt-20 text-red-500">{error}</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Product Management</h1>
        <button
          onClick={() => navigate("/dashboardAdmin/create")}
          className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded"
        >
          + Add Product
        </button>
      </div>

      <table className="min-w-full bg-white shadow-md rounded">
        <thead className="bg-gray-100">
          <tr>
            <th className="py-2 px-4 text-left">Name</th>
            <th className="py-2 px-4 text-left">Category</th>
            <th className="py-2 px-4 text-left">Price</th>
            <th className="py-2 px-4 text-left">Rate</th>
            <th className="py-2 px-4 text-left">Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.length === 0 ? (
            <tr>
              <td colSpan={5} className="text-center py-4 text-gray-500">
                Tidak ada produk yang tersedia.
              </td>
            </tr>
          ) : (
            products.map((prod) => (
              <tr key={prod.productId} className="border-b">
                <td className="py-2 px-4">{prod.productName}</td>
                <td className="py-2 px-4">{prod.productCategory}</td>
                <td className="py-2 px-4">
                  Rp {prod.productPrice.toLocaleString()}
                </td>
                <td className="py-2 px-4">{prod.productRate}</td>
                <td className="py-2 px-4 space-x-2">
                  <button
                    onClick={() =>
                      navigate(`/dashboardAdmin/edit/${prod.productId}`)
                    }
                    className="text-blue-600 hover:underline"
                  >
                    Edit
                  </button>

                  <button
                    onClick={() => handleDelete(prod.productId)}
                    className="text-red-600 hover:underline"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default HomeAdmin;
