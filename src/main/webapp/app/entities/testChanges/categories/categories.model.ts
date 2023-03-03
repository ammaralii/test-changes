export interface ICategories {
  id: number;
  name?: string | null;
  detail?: string | null;
}

export type NewCategories = Omit<ICategories, 'id'> & { id: null };
