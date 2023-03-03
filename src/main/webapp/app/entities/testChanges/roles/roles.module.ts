import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RolesComponent } from './list/roles.component';
import { RolesDetailComponent } from './detail/roles-detail.component';
import { RolesUpdateComponent } from './update/roles-update.component';
import { RolesDeleteDialogComponent } from './delete/roles-delete-dialog.component';
import { RolesRoutingModule } from './route/roles-routing.module';

@NgModule({
  imports: [SharedModule, RolesRoutingModule],
  declarations: [RolesComponent, RolesDetailComponent, RolesUpdateComponent, RolesDeleteDialogComponent],
})
export class TestChangesRolesModule {}
