import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOrderDelivery } from '../order-delivery.model';

@Component({
  selector: 'jhi-order-delivery-detail',
  templateUrl: './order-delivery-detail.component.html',
})
export class OrderDeliveryDetailComponent implements OnInit {
  orderDelivery: IOrderDelivery | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderDelivery }) => {
      this.orderDelivery = orderDelivery;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
