import axios from "axios";
import { Product } from "./type";

export const getProducts = async (): Promise<Product[]> => {
  const res = await axios.get("http://localhost:8082/product");
  return res.data;
};

export const getProductById = async (id: string): Promise<Product> => {
  const res = await axios.get(`http://localhost:8082/product/get/${id}`);
  return res.data;
};

export const deleteProduct = async (id: string): Promise<void> => {
  await axios.delete(`http://localhost:8082/product/delete/${id}`);
};

export const createProduct = async (product: Partial<Product>) => {
  await axios.post("http://localhost:8082/product/save", product);
};

export const updateProduct = async (
  id: string | number,
  product: Partial<Product>
) => {
  await axios.put(`http://localhost:8082/product/update/${id}`, product);
};

export const getAllCategory = async () => {
  const response = await axios.get("http://localhost:8082/category");
  return response.data;
};
