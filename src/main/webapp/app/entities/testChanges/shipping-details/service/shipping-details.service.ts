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
import { IShippingDetails, NewShippingDetails } from '../shipping-details.model';

export type PartialUpdateShippingDetails = Partial<IShippingDetails> & Pick<IShippingDetails, 'id'>;

type RestOf<T extends IShippingDetails | NewShippingDetails> = Omit<T, 'estimatedDeliveryDate'> & {
  estimatedDeliveryDate?: string | null;
};

export type RestShippingDetails = RestOf<IShippingDetails>;

export type NewRestShippingDetails = RestOf<NewShippingDetails>;

export type PartialUpdateRestShippingDetails = RestOf<PartialUpdateShippingDetails>;

export type EntityResponseType = HttpResponse<IShippingDetails>;
export type EntityArrayResponseType = HttpResponse<IShippingDetails[]>;

@Injectable({ providedIn: 'root' })
export class ShippingDetailsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/shipping-details', 'testchanges');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/shipping-details', 'testchanges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(shippingDetails: NewShippingDetails): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shippingDetails);
    return this.http
      .post<RestShippingDetails>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(shippingDetails: IShippingDetails): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shippingDetails);
    return this.http
      .put<RestShippingDetails>(`${this.resourceUrl}/${this.getShippingDetailsIdentifier(shippingDetails)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(shippingDetails: PartialUpdateShippingDetails): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(shippingDetails);
    return this.http
      .patch<RestShippingDetails>(`${this.resourceUrl}/${this.getShippingDetailsIdentifier(shippingDetails)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestShippingDetails>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestShippingDetails[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestShippingDetails[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getShippingDetailsIdentifier(shippingDetails: Pick<IShippingDetails, 'id'>): number {
    return shippingDetails.id;
  }

  compareShippingDetails(o1: Pick<IShippingDetails, 'id'> | null, o2: Pick<IShippingDetails, 'id'> | null): boolean {
    return o1 && o2 ? this.getShippingDetailsIdentifier(o1) === this.getShippingDetailsIdentifier(o2) : o1 === o2;
  }

  addShippingDetailsToCollectionIfMissing<Type extends Pick<IShippingDetails, 'id'>>(
    shippingDetailsCollection: Type[],
    ...shippingDetailsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const shippingDetails: Type[] = shippingDetailsToCheck.filter(isPresent);
    if (shippingDetails.length > 0) {
      const shippingDetailsCollectionIdentifiers = shippingDetailsCollection.map(
        shippingDetailsItem => this.getShippingDetailsIdentifier(shippingDetailsItem)!
      );
      const shippingDetailsToAdd = shippingDetails.filter(shippingDetailsItem => {
        const shippingDetailsIdentifier = this.getShippingDetailsIdentifier(shippingDetailsItem);
        if (shippingDetailsCollectionIdentifiers.includes(shippingDetailsIdentifier)) {
          return false;
        }
        shippingDetailsCollectionIdentifiers.push(shippingDetailsIdentifier);
        return true;
      });
      return [...shippingDetailsToAdd, ...shippingDetailsCollection];
    }
    return shippingDetailsCollection;
  }

  protected convertDateFromClient<T extends IShippingDetails | NewShippingDetails | PartialUpdateShippingDetails>(
    shippingDetails: T
  ): RestOf<T> {
    return {
      ...shippingDetails,
      estimatedDeliveryDate: shippingDetails.estimatedDeliveryDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restShippingDetails: RestShippingDetails): IShippingDetails {
    return {
      ...restShippingDetails,
      estimatedDeliveryDate: restShippingDetails.estimatedDeliveryDate ? dayjs(restShippingDetails.estimatedDeliveryDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestShippingDetails>): HttpResponse<IShippingDetails> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestShippingDetails[]>): HttpResponse<IShippingDetails[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
