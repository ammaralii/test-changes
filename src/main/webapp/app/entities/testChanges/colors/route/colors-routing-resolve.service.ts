import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IColors } from '../colors.model';
import { ColorsService } from '../service/colors.service';

@Injectable({ providedIn: 'root' })
export class ColorsRoutingResolveService implements Resolve<IColors | null> {
  constructor(protected service: ColorsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IColors | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((colors: HttpResponse<IColors>) => {
          if (colors.body) {
            return of(colors.body);
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
