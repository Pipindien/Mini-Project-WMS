export interface BuyTransactionRequest {
  amount: number;
  productName: string;
  goalName: string;
  notes: string;
}

export interface BuyTransactionResponse {
  status: string;
  amount: number;
  custId: number;
  productId: number;
  goalId: number;
  productPrice: number;
  lot: number;
  notes: string;
}
