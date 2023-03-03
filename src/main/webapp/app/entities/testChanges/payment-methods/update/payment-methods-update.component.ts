import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PaymentMethodsFormService, PaymentMethodsFormGroup } from './payment-methods-form.service';
import { IPaymentMethods } from '../payment-methods.model';
import { PaymentMethodsService } from '../service/payment-methods.service';
import { ICustomers } from 'app/entities/testChanges/customers/customers.model';
import { CustomersService } from 'app/entities/testChanges/customers/service/customers.service';

@Component({
  selector: 'jhi-payment-methods-update',
  templateUrl: './payment-methods-update.component.html',
})
export class PaymentMethodsUpdateComponent implements OnInit {
  isSaving = false;
  paymentMethods: IPaymentMethods | null = null;

  customersSharedCollection: ICustomers[] = [];

  editForm: PaymentMethodsFormGroup = this.paymentMethodsFormService.createPaymentMethodsFormGroup();

  constructor(
    protected paymentMethodsService: PaymentMethodsService,
    protected paymentMethodsFormService: PaymentMethodsFormService,
    protected customersService: CustomersService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCustomers = (o1: ICustomers | null, o2: ICustomers | null): boolean => this.customersService.compareCustomers(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paymentMethods }) => {
      this.paymentMethods = paymentMethods;
      if (paymentMethods) {
        this.updateForm(paymentMethods);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const paymentMethods = this.paymentMethodsFormService.getPaymentMethods(this.editForm);
    if (paymentMethods.id !== null) {
      this.subscribeToSaveResponse(this.paymentMethodsService.update(paymentMethods));
    } else {
      this.subscribeToSaveResponse(this.paymentMethodsService.create(paymentMethods));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPaymentMethods>>): void {
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

  protected updateForm(paymentMethods: IPaymentMethods): void {
    this.paymentMethods = paymentMethods;
    this.paymentMethodsFormService.resetForm(this.editForm, paymentMethods);

    this.customersSharedCollection = this.customersService.addCustomersToCollectionIfMissing<ICustomers>(
      this.customersSharedCollection,
      paymentMethods.customer
    );
  }

  protected loadRelationshipsOptions(): void {
    this.customersService
      .query()
      .pipe(map((res: HttpResponse<ICustomers[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomers[]) =>
          this.customersService.addCustomersToCollectionIfMissing<ICustomers>(customers, this.paymentMethods?.customer)
        )
      )
      .subscribe((customers: ICustomers[]) => (this.customersSharedCollection = customers));
  }
}
