import { useState, useEffect } from "react";
import { getGoalById } from "../../../services/goal/api";
import { Goal } from "../../../services/goal/type";

const useGoalById = (goalId: string | undefined) => {
  const [goal, setGoal] = useState<Goal | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!goalId) return;

    const fetchGoal = async () => {
      const token = localStorage.getItem("token");
      if (!token) return;
      try {
        const data = await getGoalById(goalId, token);
        setGoal(data);
      } catch (err) {
        setError("Failed to load goal.");
      } finally {
        setLoading(false);
      }
    };

    fetchGoal();
  }, [goalId]);

  return { goal, loading, error };
};

export default useGoalById;
