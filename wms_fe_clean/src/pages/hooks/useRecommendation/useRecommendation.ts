import { useEffect, useState } from "react";
import { getSuggestedPortfolio } from "../../../services/goal/api";
import { PortfolioRecommendationResponse } from "../../../services/goal/type";

export const useSuggestedPortfolio = (goalId: string, token: string) => {
  const [data, setData] = useState<PortfolioRecommendationResponse | null>(
    null
  );
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!goalId || !token) return;

    const fetchData = async () => {
      setLoading(true);
      try {
        const res = await getSuggestedPortfolio(goalId, token);
        setData(res);
        setError(null);
      } catch (err: any) {
        setError(err?.response?.data?.message || "Something went wrong");
        setData(null);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [goalId, token]);

  return { data, loading, error };
};
