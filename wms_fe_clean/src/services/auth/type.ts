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
  balance: number;
  role: string;
  fullname: string;
  email: string;
  phone: string;
}

export interface RegisterPayload {
  fullName: string;
  email: string;
  phone: string;
  age: number;
  salary: number;
  balance: number;
  username: string;
  password: string;
}
