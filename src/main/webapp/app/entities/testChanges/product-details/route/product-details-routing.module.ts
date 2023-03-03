import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProductDetailsComponent } from '../list/product-details.component';
import { ProductDetailsDetailComponent } from '../detail/product-details-detail.component';
import { ProductDetailsUpdateComponent } from '../update/product-details-update.component';
import { ProductDetailsRoutingResolveService } from './product-details-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const productDetailsRoute: Routes = [
  {
    path: '',
    component: ProductDetailsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductDetailsDetailComponent,
    resolve: {
      productDetails: ProductDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductDetailsUpdateComponent,
    resolve: {
      productDetails: ProductDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductDetailsUpdateComponent,
    resolve: {
      productDetails: ProductDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(productDetailsRoute)],
  exports: [RouterModule],
})
export class ProductDetailsRoutingModule {}
