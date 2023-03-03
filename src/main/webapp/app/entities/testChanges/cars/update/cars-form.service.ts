import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICars, NewCars } from '../cars.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICars for edit and NewCarsFormGroupInput for create.
 */
type CarsFormGroupInput = ICars | PartialWithRequiredKeyOf<NewCars>;

type CarsFormDefaults = Pick<NewCars, 'id' | 'colors'>;

type CarsFormGroupContent = {
  id: FormControl<ICars['id'] | NewCars['id']>;
  caruid: FormControl<ICars['caruid']>;
  name: FormControl<ICars['name']>;
  colors: FormControl<ICars['colors']>;
};

export type CarsFormGroup = FormGroup<CarsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CarsFormService {
  createCarsFormGroup(cars: CarsFormGroupInput = { id: null }): CarsFormGroup {
    const carsRawValue = {
      ...this.getFormDefaults(),
      ...cars,
    };
    return new FormGroup<CarsFormGroupContent>({
      id: new FormControl(
        { value: carsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      caruid: new FormControl(carsRawValue.caruid, {
        validators: [Validators.required],
      }),
      name: new FormControl(carsRawValue.name, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      colors: new FormControl(carsRawValue.colors ?? []),
    });
  }

  getCars(form: CarsFormGroup): ICars | NewCars {
    return form.getRawValue() as ICars | NewCars;
  }

  resetForm(form: CarsFormGroup, cars: CarsFormGroupInput): void {
    const carsRawValue = { ...this.getFormDefaults(), ...cars };
    form.reset(
      {
        ...carsRawValue,
        id: { value: carsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CarsFormDefaults {
    return {
      id: null,
      colors: [],
    };
  }
}
