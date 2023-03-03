import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProductDetails } from '../product-details.model';
import { ProductDetailsService } from '../service/product-details.service';

@Injectable({ providedIn: 'root' })
export class ProductDetailsRoutingResolveService implements Resolve<IProductDetails | null> {
  constructor(protected service: ProductDetailsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProductDetails | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((productDetails: HttpResponse<IProductDetails>) => {
          if (productDetails.body) {
            return of(productDetails.body);
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
