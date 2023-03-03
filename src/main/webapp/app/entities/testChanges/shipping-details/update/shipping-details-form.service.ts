import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IShippingDetails, NewShippingDetails } from '../shipping-details.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IShippingDetails for edit and NewShippingDetailsFormGroupInput for create.
 */
type ShippingDetailsFormGroupInput = IShippingDetails | PartialWithRequiredKeyOf<NewShippingDetails>;

type ShippingDetailsFormDefaults = Pick<NewShippingDetails, 'id'>;

type ShippingDetailsFormGroupContent = {
  id: FormControl<IShippingDetails['id'] | NewShippingDetails['id']>;
  shippingAddress: FormControl<IShippingDetails['shippingAddress']>;
  shippingMethod: FormControl<IShippingDetails['shippingMethod']>;
  estimatedDeliveryDate: FormControl<IShippingDetails['estimatedDeliveryDate']>;
  order: FormControl<IShippingDetails['order']>;
};

export type ShippingDetailsFormGroup = FormGroup<ShippingDetailsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ShippingDetailsFormService {
  createShippingDetailsFormGroup(shippingDetails: ShippingDetailsFormGroupInput = { id: null }): ShippingDetailsFormGroup {
    const shippingDetailsRawValue = {
      ...this.getFormDefaults(),
      ...shippingDetails,
    };
    return new FormGroup<ShippingDetailsFormGroupContent>({
      id: new FormControl(
        { value: shippingDetailsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      shippingAddress: new FormControl(shippingDetailsRawValue.shippingAddress, {
        validators: [Validators.required, Validators.maxLength(32)],
      }),
      shippingMethod: new FormControl(shippingDetailsRawValue.shippingMethod, {
        validators: [Validators.required],
      }),
      estimatedDeliveryDate: new FormControl(shippingDetailsRawValue.estimatedDeliveryDate, {
        validators: [Validators.required],
      }),
      order: new FormControl(shippingDetailsRawValue.order, {
        validators: [Validators.required],
      }),
    });
  }

  getShippingDetails(form: ShippingDetailsFormGroup): IShippingDetails | NewShippingDetails {
    return form.getRawValue() as IShippingDetails | NewShippingDetails;
  }

  resetForm(form: ShippingDetailsFormGroup, shippingDetails: ShippingDetailsFormGroupInput): void {
    const shippingDetailsRawValue = { ...this.getFormDefaults(), ...shippingDetails };
    form.reset(
      {
        ...shippingDetailsRawValue,
        id: { value: shippingDetailsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ShippingDetailsFormDefaults {
    return {
      id: null,
    };
  }
}
