import axios from "axios";
import goalApi from "../api/goal";
import transactionApi from "../api/transaction";
import { Goal, ApiResponse } from "./type";
import { PortfolioRecommendationResponse } from "./type";

export const getGoals = async (
  token: string,
  status?: string
): Promise<Goal[]> => {
  const res = await goalApi.get<ApiResponse<Goal[]>>("/financial-goal/", {
    headers: {
      token,
    },
    params: status ? { status } : {},
  });

  return res.data.data;
};
// Get goal by ID
export const getGoalById = async (
  goalId: string,
  token: string
): Promise<Goal> => {
  const res = await goalApi.get<ApiResponse<Goal>>(
    `/financial-goal/${goalId}`,
    {
      headers: { token },
    }
  );

  return res.data.data; // ✅ ambil hanya data-nya
};

// Create goal
export const createGoal = async (
  payload: { goalName: string; targetAmount: number; targetDate: string },
  token: string
): Promise<Goal> => {
  try {
    const res = await goalApi.post<ApiResponse<Goal>>(
      "/financial-goal/save",
      payload,
      {
        headers: {
          "Content-Type": "application/json",
          token,
        },
      }
    );

    return res.data.data; // ✅ return hanya data goal-nya
  } catch (err) {
    if (err instanceof axios.AxiosError) {
      console.error("Error creating goal:", err.response?.data || err.message);
    }
    throw new Error("Failed to create goal");
  }
};

// Get suggested portfolio
export const getSuggestedPortfolio = async (
  goalId: string,
  token: string
): Promise<PortfolioRecommendationResponse> => {
  const res = await goalApi.get<ApiResponse<PortfolioRecommendationResponse>>(
    `/financial-goal/${goalId}/suggested-portfolio`,
    {
      headers: { token },
    }
  );

  return res.data.data; // ✅ ambil hanya data-nya
};

// Update goal
export const updateGoal = async (
  goalId: string,
  token: string,
  goal: Partial<Goal>
): Promise<void> => {
  await goalApi.put(`/financial-goal/update/${goalId}`, goal, {
    headers: { token },
  });
};

// Archive goal
export const archiveGoal = async (
  goalId: string,
  token: string
): Promise<void> => {
  await goalApi.patch(`/financial-goal/archive/${goalId}`, null, {
    headers: { token },
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
