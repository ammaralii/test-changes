import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProductDetailsComponent } from './list/product-details.component';
import { ProductDetailsDetailComponent } from './detail/product-details-detail.component';
import { ProductDetailsUpdateComponent } from './update/product-details-update.component';
import { ProductDetailsDeleteDialogComponent } from './delete/product-details-delete-dialog.component';
import { ProductDetailsRoutingModule } from './route/product-details-routing.module';

@NgModule({
  imports: [SharedModule, ProductDetailsRoutingModule],
  declarations: [
    ProductDetailsComponent,
    ProductDetailsDetailComponent,
    ProductDetailsUpdateComponent,
    ProductDetailsDeleteDialogComponent,
  ],
})
export class TestChangesProductDetailsModule {}
