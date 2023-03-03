import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrderDelivery } from '../order-delivery.model';
import { OrderDeliveryService } from '../service/order-delivery.service';

@Injectable({ providedIn: 'root' })
export class OrderDeliveryRoutingResolveService implements Resolve<IOrderDelivery | null> {
  constructor(protected service: OrderDeliveryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrderDelivery | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((orderDelivery: HttpResponse<IOrderDelivery>) => {
          if (orderDelivery.body) {
            return of(orderDelivery.body);
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
