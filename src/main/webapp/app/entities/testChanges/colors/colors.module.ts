import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ColorsComponent } from './list/colors.component';
import { ColorsDetailComponent } from './detail/colors-detail.component';
import { ColorsUpdateComponent } from './update/colors-update.component';
import { ColorsDeleteDialogComponent } from './delete/colors-delete-dialog.component';
import { ColorsRoutingModule } from './route/colors-routing.module';

@NgModule({
  imports: [SharedModule, ColorsRoutingModule],
  declarations: [ColorsComponent, ColorsDetailComponent, ColorsUpdateComponent, ColorsDeleteDialogComponent],
})
export class TestChangesColorsModule {}
