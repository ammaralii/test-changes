import dayjs from 'dayjs/esm';
import { IOrders } from 'app/entities/testChanges/orders/orders.model';
import { IShippingDetails } from 'app/entities/testChanges/shipping-details/shipping-details.model';
import { IDarazUsers } from 'app/entities/testChanges/daraz-users/daraz-users.model';
import { ShippingStatus } from 'app/entities/enumerations/shipping-status.model';

export interface IOrderDelivery {
  id: number;
  deliveryDate?: dayjs.Dayjs | null;
  deliveryCharge?: number | null;
  shippingStatus?: ShippingStatus | null;
  order?: Pick<IOrders, 'id'> | null;
  shippingAddress?: Pick<IShippingDetails, 'id'> | null;
  deliveryManager?: Pick<IDarazUsers, 'id'> | null;
  deliveryBoy?: Pick<IDarazUsers, 'id'> | null;
}

export type NewOrderDelivery = Omit<IOrderDelivery, 'id'> & { id: null };
