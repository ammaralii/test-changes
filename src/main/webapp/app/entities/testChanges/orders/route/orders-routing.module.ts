import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OrdersComponent } from '../list/orders.component';
import { OrdersDetailComponent } from '../detail/orders-detail.component';
import { OrdersUpdateComponent } from '../update/orders-update.component';
import { OrdersRoutingResolveService } from './orders-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const ordersRoute: Routes = [
  {
    path: '',
    component: OrdersComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OrdersDetailComponent,
    resolve: {
      orders: OrdersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OrdersUpdateComponent,
    resolve: {
      orders: OrdersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OrdersUpdateComponent,
    resolve: {
      orders: OrdersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(ordersRoute)],
  exports: [RouterModule],
})
export class OrdersRoutingModule {}
