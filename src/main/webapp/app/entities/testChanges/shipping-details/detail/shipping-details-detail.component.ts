import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IShippingDetails } from '../shipping-details.model';

@Component({
  selector: 'jhi-shipping-details-detail',
  templateUrl: './shipping-details-detail.component.html',
})
export class ShippingDetailsDetailComponent implements OnInit {
  shippingDetails: IShippingDetails | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shippingDetails }) => {
      this.shippingDetails = shippingDetails;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
