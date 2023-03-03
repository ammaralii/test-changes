import { IDarazUsers, NewDarazUsers } from './daraz-users.model';

export const sampleWithRequiredData: IDarazUsers = {
  id: 87342,
  fullName: 'Synchronised bandwidth Towels',
  email: 'Vallie84@yahoo.com',
  phone: '644.391.4421',
};

export const sampleWithPartialData: IDarazUsers = {
  id: 1283,
  fullName: 'Rustic Card',
  email: 'Sasha.Pacocha@hotmail.com',
  phone: '1-393-764-4721 x606',
};

export const sampleWithFullData: IDarazUsers = {
  id: 41376,
  fullName: 'Savings',
  email: 'Clarissa_Casper66@yahoo.com',
  phone: '558-413-7586 x9083',
};

export const sampleWithNewData: NewDarazUsers = {
  fullName: 'policy Investment Wyoming',
  email: 'Christ89@hotmail.com',
  phone: '1-745-541-2669',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
