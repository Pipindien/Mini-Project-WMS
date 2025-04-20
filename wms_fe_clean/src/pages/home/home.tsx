import React from "react";
import { Link } from "react-router-dom";
import useProducts from "../hooks/useProduct/useProduct";
import CardProduct from "../../component/card/cardProduct/cardProduct";

const Home: React.FC = () => {
  const { products, loading, error } = useProducts();

  if (loading) {
    return (
      <div className="text-center text-xl text-red-500 mt-20">Loading...</div>
    );
  }

  if (error) {
    return (
      <div className="text-center text-xl text-red-500 mt-20">{error}</div>
    );
  }

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-4xl font-bold text-center text-gray-800 mb-10">
        Products List
      </h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {products.map((product) => (
          <Link key={product.productId} to={`/product/${product.productId}`}>
            <CardProduct product={product} />
          </Link>
        ))}
      </div>
    </div>
  );
};

export default Home;
