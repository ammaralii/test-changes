import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../payment-methods.test-samples';

import { PaymentMethodsFormService } from './payment-methods-form.service';

describe('PaymentMethods Form Service', () => {
  let service: PaymentMethodsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PaymentMethodsFormService);
  });

  describe('Service methods', () => {
    describe('createPaymentMethodsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPaymentMethodsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            cardNumber: expect.any(Object),
            cardHolderName: expect.any(Object),
            expirationDate: expect.any(Object),
            customer: expect.any(Object),
          })
        );
      });

      it('passing IPaymentMethods should create a new form with FormGroup', () => {
        const formGroup = service.createPaymentMethodsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            cardNumber: expect.any(Object),
            cardHolderName: expect.any(Object),
            expirationDate: expect.any(Object),
            customer: expect.any(Object),
          })
        );
      });
    });

    describe('getPaymentMethods', () => {
      it('should return NewPaymentMethods for default PaymentMethods initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPaymentMethodsFormGroup(sampleWithNewData);

        const paymentMethods = service.getPaymentMethods(formGroup) as any;

        expect(paymentMethods).toMatchObject(sampleWithNewData);
      });

      it('should return NewPaymentMethods for empty PaymentMethods initial value', () => {
        const formGroup = service.createPaymentMethodsFormGroup();

        const paymentMethods = service.getPaymentMethods(formGroup) as any;

        expect(paymentMethods).toMatchObject({});
      });

      it('should return IPaymentMethods', () => {
        const formGroup = service.createPaymentMethodsFormGroup(sampleWithRequiredData);

        const paymentMethods = service.getPaymentMethods(formGroup) as any;

        expect(paymentMethods).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPaymentMethods should not enable id FormControl', () => {
        const formGroup = service.createPaymentMethodsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPaymentMethods should disable id FormControl', () => {
        const formGroup = service.createPaymentMethodsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
