import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { OrderDeliveryFormService, OrderDeliveryFormGroup } from './order-delivery-form.service';
import { IOrderDelivery } from '../order-delivery.model';
import { OrderDeliveryService } from '../service/order-delivery.service';
import { IOrders } from 'app/entities/testChanges/orders/orders.model';
import { OrdersService } from 'app/entities/testChanges/orders/service/orders.service';
import { IShippingDetails } from 'app/entities/testChanges/shipping-details/shipping-details.model';
import { ShippingDetailsService } from 'app/entities/testChanges/shipping-details/service/shipping-details.service';
import { IDarazUsers } from 'app/entities/testChanges/daraz-users/daraz-users.model';
import { DarazUsersService } from 'app/entities/testChanges/daraz-users/service/daraz-users.service';
import { ShippingStatus } from 'app/entities/enumerations/shipping-status.model';

@Component({
  selector: 'jhi-order-delivery-update',
  templateUrl: './order-delivery-update.component.html',
})
export class OrderDeliveryUpdateComponent implements OnInit {
  isSaving = false;
  orderDelivery: IOrderDelivery | null = null;
  shippingStatusValues = Object.keys(ShippingStatus);

  ordersSharedCollection: IOrders[] = [];
  shippingDetailsSharedCollection: IShippingDetails[] = [];
  darazUsersSharedCollection: IDarazUsers[] = [];

  editForm: OrderDeliveryFormGroup = this.orderDeliveryFormService.createOrderDeliveryFormGroup();

  constructor(
    protected orderDeliveryService: OrderDeliveryService,
    protected orderDeliveryFormService: OrderDeliveryFormService,
    protected ordersService: OrdersService,
    protected shippingDetailsService: ShippingDetailsService,
    protected darazUsersService: DarazUsersService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareOrders = (o1: IOrders | null, o2: IOrders | null): boolean => this.ordersService.compareOrders(o1, o2);

  compareShippingDetails = (o1: IShippingDetails | null, o2: IShippingDetails | null): boolean =>
    this.shippingDetailsService.compareShippingDetails(o1, o2);

  compareDarazUsers = (o1: IDarazUsers | null, o2: IDarazUsers | null): boolean => this.darazUsersService.compareDarazUsers(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderDelivery }) => {
      this.orderDelivery = orderDelivery;
      if (orderDelivery) {
        this.updateForm(orderDelivery);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orderDelivery = this.orderDeliveryFormService.getOrderDelivery(this.editForm);
    if (orderDelivery.id !== null) {
      this.subscribeToSaveResponse(this.orderDeliveryService.update(orderDelivery));
    } else {
      this.subscribeToSaveResponse(this.orderDeliveryService.create(orderDelivery));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrderDelivery>>): void {
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

  protected updateForm(orderDelivery: IOrderDelivery): void {
    this.orderDelivery = orderDelivery;
    this.orderDeliveryFormService.resetForm(this.editForm, orderDelivery);

    this.ordersSharedCollection = this.ordersService.addOrdersToCollectionIfMissing<IOrders>(
      this.ordersSharedCollection,
      orderDelivery.order
    );
    this.shippingDetailsSharedCollection = this.shippingDetailsService.addShippingDetailsToCollectionIfMissing<IShippingDetails>(
      this.shippingDetailsSharedCollection,
      orderDelivery.shippingAddress
    );
    this.darazUsersSharedCollection = this.darazUsersService.addDarazUsersToCollectionIfMissing<IDarazUsers>(
      this.darazUsersSharedCollection,
      orderDelivery.deliveryManager,
      orderDelivery.deliveryBoy
    );
  }

  protected loadRelationshipsOptions(): void {
    this.ordersService
      .query()
      .pipe(map((res: HttpResponse<IOrders[]>) => res.body ?? []))
      .pipe(map((orders: IOrders[]) => this.ordersService.addOrdersToCollectionIfMissing<IOrders>(orders, this.orderDelivery?.order)))
      .subscribe((orders: IOrders[]) => (this.ordersSharedCollection = orders));

    this.shippingDetailsService
      .query()
      .pipe(map((res: HttpResponse<IShippingDetails[]>) => res.body ?? []))
      .pipe(
        map((shippingDetails: IShippingDetails[]) =>
          this.shippingDetailsService.addShippingDetailsToCollectionIfMissing<IShippingDetails>(
            shippingDetails,
            this.orderDelivery?.shippingAddress
          )
        )
      )
      .subscribe((shippingDetails: IShippingDetails[]) => (this.shippingDetailsSharedCollection = shippingDetails));

    this.darazUsersService
      .query()
      .pipe(map((res: HttpResponse<IDarazUsers[]>) => res.body ?? []))
      .pipe(
        map((darazUsers: IDarazUsers[]) =>
          this.darazUsersService.addDarazUsersToCollectionIfMissing<IDarazUsers>(
            darazUsers,
            this.orderDelivery?.deliveryManager,
            this.orderDelivery?.deliveryBoy
          )
        )
      )
      .subscribe((darazUsers: IDarazUsers[]) => (this.darazUsersSharedCollection = darazUsers));
  }
}
