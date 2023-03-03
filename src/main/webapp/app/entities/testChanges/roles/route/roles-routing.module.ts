import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RolesComponent } from '../list/roles.component';
import { RolesDetailComponent } from '../detail/roles-detail.component';
import { RolesUpdateComponent } from '../update/roles-update.component';
import { RolesRoutingResolveService } from './roles-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const rolesRoute: Routes = [
  {
    path: '',
    component: RolesComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RolesDetailComponent,
    resolve: {
      roles: RolesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RolesUpdateComponent,
    resolve: {
      roles: RolesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RolesUpdateComponent,
    resolve: {
      roles: RolesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(rolesRoute)],
  exports: [RouterModule],
})
export class RolesRoutingModule {}
