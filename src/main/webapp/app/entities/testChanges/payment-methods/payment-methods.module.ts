import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PaymentMethodsComponent } from './list/payment-methods.component';
import { PaymentMethodsDetailComponent } from './detail/payment-methods-detail.component';
import { PaymentMethodsUpdateComponent } from './update/payment-methods-update.component';
import { PaymentMethodsDeleteDialogComponent } from './delete/payment-methods-delete-dialog.component';
import { PaymentMethodsRoutingModule } from './route/payment-methods-routing.module';

@NgModule({
  imports: [SharedModule, PaymentMethodsRoutingModule],
  declarations: [
    PaymentMethodsComponent,
    PaymentMethodsDetailComponent,
    PaymentMethodsUpdateComponent,
    PaymentMethodsDeleteDialogComponent,
  ],
})
export class TestChangesPaymentMethodsModule {}
