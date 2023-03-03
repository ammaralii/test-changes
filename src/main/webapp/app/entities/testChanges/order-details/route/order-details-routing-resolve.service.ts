import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrderDetails } from '../order-details.model';
import { OrderDetailsService } from '../service/order-details.service';

@Injectable({ providedIn: 'root' })
export class OrderDetailsRoutingResolveService implements Resolve<IOrderDetails | null> {
  constructor(protected service: OrderDetailsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrderDetails | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((orderDetails: HttpResponse<IOrderDetails>) => {
          if (orderDetails.body) {
            return of(orderDetails.body);
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
