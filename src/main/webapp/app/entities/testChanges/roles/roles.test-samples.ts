import { IRoles, NewRoles } from './roles.model';

export const sampleWithRequiredData: IRoles = {
  id: 52068,
  rolePrId: 49886,
  name: 'Loan purple',
};

export const sampleWithPartialData: IRoles = {
  id: 32831,
  rolePrId: 63724,
  name: 'Shirt Falls',
};

export const sampleWithFullData: IRoles = {
  id: 38152,
  rolePrId: 32156,
  name: 'National challenge Account',
};

export const sampleWithNewData: NewRoles = {
  rolePrId: 62899,
  name: 'encoding Corners Pound',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
