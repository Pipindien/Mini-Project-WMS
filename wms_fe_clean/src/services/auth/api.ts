import authApi from "../api/auth";
import { LoginPayload, LoginResponse } from "./type";

export const login = async (payload: LoginPayload): Promise<LoginResponse> => {
  const response = await authApi.post<LoginResponse>("auth/login", payload);
  return response.data;
};

export const getProfile = async (): Promise<LoginResponse> => {
  const token = localStorage.getItem("token");

  const response = await authApi.get<LoginResponse>("auth/", {
    headers: {
      token: token || "",
    },
  });

  return response.data;
};

export const updateBalance = async (
  custId: number,
  amount: number,
  isAddition: boolean,
  token: string
) => {
  const response = await fetch(`http://localhost:8080/auth/balance/${custId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      token,
    },
    body: JSON.stringify({ amount, isAddition }),
  });

  if (!response.ok) {
    throw new Error("Failed to update balance");
  }

  return response.text(); // atau response.json()
};
