import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ColorsComponent } from '../list/colors.component';
import { ColorsDetailComponent } from '../detail/colors-detail.component';
import { ColorsUpdateComponent } from '../update/colors-update.component';
import { ColorsRoutingResolveService } from './colors-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const colorsRoute: Routes = [
  {
    path: '',
    component: ColorsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ColorsDetailComponent,
    resolve: {
      colors: ColorsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ColorsUpdateComponent,
    resolve: {
      colors: ColorsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ColorsUpdateComponent,
    resolve: {
      colors: ColorsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(colorsRoute)],
  exports: [RouterModule],
})
export class ColorsRoutingModule {}
