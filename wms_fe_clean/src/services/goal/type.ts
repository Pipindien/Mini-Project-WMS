export interface Goal {
  id: number;
  goalName: string;
  goalId: number;
  custId: number;
  targetAmount: number;
  currentAmount: number;
  targetDate: string; // format ISO string
  riskTolerance: "Conservative" | "Moderate" | "Aggressive"; // sesuai dengan value yang mungkin
  status: "Active" | "Completed" | "Archived"; // sesuaikan kalau backend punya enum lain
  createdDate: string;
  updatedDate: string | null;
  insightMessage: string;
}

export interface SuggestedPortfolioItem {
  category: string;
  percentage: number;
}

export interface Product {
  productId: string;
  categoryId: number;
  productName: string;
  productPrice: number;
  productRate: number;
}

export interface RecommendedProducts {
  [category: string]: Product[]; // dynamic key: "Saham", "Obligasi", etc.
}

export interface PortfolioRecommendationResponse {
  goalId: number;
  goal: Goal;
  suggestedPortfolio: SuggestedPortfolioItem[];
  recommendedProducts: RecommendedProducts;
}

export interface PortfolioDashboardResponse {
  portoId: number | null;
  goalId: number | null;
  custId: number;
  goalName: string;
  totalInvestment: number;
  estimatedReturn: number;
  totalProfit: number;
  returnPercentage: number;
  categoryAllocation: Record<string, number>; // contoh: { "Obligasi": 20.6, "Saham": 9.2 }
  portfolioProductDetails: PortfolioProductDetail[] | null;
}

export interface PortfolioProductDetail {
  custId: number | null;
  goalId: number | null;
  idPortoDetail: number | null;
  productId: number;
  productName: string;
  productCategory: string;
  totalLot: number;
  buyPrice: number;
  productRate: number;
  investmentAmount: number;
  estimatedReturn: number;
  profit: number;
  buyDate: string; // ISO string
}
