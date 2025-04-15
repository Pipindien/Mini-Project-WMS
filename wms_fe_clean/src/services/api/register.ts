import authApi from "../api/auth";

export interface RegisterPayload {
  fullName: string;
  email: string;
  phone: string;
  age: number;
  salary: number;
  username: string;
  password: string;
}

export const register = async (payload: RegisterPayload) => {
  const response = await authApi.post("/auth/register", payload);
  return response.data;
};
