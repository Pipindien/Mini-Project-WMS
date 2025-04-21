import axios from "axios";
import goalApi from "../api/goal";
import transactionApi from "../api/transaction";
import { Goal } from "./type";
import { PortfolioRecommendationResponse } from "./type";

export const getGoals = async (
  token: string,
  status?: string
): Promise<Goal[]> => {
  const res = await goalApi.get("/financial-goal/", {
    headers: {
      token: token,
    },
    params: status ? { status } : {},
  });

  return res.data;
};

export const getGoalById = async (
  goalId: string,
  token: string
): Promise<Goal> => {
  const res = await goalApi.get(`/financial-goal/${goalId}`, {
    headers: {
      token: token,
    },
  });

  return res.data;
};

export const createGoal = async (
  payload: { goalName: string; targetAmount: number; targetDate: string },
  token: string
): Promise<Goal> => {
  try {
    // Send the POST request using your custom API wrapper (goalApi)
    const res = await goalApi.post("/financial-goal/save", payload, {
      headers: {
        "Content-Type": "application/json", // Ensure content type is set
        token: token, // Pass token in header
      },
    });
    const data: Goal = res.data; // Access the response body directly

    return data; // Return the goal data
  } catch (err) {
    // Handle errors from Axios
    if (err instanceof axios.AxiosError) {
      // Optional: log or re-throw custom error message based on status
      console.error("Error creating goal:", err.response?.data || err.message);
    }
    throw new Error("Failed to create goal");
  }
};

export const getSuggestedPortfolio = async (
  goalId: string,
  token: string
): Promise<PortfolioRecommendationResponse> => {
  const res = await goalApi.get(
    `/financial-goal/${goalId}/suggested-portfolio`,
    {
      headers: {
        token: token, // gunakan key `token` sesuai backend kamu
      },
    }
  );

  return res.data;
};

export const updateGoal = async (
  goalId: string,
  token: string,
  goal: Partial<Goal>
) => {
  await goalApi.put(`/financial-goal/update/${goalId}`, goal, {
    headers: {
      token: token,
    },
  });
};

export const archiveGoal = async (goalId: string, token: string) => {
  await goalApi.patch(`/financial-goal/archive/${goalId}`, null, {
    headers: {
      token: token,
    },
  });
};

export const getPortfolioByGoalId = async (goalId: string, token: string) => {
  const res = await transactionApi.get(`/portfolio/dashboard/${goalId}`, {
    headers: {
      token: token,
    },
  });
  return res.data;
};

export const getPortfolioDashboard = async (token: string) => {
  const res = await transactionApi.get(`/portfolio/dashboard`, {
    headers: {
      token: token,
    },
  });
  return res.data;
};
