import { IAddresses, NewAddresses } from './addresses.model';

export const sampleWithRequiredData: IAddresses = {
  id: 97445,
  street: 'Meagan Throughway',
  city: 'Kundeview',
  state: 'Frozen systemic',
};

export const sampleWithPartialData: IAddresses = {
  id: 37109,
  street: 'Stehr Mount',
  city: 'Lake Meggie',
  state: 'vortals Extended',
};

export const sampleWithFullData: IAddresses = {
  id: 22076,
  street: 'Lou Avenue',
  city: "Lee's Summit",
  state: 'array',
};

export const sampleWithNewData: NewAddresses = {
  street: 'Zemlak Track',
  city: 'Port Loyalhaven',
  state: 'communities Directives',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
