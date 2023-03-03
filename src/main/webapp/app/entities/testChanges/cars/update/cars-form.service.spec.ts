import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../cars.test-samples';

import { CarsFormService } from './cars-form.service';

describe('Cars Form Service', () => {
  let service: CarsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CarsFormService);
  });

  describe('Service methods', () => {
    describe('createCarsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCarsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            caruid: expect.any(Object),
            name: expect.any(Object),
            colors: expect.any(Object),
          })
        );
      });

      it('passing ICars should create a new form with FormGroup', () => {
        const formGroup = service.createCarsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            caruid: expect.any(Object),
            name: expect.any(Object),
            colors: expect.any(Object),
          })
        );
      });
    });

    describe('getCars', () => {
      it('should return NewCars for default Cars initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCarsFormGroup(sampleWithNewData);

        const cars = service.getCars(formGroup) as any;

        expect(cars).toMatchObject(sampleWithNewData);
      });

      it('should return NewCars for empty Cars initial value', () => {
        const formGroup = service.createCarsFormGroup();

        const cars = service.getCars(formGroup) as any;

        expect(cars).toMatchObject({});
      });

      it('should return ICars', () => {
        const formGroup = service.createCarsFormGroup(sampleWithRequiredData);

        const cars = service.getCars(formGroup) as any;

        expect(cars).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICars should not enable id FormControl', () => {
        const formGroup = service.createCarsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCars should disable id FormControl', () => {
        const formGroup = service.createCarsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
