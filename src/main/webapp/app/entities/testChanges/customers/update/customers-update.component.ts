import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { CustomersFormService, CustomersFormGroup } from './customers-form.service';
import { ICustomers } from '../customers.model';
import { CustomersService } from '../service/customers.service';

@Component({
  selector: 'jhi-customers-update',
  templateUrl: './customers-update.component.html',
})
export class CustomersUpdateComponent implements OnInit {
  isSaving = false;
  customers: ICustomers | null = null;

  editForm: CustomersFormGroup = this.customersFormService.createCustomersFormGroup();

  constructor(
    protected customersService: CustomersService,
    protected customersFormService: CustomersFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customers }) => {
      this.customers = customers;
      if (customers) {
        this.updateForm(customers);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const customers = this.customersFormService.getCustomers(this.editForm);
    if (customers.id !== null) {
      this.subscribeToSaveResponse(this.customersService.update(customers));
    } else {
      this.subscribeToSaveResponse(this.customersService.create(customers));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICustomers>>): void {
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

  protected updateForm(customers: ICustomers): void {
    this.customers = customers;
    this.customersFormService.resetForm(this.editForm, customers);
  }
}
