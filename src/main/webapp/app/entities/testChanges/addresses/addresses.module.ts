import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AddressesComponent } from './list/addresses.component';
import { AddressesDetailComponent } from './detail/addresses-detail.component';
import { AddressesUpdateComponent } from './update/addresses-update.component';
import { AddressesDeleteDialogComponent } from './delete/addresses-delete-dialog.component';
import { AddressesRoutingModule } from './route/addresses-routing.module';

@NgModule({
  imports: [SharedModule, AddressesRoutingModule],
  declarations: [AddressesComponent, AddressesDetailComponent, AddressesUpdateComponent, AddressesDeleteDialogComponent],
})
export class TestChangesAddressesModule {}
