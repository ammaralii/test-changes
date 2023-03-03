import dayjs from 'dayjs/esm';

import { IOrders, NewOrders } from './orders.model';

export const sampleWithRequiredData: IOrders = {
  id: 32144,
  orderDate: dayjs('2023-03-02'),
};

export const sampleWithPartialData: IOrders = {
  id: 82240,
  orderDate: dayjs('2023-03-02'),
};

export const sampleWithFullData: IOrders = {
  id: 75013,
  orderDate: dayjs('2023-03-02'),
  totalAmount: 98840,
};

export const sampleWithNewData: NewOrders = {
  orderDate: dayjs('2023-03-02'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
