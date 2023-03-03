import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IOrderDelivery, NewOrderDelivery } from '../order-delivery.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrderDelivery for edit and NewOrderDeliveryFormGroupInput for create.
 */
type OrderDeliveryFormGroupInput = IOrderDelivery | PartialWithRequiredKeyOf<NewOrderDelivery>;

type OrderDeliveryFormDefaults = Pick<NewOrderDelivery, 'id'>;

type OrderDeliveryFormGroupContent = {
  id: FormControl<IOrderDelivery['id'] | NewOrderDelivery['id']>;
  deliveryDate: FormControl<IOrderDelivery['deliveryDate']>;
  deliveryCharge: FormControl<IOrderDelivery['deliveryCharge']>;
  shippingStatus: FormControl<IOrderDelivery['shippingStatus']>;
  order: FormControl<IOrderDelivery['order']>;
  shippingAddress: FormControl<IOrderDelivery['shippingAddress']>;
  deliveryManager: FormControl<IOrderDelivery['deliveryManager']>;
  deliveryBoy: FormControl<IOrderDelivery['deliveryBoy']>;
};

export type OrderDeliveryFormGroup = FormGroup<OrderDeliveryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrderDeliveryFormService {
  createOrderDeliveryFormGroup(orderDelivery: OrderDeliveryFormGroupInput = { id: null }): OrderDeliveryFormGroup {
    const orderDeliveryRawValue = {
      ...this.getFormDefaults(),
      ...orderDelivery,
    };
    return new FormGroup<OrderDeliveryFormGroupContent>({
      id: new FormControl(
        { value: orderDeliveryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      deliveryDate: new FormControl(orderDeliveryRawValue.deliveryDate),
      deliveryCharge: new FormControl(orderDeliveryRawValue.deliveryCharge),
      shippingStatus: new FormControl(orderDeliveryRawValue.shippingStatus, {
        validators: [Validators.required],
      }),
      order: new FormControl(orderDeliveryRawValue.order, {
        validators: [Validators.required],
      }),
      shippingAddress: new FormControl(orderDeliveryRawValue.shippingAddress, {
        validators: [Validators.required],
      }),
      deliveryManager: new FormControl(orderDeliveryRawValue.deliveryManager, {
        validators: [Validators.required],
      }),
      deliveryBoy: new FormControl(orderDeliveryRawValue.deliveryBoy, {
        validators: [Validators.required],
      }),
    });
  }

  getOrderDelivery(form: OrderDeliveryFormGroup): IOrderDelivery | NewOrderDelivery {
    return form.getRawValue() as IOrderDelivery | NewOrderDelivery;
  }

  resetForm(form: OrderDeliveryFormGroup, orderDelivery: OrderDeliveryFormGroupInput): void {
    const orderDeliveryRawValue = { ...this.getFormDefaults(), ...orderDelivery };
    form.reset(
      {
        ...orderDeliveryRawValue,
        id: { value: orderDeliveryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OrderDeliveryFormDefaults {
    return {
      id: null,
    };
  }
}
