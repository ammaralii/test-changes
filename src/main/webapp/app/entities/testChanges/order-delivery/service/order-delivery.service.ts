import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IOrderDelivery, NewOrderDelivery } from '../order-delivery.model';

export type PartialUpdateOrderDelivery = Partial<IOrderDelivery> & Pick<IOrderDelivery, 'id'>;

type RestOf<T extends IOrderDelivery | NewOrderDelivery> = Omit<T, 'deliveryDate'> & {
  deliveryDate?: string | null;
};

export type RestOrderDelivery = RestOf<IOrderDelivery>;

export type NewRestOrderDelivery = RestOf<NewOrderDelivery>;

export type PartialUpdateRestOrderDelivery = RestOf<PartialUpdateOrderDelivery>;

export type EntityResponseType = HttpResponse<IOrderDelivery>;
export type EntityArrayResponseType = HttpResponse<IOrderDelivery[]>;

@Injectable({ providedIn: 'root' })
export class OrderDeliveryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/order-deliveries', 'testchanges');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/order-deliveries', 'testchanges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(orderDelivery: NewOrderDelivery): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orderDelivery);
    return this.http
      .post<RestOrderDelivery>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(orderDelivery: IOrderDelivery): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orderDelivery);
    return this.http
      .put<RestOrderDelivery>(`${this.resourceUrl}/${this.getOrderDeliveryIdentifier(orderDelivery)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(orderDelivery: PartialUpdateOrderDelivery): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orderDelivery);
    return this.http
      .patch<RestOrderDelivery>(`${this.resourceUrl}/${this.getOrderDeliveryIdentifier(orderDelivery)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestOrderDelivery>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOrderDelivery[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOrderDelivery[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getOrderDeliveryIdentifier(orderDelivery: Pick<IOrderDelivery, 'id'>): number {
    return orderDelivery.id;
  }

  compareOrderDelivery(o1: Pick<IOrderDelivery, 'id'> | null, o2: Pick<IOrderDelivery, 'id'> | null): boolean {
    return o1 && o2 ? this.getOrderDeliveryIdentifier(o1) === this.getOrderDeliveryIdentifier(o2) : o1 === o2;
  }

  addOrderDeliveryToCollectionIfMissing<Type extends Pick<IOrderDelivery, 'id'>>(
    orderDeliveryCollection: Type[],
    ...orderDeliveriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const orderDeliveries: Type[] = orderDeliveriesToCheck.filter(isPresent);
    if (orderDeliveries.length > 0) {
      const orderDeliveryCollectionIdentifiers = orderDeliveryCollection.map(
        orderDeliveryItem => this.getOrderDeliveryIdentifier(orderDeliveryItem)!
      );
      const orderDeliveriesToAdd = orderDeliveries.filter(orderDeliveryItem => {
        const orderDeliveryIdentifier = this.getOrderDeliveryIdentifier(orderDeliveryItem);
        if (orderDeliveryCollectionIdentifiers.includes(orderDeliveryIdentifier)) {
          return false;
        }
        orderDeliveryCollectionIdentifiers.push(orderDeliveryIdentifier);
        return true;
      });
      return [...orderDeliveriesToAdd, ...orderDeliveryCollection];
    }
    return orderDeliveryCollection;
  }

  protected convertDateFromClient<T extends IOrderDelivery | NewOrderDelivery | PartialUpdateOrderDelivery>(orderDelivery: T): RestOf<T> {
    return {
      ...orderDelivery,
      deliveryDate: orderDelivery.deliveryDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restOrderDelivery: RestOrderDelivery): IOrderDelivery {
    return {
      ...restOrderDelivery,
      deliveryDate: restOrderDelivery.deliveryDate ? dayjs(restOrderDelivery.deliveryDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestOrderDelivery>): HttpResponse<IOrderDelivery> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOrderDelivery[]>): HttpResponse<IOrderDelivery[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
