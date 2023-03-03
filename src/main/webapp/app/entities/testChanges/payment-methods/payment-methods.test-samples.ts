import { IPaymentMethods, NewPaymentMethods } from './payment-methods.model';

export const sampleWithRequiredData: IPaymentMethods = {
  id: 49490,
  cardNumber: 'Computer Refined supply-chains',
  cardHolderName: 'Arizona',
  expirationDate: 'parsing pr',
};

export const sampleWithPartialData: IPaymentMethods = {
  id: 23785,
  cardNumber: 'salmon',
  cardHolderName: 'application',
  expirationDate: 'Usability ',
};

export const sampleWithFullData: IPaymentMethods = {
  id: 4808,
  cardNumber: 'solutions Manager Account',
  cardHolderName: 'Lead',
  expirationDate: 'pixel Focu',
};

export const sampleWithNewData: NewPaymentMethods = {
  cardNumber: 'Plastic CFP',
  cardHolderName: 'extensible',
  expirationDate: 'connect ca',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
