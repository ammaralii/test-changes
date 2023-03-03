import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ICategories, NewCategories } from '../categories.model';

export type PartialUpdateCategories = Partial<ICategories> & Pick<ICategories, 'id'>;

export type EntityResponseType = HttpResponse<ICategories>;
export type EntityArrayResponseType = HttpResponse<ICategories[]>;

@Injectable({ providedIn: 'root' })
export class CategoriesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/categories', 'testchanges');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/categories', 'testchanges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(categories: NewCategories): Observable<EntityResponseType> {
    return this.http.post<ICategories>(this.resourceUrl, categories, { observe: 'response' });
  }

  update(categories: ICategories): Observable<EntityResponseType> {
    return this.http.put<ICategories>(`${this.resourceUrl}/${this.getCategoriesIdentifier(categories)}`, categories, {
      observe: 'response',
    });
  }

  partialUpdate(categories: PartialUpdateCategories): Observable<EntityResponseType> {
    return this.http.patch<ICategories>(`${this.resourceUrl}/${this.getCategoriesIdentifier(categories)}`, categories, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICategories>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICategories[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICategories[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getCategoriesIdentifier(categories: Pick<ICategories, 'id'>): number {
    return categories.id;
  }

  compareCategories(o1: Pick<ICategories, 'id'> | null, o2: Pick<ICategories, 'id'> | null): boolean {
    return o1 && o2 ? this.getCategoriesIdentifier(o1) === this.getCategoriesIdentifier(o2) : o1 === o2;
  }

  addCategoriesToCollectionIfMissing<Type extends Pick<ICategories, 'id'>>(
    categoriesCollection: Type[],
    ...categoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const categories: Type[] = categoriesToCheck.filter(isPresent);
    if (categories.length > 0) {
      const categoriesCollectionIdentifiers = categoriesCollection.map(categoriesItem => this.getCategoriesIdentifier(categoriesItem)!);
      const categoriesToAdd = categories.filter(categoriesItem => {
        const categoriesIdentifier = this.getCategoriesIdentifier(categoriesItem);
        if (categoriesCollectionIdentifiers.includes(categoriesIdentifier)) {
          return false;
        }
        categoriesCollectionIdentifiers.push(categoriesIdentifier);
        return true;
      });
      return [...categoriesToAdd, ...categoriesCollection];
    }
    return categoriesCollection;
  }
}
