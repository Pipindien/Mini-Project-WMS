export interface BuyTransactionRequest {
  amount: number;
  productName: string;
  goalName: string;
  notes: string;
}

export interface BuyTransactionResponse {
  trxNumber: string;
  status: string;
  amount: number;
  custId: number;
  productId: number;
  productName: string;
  goalName: string;
  goalId: number;
  productPrice: number;
  lot: number;
  notes: string;
  createdDate: string;
}
