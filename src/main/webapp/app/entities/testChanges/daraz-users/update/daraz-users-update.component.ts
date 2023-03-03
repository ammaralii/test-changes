import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DarazUsersFormService, DarazUsersFormGroup } from './daraz-users-form.service';
import { IDarazUsers } from '../daraz-users.model';
import { DarazUsersService } from '../service/daraz-users.service';
import { IRoles } from 'app/entities/testChanges/roles/roles.model';
import { RolesService } from 'app/entities/testChanges/roles/service/roles.service';

@Component({
  selector: 'jhi-daraz-users-update',
  templateUrl: './daraz-users-update.component.html',
})
export class DarazUsersUpdateComponent implements OnInit {
  isSaving = false;
  darazUsers: IDarazUsers | null = null;

  darazUsersSharedCollection: IDarazUsers[] = [];
  rolesSharedCollection: IRoles[] = [];

  editForm: DarazUsersFormGroup = this.darazUsersFormService.createDarazUsersFormGroup();

  constructor(
    protected darazUsersService: DarazUsersService,
    protected darazUsersFormService: DarazUsersFormService,
    protected rolesService: RolesService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareDarazUsers = (o1: IDarazUsers | null, o2: IDarazUsers | null): boolean => this.darazUsersService.compareDarazUsers(o1, o2);

  compareRoles = (o1: IRoles | null, o2: IRoles | null): boolean => this.rolesService.compareRoles(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ darazUsers }) => {
      this.darazUsers = darazUsers;
      if (darazUsers) {
        this.updateForm(darazUsers);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const darazUsers = this.darazUsersFormService.getDarazUsers(this.editForm);
    if (darazUsers.id !== null) {
      this.subscribeToSaveResponse(this.darazUsersService.update(darazUsers));
    } else {
      this.subscribeToSaveResponse(this.darazUsersService.create(darazUsers));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDarazUsers>>): void {
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

  protected updateForm(darazUsers: IDarazUsers): void {
    this.darazUsers = darazUsers;
    this.darazUsersFormService.resetForm(this.editForm, darazUsers);

    this.darazUsersSharedCollection = this.darazUsersService.addDarazUsersToCollectionIfMissing<IDarazUsers>(
      this.darazUsersSharedCollection,
      darazUsers.manager
    );
    this.rolesSharedCollection = this.rolesService.addRolesToCollectionIfMissing<IRoles>(
      this.rolesSharedCollection,
      ...(darazUsers.roles ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.darazUsersService
      .query()
      .pipe(map((res: HttpResponse<IDarazUsers[]>) => res.body ?? []))
      .pipe(
        map((darazUsers: IDarazUsers[]) =>
          this.darazUsersService.addDarazUsersToCollectionIfMissing<IDarazUsers>(darazUsers, this.darazUsers?.manager)
        )
      )
      .subscribe((darazUsers: IDarazUsers[]) => (this.darazUsersSharedCollection = darazUsers));

    this.rolesService
      .query()
      .pipe(map((res: HttpResponse<IRoles[]>) => res.body ?? []))
      .pipe(map((roles: IRoles[]) => this.rolesService.addRolesToCollectionIfMissing<IRoles>(roles, ...(this.darazUsers?.roles ?? []))))
      .subscribe((roles: IRoles[]) => (this.rolesSharedCollection = roles));
  }
}
