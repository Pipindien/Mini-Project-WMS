import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import useProductById from "../hooks/useProduct/useProductById";

const ProductDetail: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { product, loading, error } = useProductById(id);

  if (loading)
    return <div className="text-center mt-20 text-red-500">Loading...</div>;
  if (error)
    return <div className="text-center mt-20 text-red-500">{error}</div>;
  if (!product)
    return (
      <div className="text-center mt-20 text-gray-500">Product not found.</div>
    );

  const handleBuy = () => {
    navigate(`/buy/${product.productId}`);
  };

  return (
    <div className="container mx-auto p-6">
      <div className="max-w-3xl mx-auto bg-white rounded-xl shadow-md overflow-hidden md:flex">
        {/* <img
          className="h-64 w-full object-cover md:w-1/3"
          alt={product.productName}
          src={product.imageUrl || "/placeholder.png"} // tambahkan image jika ada
        /> */}
        <div className="p-8 md:w-2/3">
          <h2 className="text-2xl font-bold mb-4">{product.productName}</h2>
          <p className="text-lg font-semibold text-blue-600">
            Rp {product.productPrice.toLocaleString()}
          </p>
          <p className="text-md text-gray-600 mt-2">
            Rate: {product.productRate}%
          </p>

          <button
            onClick={handleBuy}
            className="mt-6 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
          >
            Beli
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;
