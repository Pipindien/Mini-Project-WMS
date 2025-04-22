import { SimulateResponse } from "./type"; // pastikan path-nya sesuai ya
import transactionApi from "../api/transaction";

export const createSimulateProduct = async (
  payload: {
    productId: number;
    monthlyInvestment: number;
    years: number;
  },
  token: string
): Promise<SimulateResponse> => {
  try {
    const res = await transactionApi.post(
      "api/insights/simulate-product",
      payload,
      {
        headers: {
          "Content-Type": "application/json",
          token,
        },
      }
    );
    return res.data;
  } catch {
    throw new Error("Failed to simulate product");
  }
};

export const createSimulateGoal = async (
  payload: {
    goalId: number;
    monthlyInvestment: number;
  },
  token: string
): Promise<SimulateResponse> => {
  try {
    const res = await transactionApi.post("api/insights/simulate", payload, {
      headers: {
        "Content-Type": "application/json",
        token,
      },
    });
    console.log("Simulation result:", res.data); // ⬅️ Tambah ini
    return res.data;
  } catch {
    throw new Error("Failed to simulate product");
    console.error("Simulate error:", Error); // ⬅️ Tambah ini
  }
};
