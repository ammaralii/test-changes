import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDarazUsers } from '../daraz-users.model';
import { DarazUsersService } from '../service/daraz-users.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './daraz-users-delete-dialog.component.html',
})
export class DarazUsersDeleteDialogComponent {
  darazUsers?: IDarazUsers;

  constructor(protected darazUsersService: DarazUsersService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.darazUsersService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
