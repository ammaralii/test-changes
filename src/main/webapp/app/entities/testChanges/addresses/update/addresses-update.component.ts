import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { AddressesFormService, AddressesFormGroup } from './addresses-form.service';
import { IAddresses } from '../addresses.model';
import { AddressesService } from '../service/addresses.service';
import { IDarazUsers } from 'app/entities/testChanges/daraz-users/daraz-users.model';
import { DarazUsersService } from 'app/entities/testChanges/daraz-users/service/daraz-users.service';

@Component({
  selector: 'jhi-addresses-update',
  templateUrl: './addresses-update.component.html',
})
export class AddressesUpdateComponent implements OnInit {
  isSaving = false;
  addresses: IAddresses | null = null;

  darazUsersSharedCollection: IDarazUsers[] = [];

  editForm: AddressesFormGroup = this.addressesFormService.createAddressesFormGroup();

  constructor(
    protected addressesService: AddressesService,
    protected addressesFormService: AddressesFormService,
    protected darazUsersService: DarazUsersService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareDarazUsers = (o1: IDarazUsers | null, o2: IDarazUsers | null): boolean => this.darazUsersService.compareDarazUsers(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ addresses }) => {
      this.addresses = addresses;
      if (addresses) {
        this.updateForm(addresses);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const addresses = this.addressesFormService.getAddresses(this.editForm);
    if (addresses.id !== null) {
      this.subscribeToSaveResponse(this.addressesService.update(addresses));
    } else {
      this.subscribeToSaveResponse(this.addressesService.create(addresses));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAddresses>>): void {
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

  protected updateForm(addresses: IAddresses): void {
    this.addresses = addresses;
    this.addressesFormService.resetForm(this.editForm, addresses);

    this.darazUsersSharedCollection = this.darazUsersService.addDarazUsersToCollectionIfMissing<IDarazUsers>(
      this.darazUsersSharedCollection,
      addresses.user
    );
  }

  protected loadRelationshipsOptions(): void {
    this.darazUsersService
      .query()
      .pipe(map((res: HttpResponse<IDarazUsers[]>) => res.body ?? []))
      .pipe(
        map((darazUsers: IDarazUsers[]) =>
          this.darazUsersService.addDarazUsersToCollectionIfMissing<IDarazUsers>(darazUsers, this.addresses?.user)
        )
      )
      .subscribe((darazUsers: IDarazUsers[]) => (this.darazUsersSharedCollection = darazUsers));
  }
}
