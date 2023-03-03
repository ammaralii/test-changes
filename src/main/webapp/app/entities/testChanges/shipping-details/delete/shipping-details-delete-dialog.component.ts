import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IShippingDetails } from '../shipping-details.model';
import { ShippingDetailsService } from '../service/shipping-details.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './shipping-details-delete-dialog.component.html',
})
export class ShippingDetailsDeleteDialogComponent {
  shippingDetails?: IShippingDetails;

  constructor(protected shippingDetailsService: ShippingDetailsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.shippingDetailsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
