import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DarazUsersComponent } from './list/daraz-users.component';
import { DarazUsersDetailComponent } from './detail/daraz-users-detail.component';
import { DarazUsersUpdateComponent } from './update/daraz-users-update.component';
import { DarazUsersDeleteDialogComponent } from './delete/daraz-users-delete-dialog.component';
import { DarazUsersRoutingModule } from './route/daraz-users-routing.module';

@NgModule({
  imports: [SharedModule, DarazUsersRoutingModule],
  declarations: [DarazUsersComponent, DarazUsersDetailComponent, DarazUsersUpdateComponent, DarazUsersDeleteDialogComponent],
})
export class TestChangesDarazUsersModule {}
