import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CarsFormService, CarsFormGroup } from './cars-form.service';
import { ICars } from '../cars.model';
import { CarsService } from '../service/cars.service';
import { IColors } from 'app/entities/testChanges/colors/colors.model';
import { ColorsService } from 'app/entities/testChanges/colors/service/colors.service';

@Component({
  selector: 'jhi-cars-update',
  templateUrl: './cars-update.component.html',
})
export class CarsUpdateComponent implements OnInit {
  isSaving = false;
  cars: ICars | null = null;

  colorsSharedCollection: IColors[] = [];

  editForm: CarsFormGroup = this.carsFormService.createCarsFormGroup();

  constructor(
    protected carsService: CarsService,
    protected carsFormService: CarsFormService,
    protected colorsService: ColorsService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareColors = (o1: IColors | null, o2: IColors | null): boolean => this.colorsService.compareColors(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cars }) => {
      this.cars = cars;
      if (cars) {
        this.updateForm(cars);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cars = this.carsFormService.getCars(this.editForm);
    if (cars.id !== null) {
      this.subscribeToSaveResponse(this.carsService.update(cars));
    } else {
      this.subscribeToSaveResponse(this.carsService.create(cars));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICars>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(cars: ICars): void {
    this.cars = cars;
    this.carsFormService.resetForm(this.editForm, cars);

    this.colorsSharedCollection = this.colorsService.addColorsToCollectionIfMissing<IColors>(
      this.colorsSharedCollection,
      ...(cars.colors ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.colorsService
      .query()
      .pipe(map((res: HttpResponse<IColors[]>) => res.body ?? []))
      .pipe(map((colors: IColors[]) => this.colorsService.addColorsToCollectionIfMissing<IColors>(colors, ...(this.cars?.colors ?? []))))
      .subscribe((colors: IColors[]) => (this.colorsSharedCollection = colors));
  }
}
