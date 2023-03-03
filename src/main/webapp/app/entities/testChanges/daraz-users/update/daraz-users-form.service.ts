import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IDarazUsers, NewDarazUsers } from '../daraz-users.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDarazUsers for edit and NewDarazUsersFormGroupInput for create.
 */
type DarazUsersFormGroupInput = IDarazUsers | PartialWithRequiredKeyOf<NewDarazUsers>;

type DarazUsersFormDefaults = Pick<NewDarazUsers, 'id' | 'roles'>;

type DarazUsersFormGroupContent = {
  id: FormControl<IDarazUsers['id'] | NewDarazUsers['id']>;
  fullName: FormControl<IDarazUsers['fullName']>;
  email: FormControl<IDarazUsers['email']>;
  phone: FormControl<IDarazUsers['phone']>;
  manager: FormControl<IDarazUsers['manager']>;
  roles: FormControl<IDarazUsers['roles']>;
};

export type DarazUsersFormGroup = FormGroup<DarazUsersFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DarazUsersFormService {
  createDarazUsersFormGroup(darazUsers: DarazUsersFormGroupInput = { id: null }): DarazUsersFormGroup {
    const darazUsersRawValue = {
      ...this.getFormDefaults(),
      ...darazUsers,
    };
    return new FormGroup<DarazUsersFormGroupContent>({
      id: new FormControl(
        { value: darazUsersRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      fullName: new FormControl(darazUsersRawValue.fullName, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      email: new FormControl(darazUsersRawValue.email, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      phone: new FormControl(darazUsersRawValue.phone, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      manager: new FormControl(darazUsersRawValue.manager),
      roles: new FormControl(darazUsersRawValue.roles ?? []),
    });
  }

  getDarazUsers(form: DarazUsersFormGroup): IDarazUsers | NewDarazUsers {
    return form.getRawValue() as IDarazUsers | NewDarazUsers;
  }

  resetForm(form: DarazUsersFormGroup, darazUsers: DarazUsersFormGroupInput): void {
    const darazUsersRawValue = { ...this.getFormDefaults(), ...darazUsers };
    form.reset(
      {
        ...darazUsersRawValue,
        id: { value: darazUsersRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DarazUsersFormDefaults {
    return {
      id: null,
      roles: [],
    };
  }
}
