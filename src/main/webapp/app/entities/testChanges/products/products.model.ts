import { ICategories } from 'app/entities/testChanges/categories/categories.model';

export interface IProducts {
  id: number;
  name?: string | null;
  category?: Pick<ICategories, 'id'> | null;
}

export type NewProducts = Omit<IProducts, 'id'> & { id: null };
