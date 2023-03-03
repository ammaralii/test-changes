import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IOrderDetails, NewOrderDetails } from '../order-details.model';

export type PartialUpdateOrderDetails = Partial<IOrderDetails> & Pick<IOrderDetails, 'id'>;

export type EntityResponseType = HttpResponse<IOrderDetails>;
export type EntityArrayResponseType = HttpResponse<IOrderDetails[]>;

@Injectable({ providedIn: 'root' })
export class OrderDetailsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/order-details', 'testchanges');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/order-details', 'testchanges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(orderDetails: NewOrderDetails): Observable<EntityResponseType> {
    return this.http.post<IOrderDetails>(this.resourceUrl, orderDetails, { observe: 'response' });
  }

  update(orderDetails: IOrderDetails): Observable<EntityResponseType> {
    return this.http.put<IOrderDetails>(`${this.resourceUrl}/${this.getOrderDetailsIdentifier(orderDetails)}`, orderDetails, {
      observe: 'response',
    });
  }

  partialUpdate(orderDetails: PartialUpdateOrderDetails): Observable<EntityResponseType> {
    return this.http.patch<IOrderDetails>(`${this.resourceUrl}/${this.getOrderDetailsIdentifier(orderDetails)}`, orderDetails, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IOrderDetails>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrderDetails[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrderDetails[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getOrderDetailsIdentifier(orderDetails: Pick<IOrderDetails, 'id'>): number {
    return orderDetails.id;
  }

  compareOrderDetails(o1: Pick<IOrderDetails, 'id'> | null, o2: Pick<IOrderDetails, 'id'> | null): boolean {
    return o1 && o2 ? this.getOrderDetailsIdentifier(o1) === this.getOrderDetailsIdentifier(o2) : o1 === o2;
  }

  addOrderDetailsToCollectionIfMissing<Type extends Pick<IOrderDetails, 'id'>>(
    orderDetailsCollection: Type[],
    ...orderDetailsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const orderDetails: Type[] = orderDetailsToCheck.filter(isPresent);
    if (orderDetails.length > 0) {
      const orderDetailsCollectionIdentifiers = orderDetailsCollection.map(
        orderDetailsItem => this.getOrderDetailsIdentifier(orderDetailsItem)!
      );
      const orderDetailsToAdd = orderDetails.filter(orderDetailsItem => {
        const orderDetailsIdentifier = this.getOrderDetailsIdentifier(orderDetailsItem);
        if (orderDetailsCollectionIdentifiers.includes(orderDetailsIdentifier)) {
          return false;
        }
        orderDetailsCollectionIdentifiers.push(orderDetailsIdentifier);
        return true;
      });
      return [...orderDetailsToAdd, ...orderDetailsCollection];
    }
    return orderDetailsCollection;
  }
}
