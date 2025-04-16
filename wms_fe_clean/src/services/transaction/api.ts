import axios from "axios";
import transactionApi from "../api/transaction";
import { BuyTransactionRequest, BuyTransactionResponse } from "./type";

export const buyTransaction = async (
  data: BuyTransactionRequest,
  token: string
): Promise<BuyTransactionResponse> => {
  const response = await transactionApi.post("/transaction/buy", data, {
    headers: {
      token, // sesuai yang diminta backend
    },
  });
  return response.data;
};

export const updateTransaction = async (
  trxNumber: string,
  request: BuyTransactionRequest,
  token: string
): Promise<BuyTransactionResponse> => {
  const response = await axios.put(
    `${import.meta.env.VITE_API_URL}/transaction/update/${trxNumber}`,
    request,
    {
      headers: {
        token,
      },
    }
  );
  return response.data;
};
