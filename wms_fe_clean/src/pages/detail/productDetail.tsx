import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import useProductById from "../hooks/useProduct/useProductById";

const ProductDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { product, loading, error } = useProductById(id);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="w-full max-w-2xl bg-white rounded-lg shadow-md p-6">
          <div className="text-2xl text-gray-500 animate-pulse">Loading...</div>
          <div className="mt-4 space-y-4">
            <div className="h-64 bg-gray-300 rounded-md animate-pulse"></div>
            <div className="space-y-2">
              <div className="h-6 w-3/4 bg-gray-300 rounded-md animate-pulse"></div>
              <div className="h-6 w-1/2 bg-gray-300 rounded-md animate-pulse"></div>
              <div className="h-10 w-full bg-gray-300 rounded-md animate-pulse"></div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-screen bg-gray-100">
        <div className="w-full max-w-2xl bg-white rounded-lg shadow-md p-6">
          <p className="text-2xl text-red-500 text-center">Error: {error}</p>
        </div>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="flex justify-center items-center h-screen bg-gray-100">
        <div className="w-full max-w-2xl bg-white rounded-lg shadow-md p-6">
          <p className="text-2xl text-gray-500 text-center">
            Product not found.
          </p>
        </div>
      </div>
    );
  }

  const handleBuy = () => {
    navigate(`/buy/${product.productId}`);
  };

  const getParaphrasedDescription = (
    description: string | undefined,
    productName: string
  ) => {
    const briefDescription = `${productName} offers a blend of innovative features designed to enhance your investment strategy 📈.  It provides the capability to [Add specific key benefit, e.g., 'optimize portfolio diversification' or 'gain access to emerging markets' 🚀], potentially improving returns 💰.  For investors seeking a versatile solution, ${productName} combines advanced tools 🛠️ with a user-centric approach 😊.`;

    if (description && description.length > 100) {
      const baseDescription = description
        .replace(/This product/gi, productName)
        .replace(/this product/gi, productName)
        .replace(/it is/gi, `${productName} is`)
        .replace(/it has/gi, `${productName} has`)
        .replace(/its/gi, `${productName}'s`)
        .replace(/the product/gi, productName)
        .replace(/a product/gi, productName)
        .replace(/product/gi, productName);
      return baseDescription;
    }
    return briefDescription;
  };

  const paraphrasedDescription = getParaphrasedDescription(
    product.description,
    product.productName
  );

  return (
    <div className="bg-gradient-to-br from-gray-50 to-gray-200 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-lg shadow-xl overflow-hidden">
          <div className="grid md:grid-cols-2 gap-8">
            <div className="bg-gray-100 flex items-center justify-center p-4">
              <img
                src={
                  product.imageUrl ||
                  "https://www.pngkey.com/png/full/52-526880_stock-transparent.png"
                }
                alt={product.productName}
                className="object-contain h-full w-full rounded-lg"
                style={{ maxHeight: "400px" }}
              />
            </div>
            <div className="p-6 flex flex-col justify-between">
              <div>
                <h1
                  className="text-3xl font-bold text-gray-900 mb-4"
                  style={{
                    backgroundImage:
                      "linear-gradient(to right, #6366f1, #8b5cf6)",
                    color: "transparent",
                    backgroundClip: "text",
                  }}
                >
                  {product.productName}
                </h1>
                <p className="text-2xl font-semibold text-indigo-600 mb-6">
                  Rp {product.productPrice.toLocaleString()}
                </p>
                <div className="flex items-center mb-4">
                  <span className="text-gray-700 mr-2">Rating:</span>
                  <span
                    className={
                      product.productRate > 70
                        ? "bg-green-100 text-green-800 border border-green-300 px-3 py-1 rounded-full font-medium"
                        : "bg-yellow-100 text-yellow-800 border border-yellow-300 px-3 py-1 rounded-full font-medium"
                    }
                  >
                    {product.productRate}%
                  </span>
                </div>
                <p className="text-gray-700 leading-relaxed text-md">
                  {paraphrasedDescription}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;
