import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrdersComponent } from './list/orders.component';
import { OrdersDetailComponent } from './detail/orders-detail.component';
import { OrdersUpdateComponent } from './update/orders-update.component';
import { OrdersDeleteDialogComponent } from './delete/orders-delete-dialog.component';
import { OrdersRoutingModule } from './route/orders-routing.module';

@NgModule({
  imports: [SharedModule, OrdersRoutingModule],
  declarations: [OrdersComponent, OrdersDetailComponent, OrdersUpdateComponent, OrdersDeleteDialogComponent],
})
export class TestChangesOrdersModule {}
