import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPaymentMethods } from '../payment-methods.model';
import { PaymentMethodsService } from '../service/payment-methods.service';

@Injectable({ providedIn: 'root' })
export class PaymentMethodsRoutingResolveService implements Resolve<IPaymentMethods | null> {
  constructor(protected service: PaymentMethodsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPaymentMethods | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((paymentMethods: HttpResponse<IPaymentMethods>) => {
          if (paymentMethods.body) {
            return of(paymentMethods.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
