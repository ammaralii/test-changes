import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IAddresses, NewAddresses } from '../addresses.model';

export type PartialUpdateAddresses = Partial<IAddresses> & Pick<IAddresses, 'id'>;

export type EntityResponseType = HttpResponse<IAddresses>;
export type EntityArrayResponseType = HttpResponse<IAddresses[]>;

@Injectable({ providedIn: 'root' })
export class AddressesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/addresses', 'testchanges');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/addresses', 'testchanges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(addresses: NewAddresses): Observable<EntityResponseType> {
    return this.http.post<IAddresses>(this.resourceUrl, addresses, { observe: 'response' });
  }

  update(addresses: IAddresses): Observable<EntityResponseType> {
    return this.http.put<IAddresses>(`${this.resourceUrl}/${this.getAddressesIdentifier(addresses)}`, addresses, { observe: 'response' });
  }

  partialUpdate(addresses: PartialUpdateAddresses): Observable<EntityResponseType> {
    return this.http.patch<IAddresses>(`${this.resourceUrl}/${this.getAddressesIdentifier(addresses)}`, addresses, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAddresses>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAddresses[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAddresses[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getAddressesIdentifier(addresses: Pick<IAddresses, 'id'>): number {
    return addresses.id;
  }

  compareAddresses(o1: Pick<IAddresses, 'id'> | null, o2: Pick<IAddresses, 'id'> | null): boolean {
    return o1 && o2 ? this.getAddressesIdentifier(o1) === this.getAddressesIdentifier(o2) : o1 === o2;
  }

  addAddressesToCollectionIfMissing<Type extends Pick<IAddresses, 'id'>>(
    addressesCollection: Type[],
    ...addressesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const addresses: Type[] = addressesToCheck.filter(isPresent);
    if (addresses.length > 0) {
      const addressesCollectionIdentifiers = addressesCollection.map(addressesItem => this.getAddressesIdentifier(addressesItem)!);
      const addressesToAdd = addresses.filter(addressesItem => {
        const addressesIdentifier = this.getAddressesIdentifier(addressesItem);
        if (addressesCollectionIdentifiers.includes(addressesIdentifier)) {
          return false;
        }
        addressesCollectionIdentifiers.push(addressesIdentifier);
        return true;
      });
      return [...addressesToAdd, ...addressesCollection];
    }
    return addressesCollection;
  }
}
