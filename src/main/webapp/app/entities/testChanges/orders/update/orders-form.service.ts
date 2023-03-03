import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IOrders, NewOrders } from '../orders.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrders for edit and NewOrdersFormGroupInput for create.
 */
type OrdersFormGroupInput = IOrders | PartialWithRequiredKeyOf<NewOrders>;

type OrdersFormDefaults = Pick<NewOrders, 'id'>;

type OrdersFormGroupContent = {
  id: FormControl<IOrders['id'] | NewOrders['id']>;
  orderDate: FormControl<IOrders['orderDate']>;
  totalAmount: FormControl<IOrders['totalAmount']>;
  customer: FormControl<IOrders['customer']>;
};

export type OrdersFormGroup = FormGroup<OrdersFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrdersFormService {
  createOrdersFormGroup(orders: OrdersFormGroupInput = { id: null }): OrdersFormGroup {
    const ordersRawValue = {
      ...this.getFormDefaults(),
      ...orders,
    };
    return new FormGroup<OrdersFormGroupContent>({
      id: new FormControl(
        { value: ordersRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      orderDate: new FormControl(ordersRawValue.orderDate, {
        validators: [Validators.required],
      }),
      totalAmount: new FormControl(ordersRawValue.totalAmount),
      customer: new FormControl(ordersRawValue.customer, {
        validators: [Validators.required],
      }),
    });
  }

  getOrders(form: OrdersFormGroup): IOrders | NewOrders {
    return form.getRawValue() as IOrders | NewOrders;
  }

  resetForm(form: OrdersFormGroup, orders: OrdersFormGroupInput): void {
    const ordersRawValue = { ...this.getFormDefaults(), ...orders };
    form.reset(
      {
        ...ordersRawValue,
        id: { value: ordersRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OrdersFormDefaults {
    return {
      id: null,
    };
  }
}
