import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICategories, NewCategories } from '../categories.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICategories for edit and NewCategoriesFormGroupInput for create.
 */
type CategoriesFormGroupInput = ICategories | PartialWithRequiredKeyOf<NewCategories>;

type CategoriesFormDefaults = Pick<NewCategories, 'id'>;

type CategoriesFormGroupContent = {
  id: FormControl<ICategories['id'] | NewCategories['id']>;
  name: FormControl<ICategories['name']>;
  detail: FormControl<ICategories['detail']>;
};

export type CategoriesFormGroup = FormGroup<CategoriesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CategoriesFormService {
  createCategoriesFormGroup(categories: CategoriesFormGroupInput = { id: null }): CategoriesFormGroup {
    const categoriesRawValue = {
      ...this.getFormDefaults(),
      ...categories,
    };
    return new FormGroup<CategoriesFormGroupContent>({
      id: new FormControl(
        { value: categoriesRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(categoriesRawValue.name, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      detail: new FormControl(categoriesRawValue.detail, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
    });
  }

  getCategories(form: CategoriesFormGroup): ICategories | NewCategories {
    return form.getRawValue() as ICategories | NewCategories;
  }

  resetForm(form: CategoriesFormGroup, categories: CategoriesFormGroupInput): void {
    const categoriesRawValue = { ...this.getFormDefaults(), ...categories };
    form.reset(
      {
        ...categoriesRawValue,
        id: { value: categoriesRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CategoriesFormDefaults {
    return {
      id: null,
    };
  }
}
