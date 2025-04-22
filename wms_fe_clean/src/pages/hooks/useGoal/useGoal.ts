import { useState, useEffect } from "react";
import { getGoals } from "../../../services/goal/api";

const useGoal = () => {
  const [goals, setGoals] = useState<any[]>([]);
  const [goalsLoading, setGoalsLoading] = useState<boolean>(true);
  const [goalsError, setGoalsError] = useState<string | null>(null);

  useEffect(() => {
    const fetchGoals = async () => {
      const token = localStorage.getItem("token");
      if (!token) return;

      try {
        const data = await getGoals(token);
        setGoals(data);
        setGoalsLoading(false);
      } catch (err) {
        setGoalsError("Gagal mengambil data tujuan keuangan.");
        setGoalsLoading(false);
        console.error("Failed to fetch goals", err);
      }
    };

    fetchGoals();
  }, []);

  return { goals, goalsLoading, goalsError };
};

export default useGoal;
