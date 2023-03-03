import { IOrders } from 'app/entities/testChanges/orders/orders.model';
import { IProducts } from 'app/entities/testChanges/products/products.model';

export interface IOrderDetails {
  id: number;
  quantity?: number | null;
  amount?: number | null;
  order?: Pick<IOrders, 'id'> | null;
  product?: Pick<IProducts, 'id'> | null;
}

export type NewOrderDetails = Omit<IOrderDetails, 'id'> & { id: null };
