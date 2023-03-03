import dayjs from 'dayjs/esm';
import { ICustomers } from 'app/entities/testChanges/customers/customers.model';

export interface IOrders {
  id: number;
  orderDate?: dayjs.Dayjs | null;
  totalAmount?: number | null;
  customer?: Pick<ICustomers, 'id'> | null;
}

export type NewOrders = Omit<IOrders, 'id'> & { id: null };
