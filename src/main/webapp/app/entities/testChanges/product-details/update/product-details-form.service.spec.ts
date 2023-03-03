import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../product-details.test-samples';

import { ProductDetailsFormService } from './product-details-form.service';

describe('ProductDetails Form Service', () => {
  let service: ProductDetailsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductDetailsFormService);
  });

  describe('Service methods', () => {
    describe('createProductDetailsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProductDetailsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
            imageUrl: expect.any(Object),
            isavailable: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });

      it('passing IProductDetails should create a new form with FormGroup', () => {
        const formGroup = service.createProductDetailsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
            imageUrl: expect.any(Object),
            isavailable: expect.any(Object),
            product: expect.any(Object),
          })
        );
      });
    });

    describe('getProductDetails', () => {
      it('should return NewProductDetails for default ProductDetails initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProductDetailsFormGroup(sampleWithNewData);

        const productDetails = service.getProductDetails(formGroup) as any;

        expect(productDetails).toMatchObject(sampleWithNewData);
      });

      it('should return NewProductDetails for empty ProductDetails initial value', () => {
        const formGroup = service.createProductDetailsFormGroup();

        const productDetails = service.getProductDetails(formGroup) as any;

        expect(productDetails).toMatchObject({});
      });

      it('should return IProductDetails', () => {
        const formGroup = service.createProductDetailsFormGroup(sampleWithRequiredData);

        const productDetails = service.getProductDetails(formGroup) as any;

        expect(productDetails).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProductDetails should not enable id FormControl', () => {
        const formGroup = service.createProductDetailsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProductDetails should disable id FormControl', () => {
        const formGroup = service.createProductDetailsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
