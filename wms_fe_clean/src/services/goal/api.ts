import goalApi from "../api/goal";
import { Goal } from "./type";

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
