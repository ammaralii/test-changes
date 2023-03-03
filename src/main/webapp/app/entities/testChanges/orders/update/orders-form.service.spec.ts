import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../orders.test-samples';

import { OrdersFormService } from './orders-form.service';

describe('Orders Form Service', () => {
  let service: OrdersFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrdersFormService);
  });

  describe('Service methods', () => {
    describe('createOrdersFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOrdersFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            orderDate: expect.any(Object),
            totalAmount: expect.any(Object),
            customer: expect.any(Object),
          })
        );
      });

      it('passing IOrders should create a new form with FormGroup', () => {
        const formGroup = service.createOrdersFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            orderDate: expect.any(Object),
            totalAmount: expect.any(Object),
            customer: expect.any(Object),
          })
        );
      });
    });

    describe('getOrders', () => {
      it('should return NewOrders for default Orders initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createOrdersFormGroup(sampleWithNewData);

        const orders = service.getOrders(formGroup) as any;

        expect(orders).toMatchObject(sampleWithNewData);
      });

      it('should return NewOrders for empty Orders initial value', () => {
        const formGroup = service.createOrdersFormGroup();

        const orders = service.getOrders(formGroup) as any;

        expect(orders).toMatchObject({});
      });

      it('should return IOrders', () => {
        const formGroup = service.createOrdersFormGroup(sampleWithRequiredData);

        const orders = service.getOrders(formGroup) as any;

        expect(orders).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOrders should not enable id FormControl', () => {
        const formGroup = service.createOrdersFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOrders should disable id FormControl', () => {
        const formGroup = service.createOrdersFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
