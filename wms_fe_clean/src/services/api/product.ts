import axios from "axios";

const productApi = axios.create({
  baseURL: import.meta.env.VITE_PRODUCT_API,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

export default productApi;
