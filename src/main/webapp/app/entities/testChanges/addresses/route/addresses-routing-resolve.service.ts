import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAddresses } from '../addresses.model';
import { AddressesService } from '../service/addresses.service';

@Injectable({ providedIn: 'root' })
export class AddressesRoutingResolveService implements Resolve<IAddresses | null> {
  constructor(protected service: AddressesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAddresses | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((addresses: HttpResponse<IAddresses>) => {
          if (addresses.body) {
            return of(addresses.body);
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
