import dayjs from 'dayjs/esm';
import { IOrders } from 'app/entities/testChanges/orders/orders.model';
import { ShippingMethod } from 'app/entities/enumerations/shipping-method.model';

export interface IShippingDetails {
  id: number;
  shippingAddress?: string | null;
  shippingMethod?: ShippingMethod | null;
  estimatedDeliveryDate?: dayjs.Dayjs | null;
  order?: Pick<IOrders, 'id'> | null;
}

export type NewShippingDetails = Omit<IShippingDetails, 'id'> & { id: null };
