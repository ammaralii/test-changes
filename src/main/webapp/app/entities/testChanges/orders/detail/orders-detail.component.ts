import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOrders } from '../orders.model';

@Component({
  selector: 'jhi-orders-detail',
  templateUrl: './orders-detail.component.html',
})
export class OrdersDetailComponent implements OnInit {
  orders: IOrders | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orders }) => {
      this.orders = orders;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
