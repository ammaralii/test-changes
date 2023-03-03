import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ProductDetailsFormService, ProductDetailsFormGroup } from './product-details-form.service';
import { IProductDetails } from '../product-details.model';
import { ProductDetailsService } from '../service/product-details.service';
import { IProducts } from 'app/entities/testChanges/products/products.model';
import { ProductsService } from 'app/entities/testChanges/products/service/products.service';

@Component({
  selector: 'jhi-product-details-update',
  templateUrl: './product-details-update.component.html',
})
export class ProductDetailsUpdateComponent implements OnInit {
  isSaving = false;
  productDetails: IProductDetails | null = null;

  productsSharedCollection: IProducts[] = [];

  editForm: ProductDetailsFormGroup = this.productDetailsFormService.createProductDetailsFormGroup();

  constructor(
    protected productDetailsService: ProductDetailsService,
    protected productDetailsFormService: ProductDetailsFormService,
    protected productsService: ProductsService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProducts = (o1: IProducts | null, o2: IProducts | null): boolean => this.productsService.compareProducts(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productDetails }) => {
      this.productDetails = productDetails;
      if (productDetails) {
        this.updateForm(productDetails);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const productDetails = this.productDetailsFormService.getProductDetails(this.editForm);
    if (productDetails.id !== null) {
      this.subscribeToSaveResponse(this.productDetailsService.update(productDetails));
    } else {
      this.subscribeToSaveResponse(this.productDetailsService.create(productDetails));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProductDetails>>): void {
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

  protected updateForm(productDetails: IProductDetails): void {
    this.productDetails = productDetails;
    this.productDetailsFormService.resetForm(this.editForm, productDetails);

    this.productsSharedCollection = this.productsService.addProductsToCollectionIfMissing<IProducts>(
      this.productsSharedCollection,
      productDetails.product
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productsService
      .query()
      .pipe(map((res: HttpResponse<IProducts[]>) => res.body ?? []))
      .pipe(
        map((products: IProducts[]) =>
          this.productsService.addProductsToCollectionIfMissing<IProducts>(products, this.productDetails?.product)
        )
      )
      .subscribe((products: IProducts[]) => (this.productsSharedCollection = products));
  }
}
