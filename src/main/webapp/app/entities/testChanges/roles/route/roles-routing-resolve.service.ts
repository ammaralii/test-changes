import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRoles } from '../roles.model';
import { RolesService } from '../service/roles.service';

@Injectable({ providedIn: 'root' })
export class RolesRoutingResolveService implements Resolve<IRoles | null> {
  constructor(protected service: RolesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRoles | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((roles: HttpResponse<IRoles>) => {
          if (roles.body) {
            return of(roles.body);
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
