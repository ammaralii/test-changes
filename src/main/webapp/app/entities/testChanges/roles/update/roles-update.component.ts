import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { RolesFormService, RolesFormGroup } from './roles-form.service';
import { IRoles } from '../roles.model';
import { RolesService } from '../service/roles.service';

@Component({
  selector: 'jhi-roles-update',
  templateUrl: './roles-update.component.html',
})
export class RolesUpdateComponent implements OnInit {
  isSaving = false;
  roles: IRoles | null = null;

  editForm: RolesFormGroup = this.rolesFormService.createRolesFormGroup();

  constructor(
    protected rolesService: RolesService,
    protected rolesFormService: RolesFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ roles }) => {
      this.roles = roles;
      if (roles) {
        this.updateForm(roles);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const roles = this.rolesFormService.getRoles(this.editForm);
    if (roles.id !== null) {
      this.subscribeToSaveResponse(this.rolesService.update(roles));
    } else {
      this.subscribeToSaveResponse(this.rolesService.create(roles));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRoles>>): void {
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

  protected updateForm(roles: IRoles): void {
    this.roles = roles;
    this.rolesFormService.resetForm(this.editForm, roles);
  }
}
