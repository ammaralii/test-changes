import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRoles } from '../roles.model';

@Component({
  selector: 'jhi-roles-detail',
  templateUrl: './roles-detail.component.html',
})
export class RolesDetailComponent implements OnInit {
  roles: IRoles | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ roles }) => {
      this.roles = roles;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
