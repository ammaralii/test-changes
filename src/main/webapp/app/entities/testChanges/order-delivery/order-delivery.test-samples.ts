import dayjs from 'dayjs/esm';

import { ShippingStatus } from 'app/entities/enumerations/shipping-status.model';

import { IOrderDelivery, NewOrderDelivery } from './order-delivery.model';

export const sampleWithRequiredData: IOrderDelivery = {
  id: 21035,
  shippingStatus: ShippingStatus['Cancelled'],
};

export const sampleWithPartialData: IOrderDelivery = {
  id: 50309,
  deliveryDate: dayjs('2023-03-03'),
  deliveryCharge: 38313,
  shippingStatus: ShippingStatus['Pending'],
};

export const sampleWithFullData: IOrderDelivery = {
  id: 88882,
  deliveryDate: dayjs('2023-03-02'),
  deliveryCharge: 15889,
  shippingStatus: ShippingStatus['Cancelled'],
};

export const sampleWithNewData: NewOrderDelivery = {
  shippingStatus: ShippingStatus['Pending'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
