import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ShippingDetailsComponent } from './list/shipping-details.component';
import { ShippingDetailsDetailComponent } from './detail/shipping-details-detail.component';
import { ShippingDetailsUpdateComponent } from './update/shipping-details-update.component';
import { ShippingDetailsDeleteDialogComponent } from './delete/shipping-details-delete-dialog.component';
import { ShippingDetailsRoutingModule } from './route/shipping-details-routing.module';

@NgModule({
  imports: [SharedModule, ShippingDetailsRoutingModule],
  declarations: [
    ShippingDetailsComponent,
    ShippingDetailsDetailComponent,
    ShippingDetailsUpdateComponent,
    ShippingDetailsDeleteDialogComponent,
  ],
})
export class TestChangesShippingDetailsModule {}
