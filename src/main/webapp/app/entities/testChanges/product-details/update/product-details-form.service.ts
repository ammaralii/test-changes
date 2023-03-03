import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProductDetails, NewProductDetails } from '../product-details.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProductDetails for edit and NewProductDetailsFormGroupInput for create.
 */
type ProductDetailsFormGroupInput = IProductDetails | PartialWithRequiredKeyOf<NewProductDetails>;

type ProductDetailsFormDefaults = Pick<NewProductDetails, 'id' | 'isavailable'>;

type ProductDetailsFormGroupContent = {
  id: FormControl<IProductDetails['id'] | NewProductDetails['id']>;
  description: FormControl<IProductDetails['description']>;
  imageUrl: FormControl<IProductDetails['imageUrl']>;
  isavailable: FormControl<IProductDetails['isavailable']>;
  product: FormControl<IProductDetails['product']>;
};

export type ProductDetailsFormGroup = FormGroup<ProductDetailsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductDetailsFormService {
  createProductDetailsFormGroup(productDetails: ProductDetailsFormGroupInput = { id: null }): ProductDetailsFormGroup {
    const productDetailsRawValue = {
      ...this.getFormDefaults(),
      ...productDetails,
    };
    return new FormGroup<ProductDetailsFormGroupContent>({
      id: new FormControl(
        { value: productDetailsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      description: new FormControl(productDetailsRawValue.description, {
        validators: [Validators.required, Validators.maxLength(65535)],
      }),
      imageUrl: new FormControl(productDetailsRawValue.imageUrl, {
        validators: [Validators.required, Validators.maxLength(32)],
      }),
      isavailable: new FormControl(productDetailsRawValue.isavailable, {
        validators: [Validators.required],
      }),
      product: new FormControl(productDetailsRawValue.product, {
        validators: [Validators.required],
      }),
    });
  }

  getProductDetails(form: ProductDetailsFormGroup): IProductDetails | NewProductDetails {
    return form.getRawValue() as IProductDetails | NewProductDetails;
  }

  resetForm(form: ProductDetailsFormGroup, productDetails: ProductDetailsFormGroupInput): void {
    const productDetailsRawValue = { ...this.getFormDefaults(), ...productDetails };
    form.reset(
      {
        ...productDetailsRawValue,
        id: { value: productDetailsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProductDetailsFormDefaults {
    return {
      id: null,
      isavailable: false,
    };
  }
}
