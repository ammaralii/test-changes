import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../categories.test-samples';

import { CategoriesFormService } from './categories-form.service';

describe('Categories Form Service', () => {
  let service: CategoriesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CategoriesFormService);
  });

  describe('Service methods', () => {
    describe('createCategoriesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCategoriesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            detail: expect.any(Object),
          })
        );
      });

      it('passing ICategories should create a new form with FormGroup', () => {
        const formGroup = service.createCategoriesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            detail: expect.any(Object),
          })
        );
      });
    });

    describe('getCategories', () => {
      it('should return NewCategories for default Categories initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCategoriesFormGroup(sampleWithNewData);

        const categories = service.getCategories(formGroup) as any;

        expect(categories).toMatchObject(sampleWithNewData);
      });

      it('should return NewCategories for empty Categories initial value', () => {
        const formGroup = service.createCategoriesFormGroup();

        const categories = service.getCategories(formGroup) as any;

        expect(categories).toMatchObject({});
      });

      it('should return ICategories', () => {
        const formGroup = service.createCategoriesFormGroup(sampleWithRequiredData);

        const categories = service.getCategories(formGroup) as any;

        expect(categories).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICategories should not enable id FormControl', () => {
        const formGroup = service.createCategoriesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCategories should disable id FormControl', () => {
        const formGroup = service.createCategoriesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
