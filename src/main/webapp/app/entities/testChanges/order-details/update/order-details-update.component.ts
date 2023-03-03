import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { OrderDetailsFormService, OrderDetailsFormGroup } from './order-details-form.service';
import { IOrderDetails } from '../order-details.model';
import { OrderDetailsService } from '../service/order-details.service';
import { IOrders } from 'app/entities/testChanges/orders/orders.model';
import { OrdersService } from 'app/entities/testChanges/orders/service/orders.service';
import { IProducts } from 'app/entities/testChanges/products/products.model';
import { ProductsService } from 'app/entities/testChanges/products/service/products.service';

@Component({
  selector: 'jhi-order-details-update',
  templateUrl: './order-details-update.component.html',
})
export class OrderDetailsUpdateComponent implements OnInit {
  isSaving = false;
  orderDetails: IOrderDetails | null = null;

  ordersSharedCollection: IOrders[] = [];
  productsSharedCollection: IProducts[] = [];

  editForm: OrderDetailsFormGroup = this.orderDetailsFormService.createOrderDetailsFormGroup();

  constructor(
    protected orderDetailsService: OrderDetailsService,
    protected orderDetailsFormService: OrderDetailsFormService,
    protected ordersService: OrdersService,
    protected productsService: ProductsService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareOrders = (o1: IOrders | null, o2: IOrders | null): boolean => this.ordersService.compareOrders(o1, o2);

  compareProducts = (o1: IProducts | null, o2: IProducts | null): boolean => this.productsService.compareProducts(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderDetails }) => {
      this.orderDetails = orderDetails;
      if (orderDetails) {
        this.updateForm(orderDetails);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orderDetails = this.orderDetailsFormService.getOrderDetails(this.editForm);
    if (orderDetails.id !== null) {
      this.subscribeToSaveResponse(this.orderDetailsService.update(orderDetails));
    } else {
      this.subscribeToSaveResponse(this.orderDetailsService.create(orderDetails));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrderDetails>>): void {
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

  protected updateForm(orderDetails: IOrderDetails): void {
    this.orderDetails = orderDetails;
    this.orderDetailsFormService.resetForm(this.editForm, orderDetails);

    this.ordersSharedCollection = this.ordersService.addOrdersToCollectionIfMissing<IOrders>(
      this.ordersSharedCollection,
      orderDetails.order
    );
    this.productsSharedCollection = this.productsService.addProductsToCollectionIfMissing<IProducts>(
      this.productsSharedCollection,
      orderDetails.product
    );
  }

  protected loadRelationshipsOptions(): void {
    this.ordersService
      .query()
      .pipe(map((res: HttpResponse<IOrders[]>) => res.body ?? []))
      .pipe(map((orders: IOrders[]) => this.ordersService.addOrdersToCollectionIfMissing<IOrders>(orders, this.orderDetails?.order)))
      .subscribe((orders: IOrders[]) => (this.ordersSharedCollection = orders));

    this.productsService
      .query()
      .pipe(map((res: HttpResponse<IProducts[]>) => res.body ?? []))
      .pipe(
        map((products: IProducts[]) =>
          this.productsService.addProductsToCollectionIfMissing<IProducts>(products, this.orderDetails?.product)
        )
      )
      .subscribe((products: IProducts[]) => (this.productsSharedCollection = products));
  }
}
