import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OrderDeliveryComponent } from '../list/order-delivery.component';
import { OrderDeliveryDetailComponent } from '../detail/order-delivery-detail.component';
import { OrderDeliveryUpdateComponent } from '../update/order-delivery-update.component';
import { OrderDeliveryRoutingResolveService } from './order-delivery-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const orderDeliveryRoute: Routes = [
  {
    path: '',
    component: OrderDeliveryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OrderDeliveryDetailComponent,
    resolve: {
      orderDelivery: OrderDeliveryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OrderDeliveryUpdateComponent,
    resolve: {
      orderDelivery: OrderDeliveryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OrderDeliveryUpdateComponent,
    resolve: {
      orderDelivery: OrderDeliveryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(orderDeliveryRoute)],
  exports: [RouterModule],
})
export class OrderDeliveryRoutingModule {}
