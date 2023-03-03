import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../shipping-details.test-samples';

import { ShippingDetailsFormService } from './shipping-details-form.service';

describe('ShippingDetails Form Service', () => {
  let service: ShippingDetailsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ShippingDetailsFormService);
  });

  describe('Service methods', () => {
    describe('createShippingDetailsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createShippingDetailsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            shippingAddress: expect.any(Object),
            shippingMethod: expect.any(Object),
            estimatedDeliveryDate: expect.any(Object),
            order: expect.any(Object),
          })
        );
      });

      it('passing IShippingDetails should create a new form with FormGroup', () => {
        const formGroup = service.createShippingDetailsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            shippingAddress: expect.any(Object),
            shippingMethod: expect.any(Object),
            estimatedDeliveryDate: expect.any(Object),
            order: expect.any(Object),
          })
        );
      });
    });

    describe('getShippingDetails', () => {
      it('should return NewShippingDetails for default ShippingDetails initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createShippingDetailsFormGroup(sampleWithNewData);

        const shippingDetails = service.getShippingDetails(formGroup) as any;

        expect(shippingDetails).toMatchObject(sampleWithNewData);
      });

      it('should return NewShippingDetails for empty ShippingDetails initial value', () => {
        const formGroup = service.createShippingDetailsFormGroup();

        const shippingDetails = service.getShippingDetails(formGroup) as any;

        expect(shippingDetails).toMatchObject({});
      });

      it('should return IShippingDetails', () => {
        const formGroup = service.createShippingDetailsFormGroup(sampleWithRequiredData);

        const shippingDetails = service.getShippingDetails(formGroup) as any;

        expect(shippingDetails).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IShippingDetails should not enable id FormControl', () => {
        const formGroup = service.createShippingDetailsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewShippingDetails should disable id FormControl', () => {
        const formGroup = service.createShippingDetailsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
