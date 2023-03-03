import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../addresses.test-samples';

import { AddressesFormService } from './addresses-form.service';

describe('Addresses Form Service', () => {
  let service: AddressesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AddressesFormService);
  });

  describe('Service methods', () => {
    describe('createAddressesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAddressesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            street: expect.any(Object),
            city: expect.any(Object),
            state: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing IAddresses should create a new form with FormGroup', () => {
        const formGroup = service.createAddressesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            street: expect.any(Object),
            city: expect.any(Object),
            state: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });
    });

    describe('getAddresses', () => {
      it('should return NewAddresses for default Addresses initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createAddressesFormGroup(sampleWithNewData);

        const addresses = service.getAddresses(formGroup) as any;

        expect(addresses).toMatchObject(sampleWithNewData);
      });

      it('should return NewAddresses for empty Addresses initial value', () => {
        const formGroup = service.createAddressesFormGroup();

        const addresses = service.getAddresses(formGroup) as any;

        expect(addresses).toMatchObject({});
      });

      it('should return IAddresses', () => {
        const formGroup = service.createAddressesFormGroup(sampleWithRequiredData);

        const addresses = service.getAddresses(formGroup) as any;

        expect(addresses).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAddresses should not enable id FormControl', () => {
        const formGroup = service.createAddressesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAddresses should disable id FormControl', () => {
        const formGroup = service.createAddressesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
