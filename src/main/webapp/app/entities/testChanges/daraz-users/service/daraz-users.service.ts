import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IDarazUsers, NewDarazUsers } from '../daraz-users.model';

export type PartialUpdateDarazUsers = Partial<IDarazUsers> & Pick<IDarazUsers, 'id'>;

export type EntityResponseType = HttpResponse<IDarazUsers>;
export type EntityArrayResponseType = HttpResponse<IDarazUsers[]>;

@Injectable({ providedIn: 'root' })
export class DarazUsersService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/daraz-users', 'testchanges');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/daraz-users', 'testchanges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(darazUsers: NewDarazUsers): Observable<EntityResponseType> {
    return this.http.post<IDarazUsers>(this.resourceUrl, darazUsers, { observe: 'response' });
  }

  update(darazUsers: IDarazUsers): Observable<EntityResponseType> {
    return this.http.put<IDarazUsers>(`${this.resourceUrl}/${this.getDarazUsersIdentifier(darazUsers)}`, darazUsers, {
      observe: 'response',
    });
  }

  partialUpdate(darazUsers: PartialUpdateDarazUsers): Observable<EntityResponseType> {
    return this.http.patch<IDarazUsers>(`${this.resourceUrl}/${this.getDarazUsersIdentifier(darazUsers)}`, darazUsers, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDarazUsers>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDarazUsers[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDarazUsers[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getDarazUsersIdentifier(darazUsers: Pick<IDarazUsers, 'id'>): number {
    return darazUsers.id;
  }

  compareDarazUsers(o1: Pick<IDarazUsers, 'id'> | null, o2: Pick<IDarazUsers, 'id'> | null): boolean {
    return o1 && o2 ? this.getDarazUsersIdentifier(o1) === this.getDarazUsersIdentifier(o2) : o1 === o2;
  }

  addDarazUsersToCollectionIfMissing<Type extends Pick<IDarazUsers, 'id'>>(
    darazUsersCollection: Type[],
    ...darazUsersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const darazUsers: Type[] = darazUsersToCheck.filter(isPresent);
    if (darazUsers.length > 0) {
      const darazUsersCollectionIdentifiers = darazUsersCollection.map(darazUsersItem => this.getDarazUsersIdentifier(darazUsersItem)!);
      const darazUsersToAdd = darazUsers.filter(darazUsersItem => {
        const darazUsersIdentifier = this.getDarazUsersIdentifier(darazUsersItem);
        if (darazUsersCollectionIdentifiers.includes(darazUsersIdentifier)) {
          return false;
        }
        darazUsersCollectionIdentifiers.push(darazUsersIdentifier);
        return true;
      });
      return [...darazUsersToAdd, ...darazUsersCollection];
    }
    return darazUsersCollection;
  }
}
