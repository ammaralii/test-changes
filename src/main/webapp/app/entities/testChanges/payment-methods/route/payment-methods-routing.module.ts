import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PaymentMethodsComponent } from '../list/payment-methods.component';
import { PaymentMethodsDetailComponent } from '../detail/payment-methods-detail.component';
import { PaymentMethodsUpdateComponent } from '../update/payment-methods-update.component';
import { PaymentMethodsRoutingResolveService } from './payment-methods-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const paymentMethodsRoute: Routes = [
  {
    path: '',
    component: PaymentMethodsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PaymentMethodsDetailComponent,
    resolve: {
      paymentMethods: PaymentMethodsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PaymentMethodsUpdateComponent,
    resolve: {
      paymentMethods: PaymentMethodsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PaymentMethodsUpdateComponent,
    resolve: {
      paymentMethods: PaymentMethodsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(paymentMethodsRoute)],
  exports: [RouterModule],
})
export class PaymentMethodsRoutingModule {}
