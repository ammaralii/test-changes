import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { CategoriesFormService, CategoriesFormGroup } from './categories-form.service';
import { ICategories } from '../categories.model';
import { CategoriesService } from '../service/categories.service';

@Component({
  selector: 'jhi-categories-update',
  templateUrl: './categories-update.component.html',
})
export class CategoriesUpdateComponent implements OnInit {
  isSaving = false;
  categories: ICategories | null = null;

  editForm: CategoriesFormGroup = this.categoriesFormService.createCategoriesFormGroup();

  constructor(
    protected categoriesService: CategoriesService,
    protected categoriesFormService: CategoriesFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ categories }) => {
      this.categories = categories;
      if (categories) {
        this.updateForm(categories);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const categories = this.categoriesFormService.getCategories(this.editForm);
    if (categories.id !== null) {
      this.subscribeToSaveResponse(this.categoriesService.update(categories));
    } else {
      this.subscribeToSaveResponse(this.categoriesService.create(categories));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICategories>>): void {
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

  protected updateForm(categories: ICategories): void {
    this.categories = categories;
    this.categoriesFormService.resetForm(this.editForm, categories);
  }
}
