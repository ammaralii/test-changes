import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProducts, NewProducts } from '../products.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProducts for edit and NewProductsFormGroupInput for create.
 */
type ProductsFormGroupInput = IProducts | PartialWithRequiredKeyOf<NewProducts>;

type ProductsFormDefaults = Pick<NewProducts, 'id'>;

type ProductsFormGroupContent = {
  id: FormControl<IProducts['id'] | NewProducts['id']>;
  name: FormControl<IProducts['name']>;
  category: FormControl<IProducts['category']>;
};

export type ProductsFormGroup = FormGroup<ProductsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductsFormService {
  createProductsFormGroup(products: ProductsFormGroupInput = { id: null }): ProductsFormGroup {
    const productsRawValue = {
      ...this.getFormDefaults(),
      ...products,
    };
    return new FormGroup<ProductsFormGroupContent>({
      id: new FormControl(
        { value: productsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(productsRawValue.name, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      category: new FormControl(productsRawValue.category, {
        validators: [Validators.required],
      }),
    });
  }

  getProducts(form: ProductsFormGroup): IProducts | NewProducts {
    return form.getRawValue() as IProducts | NewProducts;
  }

  resetForm(form: ProductsFormGroup, products: ProductsFormGroupInput): void {
    const productsRawValue = { ...this.getFormDefaults(), ...products };
    form.reset(
      {
        ...productsRawValue,
        id: { value: productsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProductsFormDefaults {
    return {
      id: null,
    };
  }
}
