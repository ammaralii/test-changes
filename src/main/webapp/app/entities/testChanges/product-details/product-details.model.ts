import { IProducts } from 'app/entities/testChanges/products/products.model';

export interface IProductDetails {
  id: number;
  description?: string | null;
  imageUrl?: string | null;
  isavailable?: boolean | null;
  product?: Pick<IProducts, 'id'> | null;
}

export type NewProductDetails = Omit<IProductDetails, 'id'> & { id: null };
