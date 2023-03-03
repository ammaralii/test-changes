import { ICustomers, NewCustomers } from './customers.model';

export const sampleWithRequiredData: ICustomers = {
  id: 77988,
  fullName: 'Money',
  email: 'Antonio.Christiansen@hotmail.com',
  phone: '1-885-868-1271 x3688',
};

export const sampleWithPartialData: ICustomers = {
  id: 16909,
  fullName: 'Clothing repurpose',
  email: 'Braxton_McClure78@hotmail.com',
  phone: '365.741.1232',
};

export const sampleWithFullData: ICustomers = {
  id: 46271,
  fullName: 'directional infrastructures',
  email: 'Tamara24@hotmail.com',
  phone: '662.376.5236',
};

export const sampleWithNewData: NewCustomers = {
  fullName: 'group Towels connect',
  email: 'Mateo.Erdman52@hotmail.com',
  phone: '337.705.3983',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
