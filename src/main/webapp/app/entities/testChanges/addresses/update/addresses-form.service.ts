import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IAddresses, NewAddresses } from '../addresses.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAddresses for edit and NewAddressesFormGroupInput for create.
 */
type AddressesFormGroupInput = IAddresses | PartialWithRequiredKeyOf<NewAddresses>;

type AddressesFormDefaults = Pick<NewAddresses, 'id'>;

type AddressesFormGroupContent = {
  id: FormControl<IAddresses['id'] | NewAddresses['id']>;
  street: FormControl<IAddresses['street']>;
  city: FormControl<IAddresses['city']>;
  state: FormControl<IAddresses['state']>;
  user: FormControl<IAddresses['user']>;
};

export type AddressesFormGroup = FormGroup<AddressesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AddressesFormService {
  createAddressesFormGroup(addresses: AddressesFormGroupInput = { id: null }): AddressesFormGroup {
    const addressesRawValue = {
      ...this.getFormDefaults(),
      ...addresses,
    };
    return new FormGroup<AddressesFormGroupContent>({
      id: new FormControl(
        { value: addressesRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      street: new FormControl(addressesRawValue.street, {
        validators: [Validators.required, Validators.maxLength(30)],
      }),
      city: new FormControl(addressesRawValue.city, {
        validators: [Validators.required, Validators.maxLength(30)],
      }),
      state: new FormControl(addressesRawValue.state, {
        validators: [Validators.required, Validators.maxLength(30)],
      }),
      user: new FormControl(addressesRawValue.user, {
        validators: [Validators.required],
      }),
    });
  }

  getAddresses(form: AddressesFormGroup): IAddresses | NewAddresses {
    return form.getRawValue() as IAddresses | NewAddresses;
  }

  resetForm(form: AddressesFormGroup, addresses: AddressesFormGroupInput): void {
    const addressesRawValue = { ...this.getFormDefaults(), ...addresses };
    form.reset(
      {
        ...addressesRawValue,
        id: { value: addressesRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AddressesFormDefaults {
    return {
      id: null,
    };
  }
}
