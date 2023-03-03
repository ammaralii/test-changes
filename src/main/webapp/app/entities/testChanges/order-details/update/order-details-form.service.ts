import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IOrderDetails, NewOrderDetails } from '../order-details.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrderDetails for edit and NewOrderDetailsFormGroupInput for create.
 */
type OrderDetailsFormGroupInput = IOrderDetails | PartialWithRequiredKeyOf<NewOrderDetails>;

type OrderDetailsFormDefaults = Pick<NewOrderDetails, 'id'>;

type OrderDetailsFormGroupContent = {
  id: FormControl<IOrderDetails['id'] | NewOrderDetails['id']>;
  quantity: FormControl<IOrderDetails['quantity']>;
  amount: FormControl<IOrderDetails['amount']>;
  order: FormControl<IOrderDetails['order']>;
  product: FormControl<IOrderDetails['product']>;
};

export type OrderDetailsFormGroup = FormGroup<OrderDetailsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrderDetailsFormService {
  createOrderDetailsFormGroup(orderDetails: OrderDetailsFormGroupInput = { id: null }): OrderDetailsFormGroup {
    const orderDetailsRawValue = {
      ...this.getFormDefaults(),
      ...orderDetails,
    };
    return new FormGroup<OrderDetailsFormGroupContent>({
      id: new FormControl(
        { value: orderDetailsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      quantity: new FormControl(orderDetailsRawValue.quantity),
      amount: new FormControl(orderDetailsRawValue.amount),
      order: new FormControl(orderDetailsRawValue.order, {
        validators: [Validators.required],
      }),
      product: new FormControl(orderDetailsRawValue.product, {
        validators: [Validators.required],
      }),
    });
  }

  getOrderDetails(form: OrderDetailsFormGroup): IOrderDetails | NewOrderDetails {
    return form.getRawValue() as IOrderDetails | NewOrderDetails;
  }

  resetForm(form: OrderDetailsFormGroup, orderDetails: OrderDetailsFormGroupInput): void {
    const orderDetailsRawValue = { ...this.getFormDefaults(), ...orderDetails };
    form.reset(
      {
        ...orderDetailsRawValue,
        id: { value: orderDetailsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OrderDetailsFormDefaults {
    return {
      id: null,
    };
  }
}
