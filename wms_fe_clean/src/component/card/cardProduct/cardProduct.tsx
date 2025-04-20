import React from "react";
import { Product } from "../../../services/product/type";

// Define CardProduct component to accept Product as prop
interface CardProductProps {
  product: Product;
}

const CardProduct: React.FC<CardProductProps> = ({ product }) => {
  return (
    <div className="bg-white p-6 rounded-lg shadow-lg hover:shadow-xl transition-transform transform hover:scale-105">
      <h2 className="text-xl font-bold text-gray-800 mb-4">
        {product.productName}
      </h2>
      <p className="text-lg text-green-600 mb-4">${product.productPrice}</p>
      <p className="text-sm text-yellow-500 mb-2">
        Rate: {product.productRate} /bulan
      </p>
      <p className="text-sm text-gray-600 mb-2">
        Product Category: {product.productCategory}
      </p>
      <p className="text-sm text-gray-500">Created: {product.createdDate}</p>
    </div>
  );
};

export default CardProduct;
