import { IRoles } from 'app/entities/testChanges/roles/roles.model';

export interface IDarazUsers {
  id: number;
  fullName?: string | null;
  email?: string | null;
  phone?: string | null;
  manager?: Pick<IDarazUsers, 'id'> | null;
  roles?: Pick<IRoles, 'id'>[] | null;
}

export type NewDarazUsers = Omit<IDarazUsers, 'id'> & { id: null };
