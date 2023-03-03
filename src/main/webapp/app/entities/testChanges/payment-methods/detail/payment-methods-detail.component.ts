import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPaymentMethods } from '../payment-methods.model';

@Component({
  selector: 'jhi-payment-methods-detail',
  templateUrl: './payment-methods-detail.component.html',
})
export class PaymentMethodsDetailComponent implements OnInit {
  paymentMethods: IPaymentMethods | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paymentMethods }) => {
      this.paymentMethods = paymentMethods;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
