import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrderDeliveryComponent } from './list/order-delivery.component';
import { OrderDeliveryDetailComponent } from './detail/order-delivery-detail.component';
import { OrderDeliveryUpdateComponent } from './update/order-delivery-update.component';
import { OrderDeliveryDeleteDialogComponent } from './delete/order-delivery-delete-dialog.component';
import { OrderDeliveryRoutingModule } from './route/order-delivery-routing.module';

@NgModule({
  imports: [SharedModule, OrderDeliveryRoutingModule],
  declarations: [OrderDeliveryComponent, OrderDeliveryDetailComponent, OrderDeliveryUpdateComponent, OrderDeliveryDeleteDialogComponent],
})
export class TestChangesOrderDeliveryModule {}
