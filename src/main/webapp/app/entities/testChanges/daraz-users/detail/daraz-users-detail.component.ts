import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDarazUsers } from '../daraz-users.model';

@Component({
  selector: 'jhi-daraz-users-detail',
  templateUrl: './daraz-users-detail.component.html',
})
export class DarazUsersDetailComponent implements OnInit {
  darazUsers: IDarazUsers | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ darazUsers }) => {
      this.darazUsers = darazUsers;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
