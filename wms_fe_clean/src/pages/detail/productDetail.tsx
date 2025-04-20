import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import useProductById from "../hooks/useProduct/useProductById";

const ProductDetail: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { product, loading, error } = useProductById(id);

  const formatCurrency = (value: number) =>
    value.toLocaleString("id-ID", { style: "currency", currency: "IDR" });

  const formatRate = (rate: number) => `${(rate * 100).toFixed(2)}%`;

  if (loading)
    return (
      <div className="text-center mt-20 text-blue-500 font-semibold">
        Loading...
      </div>
    );

  if (error)
    return (
      <div className="text-center mt-20 text-red-500 font-semibold">
        {error}
      </div>
    );

  if (!product)
    return (
      <div className="text-center mt-20 text-gray-500">Product not found.</div>
    );

  const handleBuy = () => {
    navigate(`/buy/${product.productId}`);
  };

  return (
    <div className="container mx-auto px-4 py-10">
      <div className="max-w-3xl mx-auto bg-white rounded-2xl shadow-lg overflow-hidden md:flex">
        {/* Uncomment if you add imageUrl support
        <img
          className="h-64 w-full object-cover md:w-1/3"
          alt={product.productName}
          src={product.imageUrl || "/placeholder.png"}
        />
        */}

        <div className="p-8 md:w-full">
          <h1 className="text-3xl font-bold text-gray-800 mb-2">
            {product.productName}
          </h1>

          <p className="text-xl text-green-600 font-semibold mb-4">
            {formatCurrency(product.productPrice)}
          </p>

          <p className="text-sm text-indigo-600 font-medium mb-2">
            Rate: {formatRate(product.productRate)} /bulan
          </p>

          <p className="text-sm text-gray-500 mb-6">
            Category:{" "}
            <span className="font-medium text-gray-700">
              {product.productCategory}
            </span>
          </p>

          <button
            onClick={handleBuy}
            className="px-6 py-2 bg-green-600 text-white rounded-full font-semibold hover:bg-green-700 transition"
          >
            Beli Sekarang
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;
