import { ICars } from 'app/entities/testChanges/cars/cars.model';

export interface IColors {
  id: number;
  coloruid?: number | null;
  name?: string | null;
  cars?: Pick<ICars, 'id'>[] | null;
}

export type NewColors = Omit<IColors, 'id'> & { id: null };
