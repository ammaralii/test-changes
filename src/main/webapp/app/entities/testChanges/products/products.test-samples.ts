import { IProducts, NewProducts } from './products.model';

export const sampleWithRequiredData: IProducts = {
  id: 79224,
  name: 'Switchable Alaska',
};

export const sampleWithPartialData: IProducts = {
  id: 23782,
  name: 'Metal',
};

export const sampleWithFullData: IProducts = {
  id: 96949,
  name: 'Generic Account port',
};

export const sampleWithNewData: NewProducts = {
  name: 'parsing Infrastructure',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
