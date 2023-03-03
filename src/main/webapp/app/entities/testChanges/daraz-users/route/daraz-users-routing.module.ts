import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DarazUsersComponent } from '../list/daraz-users.component';
import { DarazUsersDetailComponent } from '../detail/daraz-users-detail.component';
import { DarazUsersUpdateComponent } from '../update/daraz-users-update.component';
import { DarazUsersRoutingResolveService } from './daraz-users-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const darazUsersRoute: Routes = [
  {
    path: '',
    component: DarazUsersComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DarazUsersDetailComponent,
    resolve: {
      darazUsers: DarazUsersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DarazUsersUpdateComponent,
    resolve: {
      darazUsers: DarazUsersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DarazUsersUpdateComponent,
    resolve: {
      darazUsers: DarazUsersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(darazUsersRoute)],
  exports: [RouterModule],
})
export class DarazUsersRoutingModule {}
