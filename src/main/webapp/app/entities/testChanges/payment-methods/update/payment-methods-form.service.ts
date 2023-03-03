import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPaymentMethods, NewPaymentMethods } from '../payment-methods.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPaymentMethods for edit and NewPaymentMethodsFormGroupInput for create.
 */
type PaymentMethodsFormGroupInput = IPaymentMethods | PartialWithRequiredKeyOf<NewPaymentMethods>;

type PaymentMethodsFormDefaults = Pick<NewPaymentMethods, 'id'>;

type PaymentMethodsFormGroupContent = {
  id: FormControl<IPaymentMethods['id'] | NewPaymentMethods['id']>;
  cardNumber: FormControl<IPaymentMethods['cardNumber']>;
  cardHolderName: FormControl<IPaymentMethods['cardHolderName']>;
  expirationDate: FormControl<IPaymentMethods['expirationDate']>;
  customer: FormControl<IPaymentMethods['customer']>;
};

export type PaymentMethodsFormGroup = FormGroup<PaymentMethodsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PaymentMethodsFormService {
  createPaymentMethodsFormGroup(paymentMethods: PaymentMethodsFormGroupInput = { id: null }): PaymentMethodsFormGroup {
    const paymentMethodsRawValue = {
      ...this.getFormDefaults(),
      ...paymentMethods,
    };
    return new FormGroup<PaymentMethodsFormGroupContent>({
      id: new FormControl(
        { value: paymentMethodsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      cardNumber: new FormControl(paymentMethodsRawValue.cardNumber, {
        validators: [Validators.required, Validators.maxLength(32)],
      }),
      cardHolderName: new FormControl(paymentMethodsRawValue.cardHolderName, {
        validators: [Validators.required, Validators.maxLength(32)],
      }),
      expirationDate: new FormControl(paymentMethodsRawValue.expirationDate, {
        validators: [Validators.required, Validators.maxLength(10)],
      }),
      customer: new FormControl(paymentMethodsRawValue.customer, {
        validators: [Validators.required],
      }),
    });
  }

  getPaymentMethods(form: PaymentMethodsFormGroup): IPaymentMethods | NewPaymentMethods {
    return form.getRawValue() as IPaymentMethods | NewPaymentMethods;
  }

  resetForm(form: PaymentMethodsFormGroup, paymentMethods: PaymentMethodsFormGroupInput): void {
    const paymentMethodsRawValue = { ...this.getFormDefaults(), ...paymentMethods };
    form.reset(
      {
        ...paymentMethodsRawValue,
        id: { value: paymentMethodsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PaymentMethodsFormDefaults {
    return {
      id: null,
    };
  }
}
