import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IRoles, NewRoles } from '../roles.model';

export type PartialUpdateRoles = Partial<IRoles> & Pick<IRoles, 'id'>;

export type EntityResponseType = HttpResponse<IRoles>;
export type EntityArrayResponseType = HttpResponse<IRoles[]>;

@Injectable({ providedIn: 'root' })
export class RolesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/roles', 'testchanges');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/roles', 'testchanges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(roles: NewRoles): Observable<EntityResponseType> {
    return this.http.post<IRoles>(this.resourceUrl, roles, { observe: 'response' });
  }

  update(roles: IRoles): Observable<EntityResponseType> {
    return this.http.put<IRoles>(`${this.resourceUrl}/${this.getRolesIdentifier(roles)}`, roles, { observe: 'response' });
  }

  partialUpdate(roles: PartialUpdateRoles): Observable<EntityResponseType> {
    return this.http.patch<IRoles>(`${this.resourceUrl}/${this.getRolesIdentifier(roles)}`, roles, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRoles>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRoles[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRoles[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getRolesIdentifier(roles: Pick<IRoles, 'id'>): number {
    return roles.id;
  }

  compareRoles(o1: Pick<IRoles, 'id'> | null, o2: Pick<IRoles, 'id'> | null): boolean {
    return o1 && o2 ? this.getRolesIdentifier(o1) === this.getRolesIdentifier(o2) : o1 === o2;
  }

  addRolesToCollectionIfMissing<Type extends Pick<IRoles, 'id'>>(
    rolesCollection: Type[],
    ...rolesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const roles: Type[] = rolesToCheck.filter(isPresent);
    if (roles.length > 0) {
      const rolesCollectionIdentifiers = rolesCollection.map(rolesItem => this.getRolesIdentifier(rolesItem)!);
      const rolesToAdd = roles.filter(rolesItem => {
        const rolesIdentifier = this.getRolesIdentifier(rolesItem);
        if (rolesCollectionIdentifiers.includes(rolesIdentifier)) {
          return false;
        }
        rolesCollectionIdentifiers.push(rolesIdentifier);
        return true;
      });
      return [...rolesToAdd, ...rolesCollection];
    }
    return rolesCollection;
  }
}
