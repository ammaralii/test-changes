export interface ICustomers {
  id: number;
  fullName?: string | null;
  email?: string | null;
  phone?: string | null;
}

export type NewCustomers = Omit<ICustomers, 'id'> & { id: null };
