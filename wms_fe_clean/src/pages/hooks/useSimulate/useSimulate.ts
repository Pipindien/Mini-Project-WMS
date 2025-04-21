import { useState } from "react";
import { createSimulateProduct } from "../../../services/simulate/api";
import { SimulateResponse } from "../../../services/simulate/type";
interface SimulatePayload {
  productId: number;
  monthlyInvestment: number;
  years: number;
}

const useSimulateProduct = () => {
  const [data, setData] = useState<SimulateResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const simulate = async (payload: SimulatePayload, token: string) => {
    setLoading(true);
    setError(null);
    console.log("Payload yang dikirim:", payload); // Cek data yang dikirim
    try {
      const result = await createSimulateProduct(payload, token);
      console.log("Hasil simulasi:", result); // Cek hasil yang diterima
      setData(result);
    } catch (err: any) {
      console.error("Error simulasi:", err); // Cek error yang diterima
      setError(err?.message || "Terjadi kesalahan saat simulasi");
    } finally {
      setLoading(false);
    }
  };

  return { data, loading, error, simulate };
};

export default useSimulateProduct;
