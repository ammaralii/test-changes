import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AddressesComponent } from '../list/addresses.component';
import { AddressesDetailComponent } from '../detail/addresses-detail.component';
import { AddressesUpdateComponent } from '../update/addresses-update.component';
import { AddressesRoutingResolveService } from './addresses-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const addressesRoute: Routes = [
  {
    path: '',
    component: AddressesComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AddressesDetailComponent,
    resolve: {
      addresses: AddressesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AddressesUpdateComponent,
    resolve: {
      addresses: AddressesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AddressesUpdateComponent,
    resolve: {
      addresses: AddressesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(addressesRoute)],
  exports: [RouterModule],
})
export class AddressesRoutingModule {}
