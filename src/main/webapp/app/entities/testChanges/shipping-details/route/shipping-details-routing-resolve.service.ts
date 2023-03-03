import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IShippingDetails } from '../shipping-details.model';
import { ShippingDetailsService } from '../service/shipping-details.service';

@Injectable({ providedIn: 'root' })
export class ShippingDetailsRoutingResolveService implements Resolve<IShippingDetails | null> {
  constructor(protected service: ShippingDetailsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IShippingDetails | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((shippingDetails: HttpResponse<IShippingDetails>) => {
          if (shippingDetails.body) {
            return of(shippingDetails.body);
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
