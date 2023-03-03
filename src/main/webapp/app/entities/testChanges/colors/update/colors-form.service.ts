import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IColors, NewColors } from '../colors.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IColors for edit and NewColorsFormGroupInput for create.
 */
type ColorsFormGroupInput = IColors | PartialWithRequiredKeyOf<NewColors>;

type ColorsFormDefaults = Pick<NewColors, 'id' | 'cars'>;

type ColorsFormGroupContent = {
  id: FormControl<IColors['id'] | NewColors['id']>;
  coloruid: FormControl<IColors['coloruid']>;
  name: FormControl<IColors['name']>;
  cars: FormControl<IColors['cars']>;
};

export type ColorsFormGroup = FormGroup<ColorsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ColorsFormService {
  createColorsFormGroup(colors: ColorsFormGroupInput = { id: null }): ColorsFormGroup {
    const colorsRawValue = {
      ...this.getFormDefaults(),
      ...colors,
    };
    return new FormGroup<ColorsFormGroupContent>({
      id: new FormControl(
        { value: colorsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      coloruid: new FormControl(colorsRawValue.coloruid, {
        validators: [Validators.required],
      }),
      name: new FormControl(colorsRawValue.name, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      cars: new FormControl(colorsRawValue.cars ?? []),
    });
  }

  getColors(form: ColorsFormGroup): IColors | NewColors {
    return form.getRawValue() as IColors | NewColors;
  }

  resetForm(form: ColorsFormGroup, colors: ColorsFormGroupInput): void {
    const colorsRawValue = { ...this.getFormDefaults(), ...colors };
    form.reset(
      {
        ...colorsRawValue,
        id: { value: colorsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ColorsFormDefaults {
    return {
      id: null,
      cars: [],
    };
  }
}
