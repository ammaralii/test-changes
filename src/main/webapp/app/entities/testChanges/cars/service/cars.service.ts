import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ICars, NewCars } from '../cars.model';

export type PartialUpdateCars = Partial<ICars> & Pick<ICars, 'id'>;

export type EntityResponseType = HttpResponse<ICars>;
export type EntityArrayResponseType = HttpResponse<ICars[]>;

@Injectable({ providedIn: 'root' })
export class CarsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cars', 'testchanges');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/cars', 'testchanges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(cars: NewCars): Observable<EntityResponseType> {
    return this.http.post<ICars>(this.resourceUrl, cars, { observe: 'response' });
  }

  update(cars: ICars): Observable<EntityResponseType> {
    return this.http.put<ICars>(`${this.resourceUrl}/${this.getCarsIdentifier(cars)}`, cars, { observe: 'response' });
  }

  partialUpdate(cars: PartialUpdateCars): Observable<EntityResponseType> {
    return this.http.patch<ICars>(`${this.resourceUrl}/${this.getCarsIdentifier(cars)}`, cars, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICars>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICars[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICars[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getCarsIdentifier(cars: Pick<ICars, 'id'>): number {
    return cars.id;
  }

  compareCars(o1: Pick<ICars, 'id'> | null, o2: Pick<ICars, 'id'> | null): boolean {
    return o1 && o2 ? this.getCarsIdentifier(o1) === this.getCarsIdentifier(o2) : o1 === o2;
  }

  addCarsToCollectionIfMissing<Type extends Pick<ICars, 'id'>>(
    carsCollection: Type[],
    ...carsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cars: Type[] = carsToCheck.filter(isPresent);
    if (cars.length > 0) {
      const carsCollectionIdentifiers = carsCollection.map(carsItem => this.getCarsIdentifier(carsItem)!);
      const carsToAdd = cars.filter(carsItem => {
        const carsIdentifier = this.getCarsIdentifier(carsItem);
        if (carsCollectionIdentifiers.includes(carsIdentifier)) {
          return false;
        }
        carsCollectionIdentifiers.push(carsIdentifier);
        return true;
      });
      return [...carsToAdd, ...carsCollection];
    }
    return carsCollection;
  }
}
