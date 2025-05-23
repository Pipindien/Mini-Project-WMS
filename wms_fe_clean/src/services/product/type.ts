export interface Product {
  productId: string;
  productName: string;
  productPrice: number;
  productRate: number;
  categoryId: string;
  productCategory: string;
  createdDate: string;
  description: string;
  imageUrl: string;
}

export interface Category {
  categoryId: string;
  categoryType: string;
}
