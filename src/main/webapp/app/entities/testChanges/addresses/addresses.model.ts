import { IDarazUsers } from 'app/entities/testChanges/daraz-users/daraz-users.model';

export interface IAddresses {
  id: number;
  street?: string | null;
  city?: string | null;
  state?: string | null;
  user?: Pick<IDarazUsers, 'id'> | null;
}

export type NewAddresses = Omit<IAddresses, 'id'> & { id: null };
