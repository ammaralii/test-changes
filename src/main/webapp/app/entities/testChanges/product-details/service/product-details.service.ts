import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProductDetails, NewProductDetails } from '../product-details.model';

export type PartialUpdateProductDetails = Partial<IProductDetails> & Pick<IProductDetails, 'id'>;

export type EntityResponseType = HttpResponse<IProductDetails>;
export type EntityArrayResponseType = HttpResponse<IProductDetails[]>;

@Injectable({ providedIn: 'root' })
export class ProductDetailsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/product-details', 'testchanges');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/product-details', 'testchanges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(productDetails: NewProductDetails): Observable<EntityResponseType> {
    return this.http.post<IProductDetails>(this.resourceUrl, productDetails, { observe: 'response' });
  }

  update(productDetails: IProductDetails): Observable<EntityResponseType> {
    return this.http.put<IProductDetails>(`${this.resourceUrl}/${this.getProductDetailsIdentifier(productDetails)}`, productDetails, {
      observe: 'response',
    });
  }

  partialUpdate(productDetails: PartialUpdateProductDetails): Observable<EntityResponseType> {
    return this.http.patch<IProductDetails>(`${this.resourceUrl}/${this.getProductDetailsIdentifier(productDetails)}`, productDetails, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProductDetails>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductDetails[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProductDetails[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getProductDetailsIdentifier(productDetails: Pick<IProductDetails, 'id'>): number {
    return productDetails.id;
  }

  compareProductDetails(o1: Pick<IProductDetails, 'id'> | null, o2: Pick<IProductDetails, 'id'> | null): boolean {
    return o1 && o2 ? this.getProductDetailsIdentifier(o1) === this.getProductDetailsIdentifier(o2) : o1 === o2;
  }

  addProductDetailsToCollectionIfMissing<Type extends Pick<IProductDetails, 'id'>>(
    productDetailsCollection: Type[],
    ...productDetailsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const productDetails: Type[] = productDetailsToCheck.filter(isPresent);
    if (productDetails.length > 0) {
      const productDetailsCollectionIdentifiers = productDetailsCollection.map(
        productDetailsItem => this.getProductDetailsIdentifier(productDetailsItem)!
      );
      const productDetailsToAdd = productDetails.filter(productDetailsItem => {
        const productDetailsIdentifier = this.getProductDetailsIdentifier(productDetailsItem);
        if (productDetailsCollectionIdentifiers.includes(productDetailsIdentifier)) {
          return false;
        }
        productDetailsCollectionIdentifiers.push(productDetailsIdentifier);
        return true;
      });
      return [...productDetailsToAdd, ...productDetailsCollection];
    }
    return productDetailsCollection;
  }
}
