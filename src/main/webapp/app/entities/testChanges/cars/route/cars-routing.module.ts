import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CarsComponent } from '../list/cars.component';
import { CarsDetailComponent } from '../detail/cars-detail.component';
import { CarsUpdateComponent } from '../update/cars-update.component';
import { CarsRoutingResolveService } from './cars-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const carsRoute: Routes = [
  {
    path: '',
    component: CarsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CarsDetailComponent,
    resolve: {
      cars: CarsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CarsUpdateComponent,
    resolve: {
      cars: CarsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CarsUpdateComponent,
    resolve: {
      cars: CarsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(carsRoute)],
  exports: [RouterModule],
})
export class CarsRoutingModule {}
