import axios from "axios";

const transactionApi = axios.create({
  baseURL: import.meta.env.VITE_TRANSACTION_API,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

export default transactionApi;
