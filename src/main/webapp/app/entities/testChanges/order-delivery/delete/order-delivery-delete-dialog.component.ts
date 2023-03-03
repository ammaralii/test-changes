import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrderDelivery } from '../order-delivery.model';
import { OrderDeliveryService } from '../service/order-delivery.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './order-delivery-delete-dialog.component.html',
})
export class OrderDeliveryDeleteDialogComponent {
  orderDelivery?: IOrderDelivery;

  constructor(protected orderDeliveryService: OrderDeliveryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.orderDeliveryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
