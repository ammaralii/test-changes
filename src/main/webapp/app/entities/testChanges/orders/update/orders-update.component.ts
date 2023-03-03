import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { OrdersFormService, OrdersFormGroup } from './orders-form.service';
import { IOrders } from '../orders.model';
import { OrdersService } from '../service/orders.service';
import { ICustomers } from 'app/entities/testChanges/customers/customers.model';
import { CustomersService } from 'app/entities/testChanges/customers/service/customers.service';

@Component({
  selector: 'jhi-orders-update',
  templateUrl: './orders-update.component.html',
})
export class OrdersUpdateComponent implements OnInit {
  isSaving = false;
  orders: IOrders | null = null;

  customersSharedCollection: ICustomers[] = [];

  editForm: OrdersFormGroup = this.ordersFormService.createOrdersFormGroup();

  constructor(
    protected ordersService: OrdersService,
    protected ordersFormService: OrdersFormService,
    protected customersService: CustomersService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCustomers = (o1: ICustomers | null, o2: ICustomers | null): boolean => this.customersService.compareCustomers(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orders }) => {
      this.orders = orders;
      if (orders) {
        this.updateForm(orders);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orders = this.ordersFormService.getOrders(this.editForm);
    if (orders.id !== null) {
      this.subscribeToSaveResponse(this.ordersService.update(orders));
    } else {
      this.subscribeToSaveResponse(this.ordersService.create(orders));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrders>>): void {
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

  protected updateForm(orders: IOrders): void {
    this.orders = orders;
    this.ordersFormService.resetForm(this.editForm, orders);

    this.customersSharedCollection = this.customersService.addCustomersToCollectionIfMissing<ICustomers>(
      this.customersSharedCollection,
      orders.customer
    );
  }

  protected loadRelationshipsOptions(): void {
    this.customersService
      .query()
      .pipe(map((res: HttpResponse<ICustomers[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomers[]) =>
          this.customersService.addCustomersToCollectionIfMissing<ICustomers>(customers, this.orders?.customer)
        )
      )
      .subscribe((customers: ICustomers[]) => (this.customersSharedCollection = customers));
  }
}
