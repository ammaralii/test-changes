import { ICategories, NewCategories } from './categories.model';

export const sampleWithRequiredData: ICategories = {
  id: 71041,
  name: 'withdrawal Towels 6th',
  detail: 'base driver',
};

export const sampleWithPartialData: ICategories = {
  id: 88896,
  name: 'Regional pink',
  detail: 'cyan',
};

export const sampleWithFullData: ICategories = {
  id: 51920,
  name: 'capability IB Forward',
  detail: 'Unbranded programming Bedfordshire',
};

export const sampleWithNewData: NewCategories = {
  name: 'Industrial',
  detail: 'payment Utah input',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
