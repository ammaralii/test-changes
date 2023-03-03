import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDarazUsers } from '../daraz-users.model';
import { DarazUsersService } from '../service/daraz-users.service';

@Injectable({ providedIn: 'root' })
export class DarazUsersRoutingResolveService implements Resolve<IDarazUsers | null> {
  constructor(protected service: DarazUsersService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDarazUsers | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((darazUsers: HttpResponse<IDarazUsers>) => {
          if (darazUsers.body) {
            return of(darazUsers.body);
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
