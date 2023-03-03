import { IProductDetails, NewProductDetails } from './product-details.model';

export const sampleWithRequiredData: IProductDetails = {
  id: 30963,
  description: 'Cotton Massachusetts Fiji',
  imageUrl: 'Ergonomic',
  isavailable: true,
};

export const sampleWithPartialData: IProductDetails = {
  id: 64201,
  description: 'IB Future override',
  imageUrl: 'sky',
  isavailable: false,
};

export const sampleWithFullData: IProductDetails = {
  id: 94214,
  description: 'Ecuador Analyst',
  imageUrl: 'Kentucky Account',
  isavailable: false,
};

export const sampleWithNewData: NewProductDetails = {
  description: 'deposit calculate Lebanon',
  imageUrl: 'Platinum orange',
  isavailable: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
