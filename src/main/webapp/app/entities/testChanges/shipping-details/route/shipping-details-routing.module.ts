import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ShippingDetailsComponent } from '../list/shipping-details.component';
import { ShippingDetailsDetailComponent } from '../detail/shipping-details-detail.component';
import { ShippingDetailsUpdateComponent } from '../update/shipping-details-update.component';
import { ShippingDetailsRoutingResolveService } from './shipping-details-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const shippingDetailsRoute: Routes = [
  {
    path: '',
    component: ShippingDetailsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ShippingDetailsDetailComponent,
    resolve: {
      shippingDetails: ShippingDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ShippingDetailsUpdateComponent,
    resolve: {
      shippingDetails: ShippingDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ShippingDetailsUpdateComponent,
    resolve: {
      shippingDetails: ShippingDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(shippingDetailsRoute)],
  exports: [RouterModule],
})
export class ShippingDetailsRoutingModule {}
