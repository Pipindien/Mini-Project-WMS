import { useState, useEffect } from "react";
import { getGoalById } from "../../../services/goal/api";
import { Goal } from "../../../services/goal/type";

const useGoalById = (goalId: string | undefined) => {
  const [goal, setGoal] = useState<Goal | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");

  useEffect(() => {
    if (!goalId) return;

    const fetchGoal = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        setError("Token not found");
        setLoading(false);
        return;
      }

      try {
        const data = await getGoalById(goalId, token); // ✅ sudah ambil dari .data.data di API
        setGoal(data);
      } catch (err) {
        setError("Failed to load goal.");
        console.error("Error fetching goal by ID:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchGoal();
  }, [goalId]);

  return { goal, loading, error };
};

export default useGoalById;
