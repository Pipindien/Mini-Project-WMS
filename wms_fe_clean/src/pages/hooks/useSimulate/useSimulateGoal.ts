import { useState } from "react";
import { SimulateResponse } from "../../../services/simulate/type"; // pastikan path-nya sesuai
import { createSimulateGoal } from "../../../services/simulate/api";

const useSimulateGoal = () => {
  const [data, setData] = useState<SimulateResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const simulate = async (
    goalId: number,
    monthlyInvestment: number,
    token: string
  ) => {
    setLoading(true);
    setError(null);
    try {
      const result = await createSimulateGoal(
        { goalId, monthlyInvestment },
        token
      );
      setData(result);
    } catch (err) {
      setError("Simulasi gagal");
    } finally {
      setLoading(false);
    }
  };

  return { data, loading, error, simulate };
};

export default useSimulateGoal;
