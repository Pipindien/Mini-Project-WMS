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
