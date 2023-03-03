import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPaymentMethods, NewPaymentMethods } from '../payment-methods.model';

export type PartialUpdatePaymentMethods = Partial<IPaymentMethods> & Pick<IPaymentMethods, 'id'>;

export type EntityResponseType = HttpResponse<IPaymentMethods>;
export type EntityArrayResponseType = HttpResponse<IPaymentMethods[]>;

@Injectable({ providedIn: 'root' })
export class PaymentMethodsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/payment-methods', 'testchanges');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/payment-methods', 'testchanges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(paymentMethods: NewPaymentMethods): Observable<EntityResponseType> {
    return this.http.post<IPaymentMethods>(this.resourceUrl, paymentMethods, { observe: 'response' });
  }

  update(paymentMethods: IPaymentMethods): Observable<EntityResponseType> {
    return this.http.put<IPaymentMethods>(`${this.resourceUrl}/${this.getPaymentMethodsIdentifier(paymentMethods)}`, paymentMethods, {
      observe: 'response',
    });
  }

  partialUpdate(paymentMethods: PartialUpdatePaymentMethods): Observable<EntityResponseType> {
    return this.http.patch<IPaymentMethods>(`${this.resourceUrl}/${this.getPaymentMethodsIdentifier(paymentMethods)}`, paymentMethods, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPaymentMethods>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPaymentMethods[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPaymentMethods[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getPaymentMethodsIdentifier(paymentMethods: Pick<IPaymentMethods, 'id'>): number {
    return paymentMethods.id;
  }

  comparePaymentMethods(o1: Pick<IPaymentMethods, 'id'> | null, o2: Pick<IPaymentMethods, 'id'> | null): boolean {
    return o1 && o2 ? this.getPaymentMethodsIdentifier(o1) === this.getPaymentMethodsIdentifier(o2) : o1 === o2;
  }

  addPaymentMethodsToCollectionIfMissing<Type extends Pick<IPaymentMethods, 'id'>>(
    paymentMethodsCollection: Type[],
    ...paymentMethodsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const paymentMethods: Type[] = paymentMethodsToCheck.filter(isPresent);
    if (paymentMethods.length > 0) {
      const paymentMethodsCollectionIdentifiers = paymentMethodsCollection.map(
        paymentMethodsItem => this.getPaymentMethodsIdentifier(paymentMethodsItem)!
      );
      const paymentMethodsToAdd = paymentMethods.filter(paymentMethodsItem => {
        const paymentMethodsIdentifier = this.getPaymentMethodsIdentifier(paymentMethodsItem);
        if (paymentMethodsCollectionIdentifiers.includes(paymentMethodsIdentifier)) {
          return false;
        }
        paymentMethodsCollectionIdentifiers.push(paymentMethodsIdentifier);
        return true;
      });
      return [...paymentMethodsToAdd, ...paymentMethodsCollection];
    }
    return paymentMethodsCollection;
  }
}
