import { useEffect, useState } from "react";
import { BuyTransactionResponse } from "../../../services/transaction/type";
import { getMyTransactions } from "../../../services/transaction/api";
export const useTransactions = (status: string) => {
  const [transactions, setTransactions] = useState<BuyTransactionResponse[]>(
    []
  );
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        const token = localStorage.getItem("token") || "";
        const allTransactions = await getMyTransactions(token, status);
        // Filter berdasarkan status yang diberikan
        const filtered = allTransactions.filter((trx) => trx.status === status);
        setTransactions(filtered);
      } catch (err) {
        setError("Gagal mengambil data transaksi");
      } finally {
        setLoading(false);
      }
    };

    fetchTransactions();
  }, [status]);

  return { transactions, loading, error };
};
