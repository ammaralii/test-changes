import { ICustomers } from 'app/entities/testChanges/customers/customers.model';

export interface IPaymentMethods {
  id: number;
  cardNumber?: string | null;
  cardHolderName?: string | null;
  expirationDate?: string | null;
  customer?: Pick<ICustomers, 'id'> | null;
}

export type NewPaymentMethods = Omit<IPaymentMethods, 'id'> & { id: null };
