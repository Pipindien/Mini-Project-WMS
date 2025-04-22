import React from "react";
import { Product } from "../../../services/product/type";

interface CardProductProps {
  product: Product;
}

const CardProduct: React.FC<CardProductProps> = ({ product }) => {
  const formatCurrency = (value: number) =>
    value.toLocaleString("id-ID", { style: "currency", currency: "IDR" });

  const formatRate = (rate: number) => `${(rate * 100).toFixed(2)}%`;

  return (
    <div className="bg-white p-6 rounded-2xl shadow-md hover:shadow-xl transition duration-300 transform hover:-translate-y-1">
      <h2 className="text-2xl font-semibold text-gray-800 mb-2">
        {product.productName}
      </h2>

      <p className="text-xl text-green-600 font-bold mb-1">
        {formatCurrency(product.productPrice)}
      </p>

      <p className="text-sm text-indigo-600 mb-2">
        Rate:{" "}
        <span className="font-medium">{formatRate(product.productRate)}</span>{" "}
        /bulan
      </p>

      <span className="inline-block px-3 py-1 text-xs font-semibold text-yellow-800 bg-yellow-100 rounded-full mb-3">
        {product.productCategory}
      </span>

      <p className="text-xs text-gray-500">
        Created on:{" "}
        {new Date(product.createdDate).toLocaleDateString("id-ID", {
          year: "numeric",
          month: "long",
          day: "numeric",
        })}
      </p>
    </div>
  );
};

export default CardProduct;
