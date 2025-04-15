export interface LoginPayload {
  username: string;
  password: string;
}

export interface UserData {
  custId: number;
  age: number;
  salary: number;
  role: "ADMIN" | "USER";
}

export interface LoginResponse {
  token: string;
  custId: number;
  age: number;
  salary: number;
}

export interface RegisterPayload {
  fullName: string;
  email: string;
  phone: string;
  age: number;
  salary: number;
  username: string;
  password: string;
}
