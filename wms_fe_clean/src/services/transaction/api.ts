import axios from "axios";
import transactionApi from "../api/transaction";
import { BuyTransactionRequest, BuyTransactionResponse } from "./type";

export const buyTransaction = async (
  data: BuyTransactionRequest,
  token: string
): Promise<BuyTransactionResponse> => {
  const response = await transactionApi.post("/transaction/buy", data, {
    headers: {
      token,
    },
  });
  return response.data;
};

export const updateTransaction = async (
  trxNumber: string,
  request: BuyTransactionRequest,
  token: string
): Promise<BuyTransactionResponse> => {
  try {
    const response = await axios.put(
      `${import.meta.env.VITE_TRANSACTION_API}/transaction/update/${trxNumber}`,
      request,
      {
        headers: {
          token,
        },
      }
    );

    if (!response.data || !response.data.status) {
      throw new Error("Respon tidak valid dari server.");
    }

    return response.data;
  } catch (error: any) {
    // Tampilkan detail dari response error kalau ada
    if (error.response) {
      console.error("Error response:", error.response.data);
      throw new Error(
        error.response.data?.message || "Gagal mengupdate transaksi."
      );
    } else if (error.request) {
      console.error("No response received:", error.request);
      throw new Error("Tidak ada respons dari server.");
    } else {
      console.error("Error lainnya:", error.message);
      throw new Error("Terjadi kesalahan saat update transaksi.");
    }
  }
};

export const getTransaction = async (
  trxNumber: string,
  token: string
): Promise<BuyTransactionResponse> => {
  const response = await axios.get(
    `${import.meta.env.VITE_TRANSACTION_API}/transaction/${trxNumber}`,
    {
      headers: {
        token,
      },
    }
  );
  return response.data;
};

export const getMyTransactions = async (
  token: string,
  status: string
): Promise<BuyTransactionResponse[]> => {
  const response = await axios.get(
    `${import.meta.env.VITE_TRANSACTION_API}/transaction/history`, // ⬅ sesuai dengan @GetMapping
    {
      headers: {
        token,
      },
      params: {
        status, // ⬅ query param ?status=...
      },
    }
  );
  return response.data;
};
