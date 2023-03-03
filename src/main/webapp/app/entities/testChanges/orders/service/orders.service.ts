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
import { IOrders, NewOrders } from '../orders.model';

export type PartialUpdateOrders = Partial<IOrders> & Pick<IOrders, 'id'>;

type RestOf<T extends IOrders | NewOrders> = Omit<T, 'orderDate'> & {
  orderDate?: string | null;
};

export type RestOrders = RestOf<IOrders>;

export type NewRestOrders = RestOf<NewOrders>;

export type PartialUpdateRestOrders = RestOf<PartialUpdateOrders>;

export type EntityResponseType = HttpResponse<IOrders>;
export type EntityArrayResponseType = HttpResponse<IOrders[]>;

@Injectable({ providedIn: 'root' })
export class OrdersService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/orders', 'testchanges');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/orders', 'testchanges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(orders: NewOrders): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orders);
    return this.http
      .post<RestOrders>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(orders: IOrders): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orders);
    return this.http
      .put<RestOrders>(`${this.resourceUrl}/${this.getOrdersIdentifier(orders)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(orders: PartialUpdateOrders): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orders);
    return this.http
      .patch<RestOrders>(`${this.resourceUrl}/${this.getOrdersIdentifier(orders)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestOrders>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOrders[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOrders[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getOrdersIdentifier(orders: Pick<IOrders, 'id'>): number {
    return orders.id;
  }

  compareOrders(o1: Pick<IOrders, 'id'> | null, o2: Pick<IOrders, 'id'> | null): boolean {
    return o1 && o2 ? this.getOrdersIdentifier(o1) === this.getOrdersIdentifier(o2) : o1 === o2;
  }

  addOrdersToCollectionIfMissing<Type extends Pick<IOrders, 'id'>>(
    ordersCollection: Type[],
    ...ordersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const orders: Type[] = ordersToCheck.filter(isPresent);
    if (orders.length > 0) {
      const ordersCollectionIdentifiers = ordersCollection.map(ordersItem => this.getOrdersIdentifier(ordersItem)!);
      const ordersToAdd = orders.filter(ordersItem => {
        const ordersIdentifier = this.getOrdersIdentifier(ordersItem);
        if (ordersCollectionIdentifiers.includes(ordersIdentifier)) {
          return false;
        }
        ordersCollectionIdentifiers.push(ordersIdentifier);
        return true;
      });
      return [...ordersToAdd, ...ordersCollection];
    }
    return ordersCollection;
  }

  protected convertDateFromClient<T extends IOrders | NewOrders | PartialUpdateOrders>(orders: T): RestOf<T> {
    return {
      ...orders,
      orderDate: orders.orderDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restOrders: RestOrders): IOrders {
    return {
      ...restOrders,
      orderDate: restOrders.orderDate ? dayjs(restOrders.orderDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestOrders>): HttpResponse<IOrders> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOrders[]>): HttpResponse<IOrders[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
