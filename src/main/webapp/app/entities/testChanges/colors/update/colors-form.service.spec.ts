import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../colors.test-samples';

import { ColorsFormService } from './colors-form.service';

describe('Colors Form Service', () => {
  let service: ColorsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ColorsFormService);
  });

  describe('Service methods', () => {
    describe('createColorsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createColorsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            coloruid: expect.any(Object),
            name: expect.any(Object),
            cars: expect.any(Object),
          })
        );
      });

      it('passing IColors should create a new form with FormGroup', () => {
        const formGroup = service.createColorsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            coloruid: expect.any(Object),
            name: expect.any(Object),
            cars: expect.any(Object),
          })
        );
      });
    });

    describe('getColors', () => {
      it('should return NewColors for default Colors initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createColorsFormGroup(sampleWithNewData);

        const colors = service.getColors(formGroup) as any;

        expect(colors).toMatchObject(sampleWithNewData);
      });

      it('should return NewColors for empty Colors initial value', () => {
        const formGroup = service.createColorsFormGroup();

        const colors = service.getColors(formGroup) as any;

        expect(colors).toMatchObject({});
      });

      it('should return IColors', () => {
        const formGroup = service.createColorsFormGroup(sampleWithRequiredData);

        const colors = service.getColors(formGroup) as any;

        expect(colors).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IColors should not enable id FormControl', () => {
        const formGroup = service.createColorsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewColors should disable id FormControl', () => {
        const formGroup = service.createColorsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
