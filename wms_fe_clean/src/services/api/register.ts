import authApi from "../api/auth";
import { RegisterPayload } from "../auth/type";

export const register = async (payload: RegisterPayload) => {
  const response = await authApi.post("/auth/register", payload);
  return response.data;
};
