import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAddresses } from '../addresses.model';

@Component({
  selector: 'jhi-addresses-detail',
  templateUrl: './addresses-detail.component.html',
})
export class AddressesDetailComponent implements OnInit {
  addresses: IAddresses | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ addresses }) => {
      this.addresses = addresses;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
