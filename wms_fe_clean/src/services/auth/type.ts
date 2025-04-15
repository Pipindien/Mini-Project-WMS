export interface LoginPayload {
  username: string;
  password: string;
}

export interface UserData {
  custId: number;
  age: number;
  salary: number;
}

export interface LoginResponse {
  token: string;
  custId: number;
  age: number;
  salary: number;
}
