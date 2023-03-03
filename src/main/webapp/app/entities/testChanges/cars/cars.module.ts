import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CarsComponent } from './list/cars.component';
import { CarsDetailComponent } from './detail/cars-detail.component';
import { CarsUpdateComponent } from './update/cars-update.component';
import { CarsDeleteDialogComponent } from './delete/cars-delete-dialog.component';
import { CarsRoutingModule } from './route/cars-routing.module';

@NgModule({
  imports: [SharedModule, CarsRoutingModule],
  declarations: [CarsComponent, CarsDetailComponent, CarsUpdateComponent, CarsDeleteDialogComponent],
})
export class TestChangesCarsModule {}
