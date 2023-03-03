import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrders } from '../orders.model';
import { OrdersService } from '../service/orders.service';

@Injectable({ providedIn: 'root' })
export class OrdersRoutingResolveService implements Resolve<IOrders | null> {
  constructor(protected service: OrdersService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrders | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((orders: HttpResponse<IOrders>) => {
          if (orders.body) {
            return of(orders.body);
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
