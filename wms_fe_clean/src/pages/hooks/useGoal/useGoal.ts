import { useState, useEffect } from "react";
import { getGoals } from "../../../services/goal/api";
import { Goal } from "../../../services/goal/type";

const useGoal = () => {
  const [goals, setGoals] = useState<Goal[]>([]);
  const [goalsLoading, setGoalsLoading] = useState<boolean>(true);
  const [goalsError, setGoalsError] = useState<string | null>(null);

  useEffect(() => {
    const fetchGoals = async () => {
      const token = localStorage.getItem("token");
      if (!token) return;

      try {
        const data = await getGoals(token);
        setGoals(data); // data now typed as Goal[]
      } catch (err) {
        setGoalsError("Gagal mengambil data tujuan keuangan.");
        console.error("Failed to fetch goals", err);
      } finally {
        setGoalsLoading(false);
      }
    };

    fetchGoals();
  }, []);

  return { goals, goalsLoading, goalsError };
};

export default useGoal;
