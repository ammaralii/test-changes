import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ShippingDetailsFormService, ShippingDetailsFormGroup } from './shipping-details-form.service';
import { IShippingDetails } from '../shipping-details.model';
import { ShippingDetailsService } from '../service/shipping-details.service';
import { IOrders } from 'app/entities/testChanges/orders/orders.model';
import { OrdersService } from 'app/entities/testChanges/orders/service/orders.service';
import { ShippingMethod } from 'app/entities/enumerations/shipping-method.model';

@Component({
  selector: 'jhi-shipping-details-update',
  templateUrl: './shipping-details-update.component.html',
})
export class ShippingDetailsUpdateComponent implements OnInit {
  isSaving = false;
  shippingDetails: IShippingDetails | null = null;
  shippingMethodValues = Object.keys(ShippingMethod);

  ordersSharedCollection: IOrders[] = [];

  editForm: ShippingDetailsFormGroup = this.shippingDetailsFormService.createShippingDetailsFormGroup();

  constructor(
    protected shippingDetailsService: ShippingDetailsService,
    protected shippingDetailsFormService: ShippingDetailsFormService,
    protected ordersService: OrdersService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareOrders = (o1: IOrders | null, o2: IOrders | null): boolean => this.ordersService.compareOrders(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shippingDetails }) => {
      this.shippingDetails = shippingDetails;
      if (shippingDetails) {
        this.updateForm(shippingDetails);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const shippingDetails = this.shippingDetailsFormService.getShippingDetails(this.editForm);
    if (shippingDetails.id !== null) {
      this.subscribeToSaveResponse(this.shippingDetailsService.update(shippingDetails));
    } else {
      this.subscribeToSaveResponse(this.shippingDetailsService.create(shippingDetails));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IShippingDetails>>): void {
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

  protected updateForm(shippingDetails: IShippingDetails): void {
    this.shippingDetails = shippingDetails;
    this.shippingDetailsFormService.resetForm(this.editForm, shippingDetails);

    this.ordersSharedCollection = this.ordersService.addOrdersToCollectionIfMissing<IOrders>(
      this.ordersSharedCollection,
      shippingDetails.order
    );
  }

  protected loadRelationshipsOptions(): void {
    this.ordersService
      .query()
      .pipe(map((res: HttpResponse<IOrders[]>) => res.body ?? []))
      .pipe(map((orders: IOrders[]) => this.ordersService.addOrdersToCollectionIfMissing<IOrders>(orders, this.shippingDetails?.order)))
      .subscribe((orders: IOrders[]) => (this.ordersSharedCollection = orders));
  }
}
