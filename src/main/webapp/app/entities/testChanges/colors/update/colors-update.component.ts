import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ColorsFormService, ColorsFormGroup } from './colors-form.service';
import { IColors } from '../colors.model';
import { ColorsService } from '../service/colors.service';

@Component({
  selector: 'jhi-colors-update',
  templateUrl: './colors-update.component.html',
})
export class ColorsUpdateComponent implements OnInit {
  isSaving = false;
  colors: IColors | null = null;

  editForm: ColorsFormGroup = this.colorsFormService.createColorsFormGroup();

  constructor(
    protected colorsService: ColorsService,
    protected colorsFormService: ColorsFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ colors }) => {
      this.colors = colors;
      if (colors) {
        this.updateForm(colors);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const colors = this.colorsFormService.getColors(this.editForm);
    if (colors.id !== null) {
      this.subscribeToSaveResponse(this.colorsService.update(colors));
    } else {
      this.subscribeToSaveResponse(this.colorsService.create(colors));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IColors>>): void {
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

  protected updateForm(colors: IColors): void {
    this.colors = colors;
    this.colorsFormService.resetForm(this.editForm, colors);
  }
}
