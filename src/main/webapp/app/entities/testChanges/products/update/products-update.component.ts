import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProductsFormService, ProductsFormGroup } from './products-form.service';
import { IProducts } from '../products.model';
import { ProductsService } from '../service/products.service';
import { ICategories } from 'app/entities/testChanges/categories/categories.model';
import { CategoriesService } from 'app/entities/testChanges/categories/service/categories.service';

@Component({
  selector: 'jhi-products-update',
  templateUrl: './products-update.component.html',
})
export class ProductsUpdateComponent implements OnInit {
  isSaving = false;
  products: IProducts | null = null;

  categoriesSharedCollection: ICategories[] = [];

  editForm: ProductsFormGroup = this.productsFormService.createProductsFormGroup();

  constructor(
    protected productsService: ProductsService,
    protected productsFormService: ProductsFormService,
    protected categoriesService: CategoriesService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCategories = (o1: ICategories | null, o2: ICategories | null): boolean => this.categoriesService.compareCategories(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ products }) => {
      this.products = products;
      if (products) {
        this.updateForm(products);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const products = this.productsFormService.getProducts(this.editForm);
    if (products.id !== null) {
      this.subscribeToSaveResponse(this.productsService.update(products));
    } else {
      this.subscribeToSaveResponse(this.productsService.create(products));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProducts>>): void {
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

  protected updateForm(products: IProducts): void {
    this.products = products;
    this.productsFormService.resetForm(this.editForm, products);

    this.categoriesSharedCollection = this.categoriesService.addCategoriesToCollectionIfMissing<ICategories>(
      this.categoriesSharedCollection,
      products.category
    );
  }

  protected loadRelationshipsOptions(): void {
    this.categoriesService
      .query()
      .pipe(map((res: HttpResponse<ICategories[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategories[]) =>
          this.categoriesService.addCategoriesToCollectionIfMissing<ICategories>(categories, this.products?.category)
        )
      )
      .subscribe((categories: ICategories[]) => (this.categoriesSharedCollection = categories));
  }
}
