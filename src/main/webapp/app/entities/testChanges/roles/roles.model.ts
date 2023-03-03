import { IDarazUsers } from 'app/entities/testChanges/daraz-users/daraz-users.model';

export interface IRoles {
  id: number;
  rolePrId?: number | null;
  name?: string | null;
  users?: Pick<IDarazUsers, 'id'>[] | null;
}

export type NewRoles = Omit<IRoles, 'id'> & { id: null };
