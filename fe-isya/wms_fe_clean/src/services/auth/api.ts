import authApi from "../api/auth";
import { LoginPayload, LoginResponse } from "./type";

export const login = async (payload: LoginPayload): Promise<LoginResponse> => {
  const response = await authApi.post<LoginResponse>("auth/login", payload);
  return response.data;
};
