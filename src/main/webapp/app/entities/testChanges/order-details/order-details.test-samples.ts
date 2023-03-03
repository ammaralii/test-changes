import { IOrderDetails, NewOrderDetails } from './order-details.model';

export const sampleWithRequiredData: IOrderDetails = {
  id: 38246,
};

export const sampleWithPartialData: IOrderDetails = {
  id: 18925,
  quantity: 31715,
};

export const sampleWithFullData: IOrderDetails = {
  id: 5009,
  quantity: 41207,
  amount: 4571,
};

export const sampleWithNewData: NewOrderDetails = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
