import { IColors } from 'app/entities/testChanges/colors/colors.model';

export interface ICars {
  id: number;
  caruid?: number | null;
  name?: string | null;
  colors?: Pick<IColors, 'id'>[] | null;
}

export type NewCars = Omit<ICars, 'id'> & { id: null };
