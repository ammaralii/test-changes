import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../order-details.test-samples';

import { OrderDetailsFormService } from './order-details-form.service';

describe('OrderDetails Form Service', () => {
  let service: OrderDetailsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrderDetailsFormService);
  });

  describe('Service methods', () => {
    describe('createOrderDetailsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOrderDetailsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            amount: expect.any(Object),
            order: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });

      it('passing IOrderDetails should create a new form with FormGroup', () => {
        const formGroup = service.createOrderDetailsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            amount: expect.any(Object),
            order: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });
    });

    describe('getOrderDetails', () => {
      it('should return NewOrderDetails for default OrderDetails initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createOrderDetailsFormGroup(sampleWithNewData);

        const orderDetails = service.getOrderDetails(formGroup) as any;

        expect(orderDetails).toMatchObject(sampleWithNewData);
      });

      it('should return NewOrderDetails for empty OrderDetails initial value', () => {
        const formGroup = service.createOrderDetailsFormGroup();

        const orderDetails = service.getOrderDetails(formGroup) as any;

        expect(orderDetails).toMatchObject({});
      });

      it('should return IOrderDetails', () => {
        const formGroup = service.createOrderDetailsFormGroup(sampleWithRequiredData);

        const orderDetails = service.getOrderDetails(formGroup) as any;

        expect(orderDetails).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOrderDetails should not enable id FormControl', () => {
        const formGroup = service.createOrderDetailsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOrderDetails should disable id FormControl', () => {
        const formGroup = service.createOrderDetailsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
