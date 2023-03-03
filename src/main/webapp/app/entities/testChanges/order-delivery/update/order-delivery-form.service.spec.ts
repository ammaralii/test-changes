import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../order-delivery.test-samples';

import { OrderDeliveryFormService } from './order-delivery-form.service';

describe('OrderDelivery Form Service', () => {
  let service: OrderDeliveryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrderDeliveryFormService);
  });

  describe('Service methods', () => {
    describe('createOrderDeliveryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOrderDeliveryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            deliveryDate: expect.any(Object),
            deliveryCharge: expect.any(Object),
            shippingStatus: expect.any(Object),
            order: expect.any(Object),
            shippingAddress: expect.any(Object),
            deliveryManager: expect.any(Object),
            deliveryBoy: expect.any(Object),
          })
        );
      });

      it('passing IOrderDelivery should create a new form with FormGroup', () => {
        const formGroup = service.createOrderDeliveryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            deliveryDate: expect.any(Object),
            deliveryCharge: expect.any(Object),
            shippingStatus: expect.any(Object),
            order: expect.any(Object),
            shippingAddress: expect.any(Object),
            deliveryManager: expect.any(Object),
            deliveryBoy: expect.any(Object),
          })
        );
      });
    });

    describe('getOrderDelivery', () => {
      it('should return NewOrderDelivery for default OrderDelivery initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createOrderDeliveryFormGroup(sampleWithNewData);

        const orderDelivery = service.getOrderDelivery(formGroup) as any;

        expect(orderDelivery).toMatchObject(sampleWithNewData);
      });

      it('should return NewOrderDelivery for empty OrderDelivery initial value', () => {
        const formGroup = service.createOrderDeliveryFormGroup();

        const orderDelivery = service.getOrderDelivery(formGroup) as any;

        expect(orderDelivery).toMatchObject({});
      });

      it('should return IOrderDelivery', () => {
        const formGroup = service.createOrderDeliveryFormGroup(sampleWithRequiredData);

        const orderDelivery = service.getOrderDelivery(formGroup) as any;

        expect(orderDelivery).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOrderDelivery should not enable id FormControl', () => {
        const formGroup = service.createOrderDeliveryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOrderDelivery should disable id FormControl', () => {
        const formGroup = service.createOrderDeliveryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
