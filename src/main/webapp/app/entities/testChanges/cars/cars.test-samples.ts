import { ICars, NewCars } from './cars.model';

export const sampleWithRequiredData: ICars = {
  id: 68426,
  caruid: 86118,
  name: 'dedicated neural Renminbi',
};

export const sampleWithPartialData: ICars = {
  id: 34151,
  caruid: 65721,
  name: 'Valley',
};

export const sampleWithFullData: ICars = {
  id: 8246,
  caruid: 14515,
  name: 'compressing Auto mint',
};

export const sampleWithNewData: NewCars = {
  caruid: 54830,
  name: 'Fuerte Loan',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
