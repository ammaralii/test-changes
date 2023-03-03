import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICars } from '../cars.model';
import { CarsService } from '../service/cars.service';

@Injectable({ providedIn: 'root' })
export class CarsRoutingResolveService implements Resolve<ICars | null> {
  constructor(protected service: CarsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICars | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cars: HttpResponse<ICars>) => {
          if (cars.body) {
            return of(cars.body);
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
