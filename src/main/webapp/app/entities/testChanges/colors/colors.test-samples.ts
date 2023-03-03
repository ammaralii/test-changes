import { IColors, NewColors } from './colors.model';

export const sampleWithRequiredData: IColors = {
  id: 35936,
  coloruid: 57013,
  name: 'SDD firewall',
};

export const sampleWithPartialData: IColors = {
  id: 40257,
  coloruid: 83027,
  name: 'panel synthesizing Jewelery',
};

export const sampleWithFullData: IColors = {
  id: 60103,
  coloruid: 47628,
  name: 'Directives 1080p',
};

export const sampleWithNewData: NewColors = {
  coloruid: 59335,
  name: 'Turnpike',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
