import dayjs from 'dayjs/esm';

import { ShippingMethod } from 'app/entities/enumerations/shipping-method.model';

import { IShippingDetails, NewShippingDetails } from './shipping-details.model';

export const sampleWithRequiredData: IShippingDetails = {
  id: 24777,
  shippingAddress: 'Chicken',
  shippingMethod: ShippingMethod['COD'],
  estimatedDeliveryDate: dayjs('2023-03-02'),
};

export const sampleWithPartialData: IShippingDetails = {
  id: 33709,
  shippingAddress: 'Card Fresh',
  shippingMethod: ShippingMethod['COD'],
  estimatedDeliveryDate: dayjs('2023-03-03'),
};

export const sampleWithFullData: IShippingDetails = {
  id: 55068,
  shippingAddress: 'foreground reintermediate',
  shippingMethod: ShippingMethod['CARD'],
  estimatedDeliveryDate: dayjs('2023-03-03'),
};

export const sampleWithNewData: NewShippingDetails = {
  shippingAddress: 'protocol Berkshire GB',
  shippingMethod: ShippingMethod['CARD'],
  estimatedDeliveryDate: dayjs('2023-03-02'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
